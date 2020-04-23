package com.studlabs.quiz.controller;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.security.*;
import com.studlabs.quiz.service.*;
import io.swagger.annotations.*;
import org.apache.logging.log4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Api(value = "quiz access controller")
@RestController
@RequestMapping("/quizAccess")
public class QuizAccessController {

    private static final Logger LOGGER = LogManager.getLogger(QuestionController.class);
    private final QuizAccessService quizAccessService;
    private final QuizService quizService;

    @Autowired
    public QuizAccessController(QuizAccessService quizAccessService, QuizService quizService) {
        this.quizAccessService = quizAccessService;
        this.quizService = quizService;
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "') or hasRole('" + Roles.ROLE_MENTOR + "')")
    @ApiOperation(value = "Find all quizzes for users")
    @GetMapping
    public ResponseEntity<List<QuizAccess>> findAll() {
        LOGGER.info("Finding all quizzes for users");

        List<QuizAccess> result = quizAccessService.findAll();

        LOGGER.info("Returning {} quizzes", result.size());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_MENTOR + "') or hasRole('" + Roles.ROLE_ADMINISTRATOR + "')")
    @ApiOperation(value = "Assign a quiz to a user")
    @PostMapping(value = "/{idQuiz}")
    public ResponseEntity<String> assignQuizToUser(@PathVariable("idQuiz") int idQuiz) {
        String idUser = SecurityUtil.getUsername();

        LOGGER.info("Assign quiz {} to user {} ", idQuiz, idUser);

        if (!quizService.exists(idQuiz)) {
            LOGGER.info("Quiz {} doesn't exist", idQuiz);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Quiz quiz = quizService.findById(idQuiz);

        if (quiz.isPublic()) {
            LOGGER.info("Quiz {} is public", idQuiz);
            return new ResponseEntity<>("Quiz is public", HttpStatus.BAD_REQUEST);
        }

        if (quizAccessService.exists(idUser, idQuiz)) {
            LOGGER.info("Quiz {} already assigned for user {} ", idQuiz, idUser);
            return new ResponseEntity<>("Already assigned", HttpStatus.BAD_REQUEST);
        }

        quizAccessService.assignQuizToUser(idUser, idQuiz);

        LOGGER.info("Quiz {} assigned for user {} ", idQuiz, idUser);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_MENTOR + "') or hasRole('" + Roles.ROLE_ADMINISTRATOR + "')")
    @ApiOperation(value = "Delete quiz for a user")
    @DeleteMapping("/{idQuiz}")
    public ResponseEntity<String> deleteUserFromQuiz(@PathVariable("idQuiz") int idQuiz) {
        String idUser = SecurityUtil.getUsername();

        LOGGER.info("Delete quiz {} for user {} ", idQuiz, idUser);

        if (!quizAccessService.exists(idUser, idQuiz)) {
            LOGGER.info("Quiz {} isn't assigned for user {} ", idQuiz, idUser);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        quizAccessService.deleteUserFromQuiz(idUser, idQuiz);

        LOGGER.info("Quiz {} deleted for user {} ", idQuiz, idUser);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
