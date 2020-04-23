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

@Api(value = "question controller")
@RestController
@RequestMapping("/questions")
public class QuestionController {

    private static final Logger LOGGER = LogManager.getLogger(QuestionController.class);
    private QuestionService questionService;
    private QuestionMapper questionMapper;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
        this.questionMapper = new QuestionMapper();
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "') or hasRole('" + Roles.ROLE_MENTOR + "')")
    @ApiOperation(value = "Find all questions")
    @GetMapping("/")
    public ResponseEntity<List<QuestionDto>> listAllQuestions() {
        LOGGER.info("Getting all questions");
        List<Question> questions = questionService.findAll();

        List<QuestionDto> questionDtos = questions.stream()
                .map(questionMapper::convertToDTO)
                .collect(Collectors.toList());

        LOGGER.info("Returning {} questions", questions.size());
        return new ResponseEntity<>(questionDtos, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "') or hasRole('" + Roles.ROLE_MENTOR + "') or hasRole('" + Roles.ROLE_USER + "')")
    @ApiOperation(value = "Find question by id")
    @GetMapping(value = "/{id}")
    public ResponseEntity<QuestionDto> findQuestionById(@PathVariable("id") int id) {
        LOGGER.info("Finding question with id:{}", id);

        if (!questionService.exists(id)) {
            LOGGER.info("Question with id:{} doesn't exists", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Question foundQuestion = questionService.findQuestionById(id);

        LOGGER.info("Question {} found successfully", id);
        return new ResponseEntity<>(questionMapper.convertToDTO(foundQuestion), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "') or hasRole('" + Roles.ROLE_MENTOR + "')")
    @ApiOperation(value = "Insert a question")
    @PostMapping("/")
    public ResponseEntity<Integer> insertQuestion(@RequestBody QuestionDto questionDto) {
        LOGGER.info("Adding a new question");
        int id = questionService.insert(questionMapper.convertToEntity(questionDto));

        LOGGER.info("Question {} added successfully!", id);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "') or hasRole('" + Roles.ROLE_MENTOR + "')")
    @ApiOperation(value = "Delete a question")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") int id) {
        LOGGER.info("Deleting question {} ", id);

        if (!questionService.exists(id)) {
            LOGGER.info("Question {} already exist!", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        questionService.delete(id);

        LOGGER.info("Question {} deleted successfully", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "') or hasRole('" + Roles.ROLE_MENTOR + "')")
    @ApiOperation(value = "Update a question")
    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> update(@PathVariable("id") int id,
                                       @RequestBody QuestionDto questionDto) {
        LOGGER.info("Updating question with id:{}", id);

        if (!questionService.exists(id)) {
            LOGGER.info("Question with id:{}  doesn't exists", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        questionDto.setIdQuestion(id);
        questionService.updateQuestion(questionMapper.convertToEntity(questionDto));

        LOGGER.info("Question {}  updated successfully!", questionDto.getIdQuestion());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "') or hasRole('" + Roles.ROLE_MENTOR + "')")
    @ApiOperation(value = "Set question deprecated")
    @PutMapping(value = "/deprecated/{id}")
    public ResponseEntity<Void> setDeprecated(@PathVariable("id") int id,
                                              @RequestParam boolean isDeprecated) {
        LOGGER.info("Set question:{} deprecated/not deprecated", id);

        if (!questionService.exists(id)) {
            LOGGER.info("Question with id:{} doesn't exists", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        questionService.setDeprecated(id, isDeprecated);

        LOGGER.info("Question {} updated successfully!", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
