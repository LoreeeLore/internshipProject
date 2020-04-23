package com.studlabs.quiz.controller;

import com.studlabs.quiz.dto.*;
import com.studlabs.quiz.mapper.*;
import com.studlabs.quiz.model.*;
import com.studlabs.quiz.security.*;
import com.studlabs.quiz.service.*;
import io.swagger.annotations.*;
import org.apache.logging.log4j.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.*;

@Api(value = "quiz rating controller")
@RestController
@RequestMapping("/quizRatings")
public class QuizRatingController {

    private static final Logger LOGGER = LogManager.getLogger(QuizRatingController.class);
    private QuizRatingService quizRatingService;
    private QuizRatingMapper quizRatingMapper;

    public QuizRatingController(QuizRatingService quizRatingService) {
        this.quizRatingService = quizRatingService;
        this.quizRatingMapper = new QuizRatingMapper();
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "') or hasRole('" + Roles.ROLE_MENTOR + "')")
    @ApiOperation(value = "Find all ratings for a quiz")
    @GetMapping("/")
    public ResponseEntity<List<QuizRatingDto>> findAll() {
        LOGGER.info("Finding all quiz rating");

        List<QuizRating> quizRatings = quizRatingService.findAll();
        List<QuizRatingDto> quizRatingsDTO;

        quizRatingsDTO = quizRatings.stream().map(quizRating -> quizRatingMapper.convertToDTO(quizRating)).collect(Collectors.toList());

        LOGGER.info("Returning {} ratings", quizRatings.size());
        return new ResponseEntity<>(quizRatingsDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_USER + "') or hasRole('" + Roles.ROLE_MENTOR + "') or hasRole('" + Roles.ROLE_ADMINISTRATOR + "')")
    @ApiOperation(value = "Insert a rating for a quiz")
    @PostMapping("/")
    public ResponseEntity<String> insertQuizRating(@RequestBody QuizRatingDto quizRating) {
        LOGGER.info("Inserting rating for quiz {}", quizRating.getIdQuiz());

        if (quizRatingService.exists(quizRating.getIdUser(), quizRating.getIdQuiz())) {
            LOGGER.info("Quiz {} already rated", quizRating.getIdQuiz());
            return new ResponseEntity<>("This quiz is already rated", HttpStatus.BAD_REQUEST);
        }

        quizRatingService.insertQuizRating(quizRatingMapper.convertToEntity(quizRating));

        LOGGER.info("Quiz {} rated successfully", quizRating.getIdQuiz());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_USER + "') or hasRole('" + Roles.ROLE_MENTOR + "') or hasRole('" + Roles.ROLE_ADMINISTRATOR + "')")
    @ApiOperation(value = "Update rating for a quiz")
    @PutMapping(value = "/{idQuiz}")
    public ResponseEntity<Void> updateQuizRating(@PathVariable("idQuiz") int idQuiz,
                                                 @RequestBody QuizRatingDto quizRating) {
        String idUser = SecurityUtil.getUsername();

        LOGGER.info("Update rating for quiz " + idQuiz);

        if (!quizRatingService.exists(idUser, idQuiz)) {
            LOGGER.info("Quiz {} doesn't exist for user {} ", idQuiz, idUser);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        quizRating.setIdQuiz(idQuiz);
        quizRating.setIdUser(idUser);

        quizRatingService.updateQuizRating(quizRatingMapper.convertToEntity(quizRating));

        LOGGER.info("Quiz {} updated successfully", idQuiz);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "') or hasRole('" + Roles.ROLE_MENTOR + "')")
    @ApiOperation(value = "Find quiz rating by id")
    @GetMapping(value = "/{idQuiz}")
    public ResponseEntity<QuizRatingDto> findQuizRatingById(@PathVariable("idQuiz") int idQuiz) {
        String idUser = SecurityUtil.getUsername();

        LOGGER.info("Finding rating for quiz {} ", idQuiz);

        if (!quizRatingService.exists(idUser, idQuiz)) {
            LOGGER.info("Quiz doesn't exist for user {}", idUser);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        QuizRating foundQuizRating = quizRatingService.findQuizRatingById(idQuiz, idUser);

        LOGGER.info("Rating for quiz {} found successfully!", idQuiz);
        return new ResponseEntity<>(quizRatingMapper.convertToDTO(foundQuizRating), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_USER + "') or hasRole('" + Roles.ROLE_MENTOR + "') or hasRole('" + Roles.ROLE_ADMINISTRATOR + "')")
    @ApiOperation(value = "Delete rating for a quiz")
    @DeleteMapping(value = "/{idQuiz}")
    public ResponseEntity<Void> deleteQuizRating(@PathVariable("idQuiz") int idQuiz) {
        String idUser = SecurityUtil.getUsername();

        LOGGER.info("Deleting rating for quiz {} ", idQuiz);

        if (!quizRatingService.exists(idUser, idQuiz)) {
            LOGGER.info("Quiz {}  doesn't available for user {} ", idQuiz, idUser);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        quizRatingService.deleteQuizRating(idQuiz, idUser);

        LOGGER.info("Rating for quiz {} deleted successfully", idQuiz);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}


