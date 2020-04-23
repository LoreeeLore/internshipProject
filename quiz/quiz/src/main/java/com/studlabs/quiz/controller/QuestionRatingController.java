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

@Api(value = "question quiz controller")
@RestController
@RequestMapping("/questions/rate")
public class QuestionRatingController {

    private static final Logger LOGGER = LogManager.getLogger(QuestionRatingController.class);
    private QuestionRatingService questionRatingService;
    private QuestionRatingMapper questionRatingMapper;

    public QuestionRatingController(QuestionRatingService questionRatingService) {
        this.questionRatingService = questionRatingService;
        this.questionRatingMapper = new QuestionRatingMapper();
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_USER + "') or hasRole('" + Roles.ROLE_MENTOR + "') or hasRole('" + Roles.ROLE_ADMINISTRATOR + "')")
    @ApiOperation(value = "Find all rates for a question")
    @PostMapping("/{idQuestion}")
    public ResponseEntity<String> rateQuestion(@PathVariable("idQuestion") int idQuestion,
                                               @RequestBody QuestionRatingDto questionRatingDto) {
        String idUser = SecurityUtil.getUsername();

        LOGGER.info("User with id:{} rating question {} ", idUser, idQuestion);

        if (questionRatingService.exists(idUser, idQuestion)) {
            LOGGER.info("Question {} already rated", idQuestion);
            return new ResponseEntity<>("This question is already rated", HttpStatus.BAD_REQUEST);
        }

        questionRatingDto.setIdQuestion(idQuestion);
        questionRatingDto.setIdUser(idUser);

        questionRatingService.insert(questionRatingMapper.convertToEntity(questionRatingDto));

        LOGGER.info("Question {} rated successfully", idQuestion);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_USER + "') or hasRole('" + Roles.ROLE_MENTOR + "') or hasRole('" + Roles.ROLE_ADMINISTRATOR + "')")
    @ApiOperation(value = "Delete a rating for a question")
    @DeleteMapping("/{idQuestion}")
    public ResponseEntity<Void> deleteRating(@PathVariable("idQuestion") int idQuestion) {
        String idUser = SecurityUtil.getUsername();

        LOGGER.info("Deleting rating from question:{}", idQuestion);

        if (!questionRatingService.exists(idUser, idQuestion)) {
            LOGGER.info("Question {} doesn't exist", idQuestion);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        questionRatingService.delete(idUser, idQuestion);

        LOGGER.info("Rating deleted successfully for question:{}", idQuestion);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "') or hasRole('" + Roles.ROLE_MENTOR + "')")
    @ApiOperation(value = "Find all question rate")
    @GetMapping("/")
    public ResponseEntity<List<QuestionRatingDto>> findAll() {
        LOGGER.info("Finding all question rating");

        List<QuestionRating> questionRatings = questionRatingService.findAll();
        List<QuestionRatingDto> questionRatingDtos = new ArrayList<>();

        for (QuestionRating questionRating : questionRatings) {
            questionRatingDtos.add(questionRatingMapper.convertToDTO(questionRating));
        }

        LOGGER.info("Returning {} questions rating", questionRatings.size());
        return new ResponseEntity<>(questionRatingDtos, HttpStatus.OK);
    }
}
