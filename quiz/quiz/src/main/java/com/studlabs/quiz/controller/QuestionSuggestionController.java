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


@Api(value = "question suggestion controller")
@RestController
@RequestMapping("/suggestQuestions")
public class QuestionSuggestionController {

    private static final Logger LOGGER = LogManager.getLogger(AnswerController.class);
    private QuestionSuggestionService questionSuggestionService;
    private QuestionSuggestionMapper questionSuggestionMapper;

    public QuestionSuggestionController(QuestionSuggestionService questionSuggestionService) {
        this.questionSuggestionService = questionSuggestionService;
        this.questionSuggestionMapper = new QuestionSuggestionMapper();
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "') or hasRole('" + Roles.ROLE_MENTOR + "')")
    @ApiOperation(value = "Find all questions suggestions")
    @GetMapping
    public ResponseEntity<List<QuestionSuggestionDto>> getAll() {
        LOGGER.info("Getting all question suggestions");

        List<QuestionSuggestion> questionSuggestions = questionSuggestionService.getAll();

        List<QuestionSuggestionDto> questionSuggestionDtos = questionSuggestions.stream()
                .map(questionSuggestionMapper::convertToDTO)
                .collect(Collectors.toList());
        LOGGER.info("Returning {} question suggestions", questionSuggestionDtos.size());

        return new ResponseEntity<>(questionSuggestionDtos, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_USER + "') or hasRole('" + Roles.ROLE_MENTOR + "') or hasRole('" + Roles.ROLE_ADMINISTRATOR + "')")
    @ApiOperation(value = "Insert a question suggestion")
    @PostMapping
    public ResponseEntity<Void> insertQuestionSuggestion(@RequestBody QuestionSuggestionDto questionSuggestionDto) {
        LOGGER.info("Adding a new question suggestion");

        int id = questionSuggestionService.insert(questionSuggestionMapper.convertToEntity(questionSuggestionDto));
        LOGGER.info("Question suggestion {} added successfully!", id);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_USER + "') or hasRole('" + Roles.ROLE_MENTOR + "') or hasRole('" + Roles.ROLE_ADMINISTRATOR + "')")
    @ApiOperation(value = "Update a question suggestion")
    @PutMapping(value = "/{idSuggestion}")
    public ResponseEntity updateQuestionSuggestion(@PathVariable("idSuggestion") int idSuggestion,
                                                   @RequestBody QuestionSuggestionDto questionSuggestionDto) {
        LOGGER.info("Updating question with id: {}", idSuggestion);

        if (questionSuggestionService.exists(idSuggestion)) {
            questionSuggestionService.update(idSuggestion, questionSuggestionMapper.convertToEntity(questionSuggestionDto));
            LOGGER.info("Question suggestion with id:" + idSuggestion + " doesn't exist");

            return new ResponseEntity<>(HttpStatus.OK);
        }
        LOGGER.info("Question suggestion with id:{} updated successfully", idSuggestion);

        return new ResponseEntity<>("Couldn't find the object you are trying to update!", HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_USER + "') or hasRole('" + Roles.ROLE_MENTOR + "') or hasRole('" + Roles.ROLE_ADMINISTRATOR + "')")
    @ApiOperation(value = "Delete a question suggestion")
    @DeleteMapping(value = "/{idSuggestion}")
    public ResponseEntity deleteQuestionSuggestion(@PathVariable("idSuggestion") int idSuggestion) {
        LOGGER.info("Deleting question suggestion {} ", idSuggestion);

        if (questionSuggestionService.exists(idSuggestion)) {
            questionSuggestionService.delete(idSuggestion);
            LOGGER.info("Question suggestion with id:{} doesn't exist", idSuggestion);

            return new ResponseEntity<>(HttpStatus.OK);
        }
        LOGGER.info("Question suggestion {} deleted successfully", idSuggestion);

        return new ResponseEntity<>("Couldn't find the object you are trying to delete", HttpStatus.NOT_FOUND);
    }
}
