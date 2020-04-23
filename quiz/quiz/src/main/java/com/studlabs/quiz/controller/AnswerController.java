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

@Api(value = "answer controller")
@RestController
@RequestMapping("/answers")
public class AnswerController {

    private static final Logger LOGGER = LogManager.getLogger(AnswerController.class);
    public static final int MAX_NUMBER_OF_ANSWERS = 8;
    private final AnswerService answerService;
    private final AnswerMapper answerMapper;

    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
        this.answerMapper = new AnswerMapper();
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "')")
    @ApiOperation(value = "Find All Answers")
    @GetMapping
    public ResponseEntity<List<AnswerDto>> findAllAnswers() {
        LOGGER.info("Getting all answers");
        List<Answer> list = answerService.findAllAnswers();

        List<AnswerDto> result = list.stream().map(answerMapper::convertToDTO).collect(Collectors.toList());

        LOGGER.info("Returning {} answers", result.size());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "')")
    @ApiOperation(value = "Find answer by id")
    @GetMapping("/{id}")
    public ResponseEntity<AnswerDto> findAnswerById(@PathVariable("id") int answerId) {
        LOGGER.info("Finding answer with id:{}", answerId);

        if (!answerService.exists(answerId)) {
            LOGGER.info("Answer with id: {} doesn't exists", answerId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Answer answer = answerService.findAnswerById(answerId);

        LOGGER.info("Answer {} found successfully", answerId);
        return new ResponseEntity<>(answerMapper.convertToDTO(answer), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "')")
    @ApiOperation(value = "Insert an answer")
    @PostMapping
    public ResponseEntity<Void> insertAnswer(@RequestBody AnswerDto answerDto) {
        if (!answerService.checkIfTooManyAnswers(answerDto.getIdQuestion())) {
            LOGGER.info("Question {} has already {} answers", MAX_NUMBER_OF_ANSWERS, answerDto.getIdQuestion());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        LOGGER.info("Adding a new answer");
        answerService.insertAnswer(answerMapper.convertToEntity(answerDto));

        LOGGER.info("Answer {} added successfully!", answerDto.getIdAnswer());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "')")
    @ApiOperation(value = "Update an answer")
    @PutMapping(path = "/{id}")
    public ResponseEntity<Void> updateAnswer(@PathVariable("id") int answerId,
                                             @RequestBody AnswerDto answerDto) {
        LOGGER.info("Updating answer with id:{}", answerId);

        if (!answerService.exists(answerId)) {
            LOGGER.info("Answer with id:{} doesn't exists", answerId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Answer answer = answerMapper.convertToEntity(answerDto);

        answer.setIdAnswer(answerId);
        answerService.updateAnswer(answer);

        LOGGER.info("Answer {} updated successfully!", answerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "')")
    @ApiOperation(value = "Delete an answer")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable("id") int answerId) {
        LOGGER.info("Deleting answer with id:{}", answerId);

        if (!answerService.exists(answerId)) {
            LOGGER.info("Answer with id:{} doesn't exists", answerId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        answerService.deleteAnswer(answerId);

        LOGGER.info("Answer {} deleted successfully!", answerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
