package com.studlabs.quiz.controller;

import com.studlabs.quiz.dto.*;
import com.studlabs.quiz.mapper.*;
import com.studlabs.quiz.model.*;
import com.studlabs.quiz.security.*;
import com.studlabs.quiz.service.*;
import io.swagger.annotations.*;
import org.apache.logging.log4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.*;
import java.util.stream.*;

import static java.time.LocalDateTime.now;

@Api(value = "play quiz controller")
@RestController
@RequestMapping("/playQuiz")
public class PlayQuizController {

    private static final Logger LOGGER = LogManager.getLogger(PlayQuizController.class);
    private final PlayQuizService playQuizService;
    private final QuizService quizService;
    private final PlayQuizMapper playQuizMapper;

    @Autowired
    public PlayQuizController(PlayQuizService playQuizService, QuizService quizService) {
        this.playQuizService = playQuizService;
        this.quizService = quizService;
        this.playQuizMapper = new PlayQuizMapper();
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "') or hasRole('" + Roles.ROLE_MENTOR + "') or hasRole('" + Roles.ROLE_USER + "')")
    @ApiOperation(value = "View all played quizzes for a user")
    @GetMapping
    public ResponseEntity<List<PlayQuizDto>> findAllQuizzesForAUser() {
        String idUser = SecurityUtil.getUsername();
        LOGGER.info("Finding all play quizzes for user:{}", idUser);

        List<PlayQuiz> playQuizzes = playQuizService.findAllQuizzesForAUser(idUser);
        List<PlayQuizDto> playQuizzesDtos = playQuizzes.stream().map(playQuizMapper::convertToDTO).collect(Collectors.toList());

        LOGGER.info("Returning {} play quizzes", playQuizzes.size());
        return new ResponseEntity<>(playQuizzesDtos, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "') or hasRole('" + Roles.ROLE_MENTOR + "')")
    @ApiOperation(value = "View played quiz by id")
    @GetMapping(value = "played/{id}")
    public ResponseEntity<PlayQuizDto> findById(@PathVariable("id") int id) {
        LOGGER.info("Getting PlayQuiz with id={}", id);

        if (!playQuizService.exists(id)) {
            LOGGER.info("PlayQuiz with id:{} doesn't exist", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        PlayQuizDto playQuizDto = playQuizMapper.convertToDTO(playQuizService.findById(id));

        LOGGER.info("PlayQuiz {} found successfully", id);
        return new ResponseEntity<>(playQuizDto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "') or hasRole('" + Roles.ROLE_MENTOR + "') or hasRole('" + Roles.ROLE_USER + "')")
    @ApiOperation(value = "Print all Play Quizzes by userId and quizId")
    @GetMapping(value = "userPlayed/{idQuiz}")
    public ResponseEntity<List<PlayQuizDto>> findByUserIdAndQuizId(@PathVariable("idQuiz") int idQuiz) {
        String idUser = SecurityUtil.getUsername();

        LOGGER.info("Getting all PlayQuizzes with userId={} and quizId={}", idUser, idQuiz);
        List<PlayQuiz> playQuizzes = playQuizService.findAllByUserIdAndQuizId(idUser, idQuiz);

        List<PlayQuizDto> playQuizDtos = playQuizzes.stream()
                .map(playQuizMapper::convertToDTO)
                .collect(Collectors.toList());

        LOGGER.info("Returning {} PlayQuizzes", playQuizDtos.size());
        return new ResponseEntity<>(playQuizDtos, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "') or hasRole('" + Roles.ROLE_MENTOR + "') or hasRole('" + Roles.ROLE_USER + "')")
    @ApiOperation(value = "Play a quiz")
    @PostMapping(value = "/{idQuiz}")
    public ResponseEntity<Void> insert(@PathVariable("idQuiz") int idQuiz) {
        String idUser = SecurityUtil.getUsername();

        LOGGER.info("User with id:{} inserting a new PlayQuiz for quiz with id:{}", idUser, idQuiz);

        if (!quizService.exists(idQuiz)) {
            LOGGER.info("Quiz with id:{} doesn't exist", idQuiz);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (!playQuizService.hasAccess(idUser, idQuiz)) {
            LOGGER.info("User with id:{} doesn't have access for quiz with id:{}", idUser, idQuiz);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        LOGGER.info("Adding a new PlayQuiz");
        int idPlayQuiz = playQuizService.insert(idUser, idQuiz);

        LOGGER.info("PlayQuiz {} added successfully", idPlayQuiz);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "') or hasRole('" + Roles.ROLE_MENTOR + "') or hasRole('" + Roles.ROLE_USER + "')")
    @ApiOperation(value = "Finish quiz")
    @PutMapping(value = "/finish/{id}")
    public ResponseEntity<Void> finishQuiz(@PathVariable("id") int id) {
        LOGGER.info("Set status and endTime for play quiz {} ", id);

        if (!playQuizService.exists(id)) {
            LOGGER.info("Play Quiz {} doesn't exist", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (!SecurityUtil.getUsername().equals(playQuizService.getUser(id))) {
            LOGGER.info("Invalid user");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        playQuizService.finishQuiz(id);

        LOGGER.info("Play Quiz {} updated successfully", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_USER + "') or hasRole('" + Roles.ROLE_MENTOR + "') or hasRole('" + Roles.ROLE_ADMINISTRATOR + "')")
    @ApiOperation(value = "Start quiz")
    @PutMapping(value = "/{idPlayQuiz}")
    public ResponseEntity<Void> updateStartTime(@PathVariable("idPlayQuiz") int id) {
        LOGGER.info("Set start time for play quiz {}", id);

        if (!playQuizService.exists(id)) {
            LOGGER.info("Play quiz with id {} doesn't exist", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        /*else if (!playQuizService.findById(id).getStartTime().equals(null)) {
            LOGGER.info("PlayQuiz with id={} was already updated", id);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

         */

        if (!SecurityUtil.getUsername().equals(playQuizService.getUser(id))) {
            LOGGER.info("Invalid user");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }


        PlayQuiz playQuiz = playQuizService.findById(id);
        //PlayQuiz playQuiz = playQuizService.findById(playQuestion.getIdPlayQuiz());

        if (playQuiz.getEndTime() != null) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }

        playQuizService.updateStartTime(id);

        LOGGER.info("Play quiz {} updated successfully!", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /*
    @PreAuthorize("hasRole('" + Roles.ROLE_USER + "') or hasRole('" + Roles.ROLE_MENTOR + "') or hasRole('" + Roles.ROLE_ADMINISTRATOR + "')")
    @ApiOperation(value = "End quiz")
    @PutMapping(value = "/endQuiz/{idPlayQuiz}")
    public ResponseEntity<Void> updateEndTime(@PathVariable("idPlayQuiz") int id) {
        LOGGER.info("Set end time for play quiz {}", id);

        PlayQuiz playQuiz = playQuizService.findById(id);
        Quiz quiz = quizService.findById(playQuiz.getIdQuiz());

        if (!playQuizService.exists(id)) {
            LOGGER.info("Play quiz with id {} doesn't exist", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (!SecurityUtil.getUsername().equals(playQuizService.getUser(id))) {
            LOGGER.info("Invalid user");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if(now().isAfter(playQuiz.getStartTime().plusMinutes(quiz.getTimeInMinutes())) || now().isEqual(playQuiz.getStartTime().plusMinutes(quiz.getTimeInMinutes()))) {
            LOGGER.info("End time");
            playQuizService.finishQuiz(id);
        }

        if (playQuiz.getEndTime() != null) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }

        //playQuizService.updateStartTime(id);

        LOGGER.info("Play quiz {} updated successfully!", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

     */

}
