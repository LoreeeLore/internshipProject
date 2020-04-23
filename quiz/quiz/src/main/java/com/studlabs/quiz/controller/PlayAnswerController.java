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

@Api(value = "play answer controller")
@RestController
@RequestMapping("/playAnswer")
public class PlayAnswerController {

    private static final Logger LOGGER = LogManager.getLogger(PlayAnswerController.class);
    private PlayAnswerService playAnswerService;
    private PlayQuestionService playQuestionService;
    private PlayAnswerMapper playAnswerMapper;

    @Autowired
    public PlayAnswerController(PlayAnswerService playAnswerService, PlayQuestionService playQuestionService) {
        this.playAnswerService = playAnswerService;
        this.playQuestionService = playQuestionService;
        this.playAnswerMapper = new PlayAnswerMapper();
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_USER + "') or hasRole('" + Roles.ROLE_MENTOR + "') or hasRole('" + Roles.ROLE_ADMINISTRATOR + "')")
    @ApiOperation(value = "Answer question")
    @PostMapping("/{playQuestionId}")
    public ResponseEntity<String> insert(@PathVariable("playQuestionId") int playQuestionId,
                                         @RequestParam List<Integer> answerIds,
                                         @RequestParam String textAnswer) {
        if (!playQuestionService.exists(playQuestionId)) {
            LOGGER.info("PlayQuestion with id={} doesn't exist", playQuestionId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (playQuestionService.findPlayQuestionById(playQuestionId).getEndTime() != null) {
            LOGGER.info("PlayQuestion with id={} was already answered", playQuestionId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!SecurityUtil.getUsername().equals(playQuestionService.getUser(playQuestionId))) {
            LOGGER.info("Invalid user");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        LOGGER.info("Inserting PlayAnswers");
        playAnswerService.insert(answerIds, playQuestionId, textAnswer);

        LOGGER.info("PlayAnswers inserted successfully");
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "')")
    @ApiOperation(value = "Find answer for this play question")
    @GetMapping(value = "/{id}")
    public ResponseEntity<PlayAnswerDto> findPlayAnswerByIdPlayQuestion(@PathVariable("id") int id) {
        LOGGER.info("Finding play question with id:{}", id);

        if (!playQuestionService.exists(id)) {
            LOGGER.info("Play question with id:{} doesn't exist", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        PlayAnswerDto foundPlayAnswer = playAnswerMapper.convertToDTO(playAnswerService.findAnswerByIdPlayQuestion(id));

        LOGGER.info("Play answer for play question {} found successfully", id);
        return new ResponseEntity<>(foundPlayAnswer, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "') or hasRole('" + Roles.ROLE_USER + "') or hasRole('" + Roles.ROLE_MENTOR + "')")
    @ApiOperation(value = "Find play answers by play question id")
    @GetMapping("/getUserAnswers/{playQuestionId}")
    public ResponseEntity<List<PlayAnswerDto>> findPlayAnswersByPlayQuestionId(@PathVariable("id") int playQuestionId) {
        LOGGER.info("Finding play question with id:{}", playQuestionId);

        if (!playQuestionService.exists(playQuestionId)) {
            LOGGER.info("Play question with id:{} doesn't exist", playQuestionId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<PlayAnswer> list = playAnswerService.getPlayAnswersByPlayQuestionId(playQuestionId);

        List<PlayAnswerDto> foundPlayAnswers = list.stream().map(playAnswerMapper::convertToDTO).collect(Collectors.toList());

        LOGGER.info("Play answers for play question {} found successfully");
        return new ResponseEntity<>(foundPlayAnswers, HttpStatus.OK);
    }
}
