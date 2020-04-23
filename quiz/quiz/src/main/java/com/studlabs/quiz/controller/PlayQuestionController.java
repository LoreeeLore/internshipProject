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

import java.util.*;
import java.util.stream.*;

@Api(value = "play question controller")
@RestController
@RequestMapping("/playQuestions")
public class PlayQuestionController {

    private static final Logger LOGGER = LogManager.getLogger(PlayQuestionController.class);
    private PlayQuestionService playQuestionService;
    private PlayQuestionMapper playQuestionMapper;
    private PlayQuizService playQuizService;

    @Autowired
    public PlayQuestionController(PlayQuestionService playQuestionService, PlayQuizService playQuizService) {
        this.playQuestionService = playQuestionService;
        this.playQuestionMapper = new PlayQuestionMapper();
        this.playQuizService = playQuizService;
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "') or hasRole('" + Roles.ROLE_USER+ "')")
    @ApiOperation(value = "Find a specific question for this quiz")
    @GetMapping(value = "/{id}")
    public ResponseEntity<PlayQuestionDto> findPlayQuestionById(@PathVariable("id") int id) {
        LOGGER.info("Finding play question with id:{}", id);

        if (!playQuestionService.exists(id)) {
            LOGGER.info("Play question with id:{} doesn't exist", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        PlayQuestion foundPlayQuestion = playQuestionService.findPlayQuestionById(id);

        LOGGER.info("Play question {} found successfully", id);
        return new ResponseEntity<>(playQuestionMapper.convertToDTO(foundPlayQuestion), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "') or hasRole('" + Roles.ROLE_USER+ "')")
    @ApiOperation(value = "Find all questions for specific quiz")
    @GetMapping(value = "/findall/{idPlayQuiz}")
    public ResponseEntity<List<PlayQuestionDto>> findAllQuestionsForQuiz(@PathVariable("idPlayQuiz") int id) {
        LOGGER.info("Find all questions for quiz{}", id);

        List<PlayQuestion> playQuestions = playQuestionService.findAllQuestionsForAQuiz(id);

        List<PlayQuestionDto> playQuestionDtos = playQuestions.stream()
                .map(playQuestionMapper::convertToDTO)
                .collect(Collectors.toList());

        LOGGER.info("Returning {} questions", playQuestions.size());
        return new ResponseEntity<>(playQuestionDtos, HttpStatus.OK);

    }


}
