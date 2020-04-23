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

@Api(value = "question correction controller")
@RestController
@RequestMapping("/correctQuestions")
public class QuestionCorrectionController {

    private static final Logger LOGGER = LogManager.getLogger(QuestionCorrectionController.class);
    private final QuestionCorrectionService questionCorrectionService;
    private final QuestionCorrectionMapper questionCorrectionMapper;

    @Autowired
    public QuestionCorrectionController(QuestionCorrectionService questionCorrectionService) {
        this.questionCorrectionService = questionCorrectionService;
        this.questionCorrectionMapper = new QuestionCorrectionMapper();
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "') or hasRole('" + Roles.ROLE_MENTOR + "')")
    @ApiOperation(value = "Find all questions corrections")
    @GetMapping
    public ResponseEntity<List<QuestionCorrectionDto>> findAll() {
        LOGGER.info("Getting all question corrections");

        List<QuestionCorrection> questionCorrectionList = questionCorrectionService.findAll();
        List<QuestionCorrectionDto> questionCorrectionDtos = questionCorrectionList.stream().map(questionCorrectionMapper::convertToDTO).collect(Collectors.toList());

        LOGGER.info("Returning {} questions corrections", questionCorrectionList.size());
        return new ResponseEntity<>(questionCorrectionDtos, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_USER + "') or hasRole('" + Roles.ROLE_MENTOR + "') or hasRole('" + Roles.ROLE_ADMINISTRATOR + "')")
    @ApiOperation(value = "Insert a question correction")
    @PostMapping
    public ResponseEntity<String> insertQuestionCorrection(@RequestBody QuestionCorrectionDto questionCorrectionDto) {
        LOGGER.info("Adding a new question correction");

        if (!questionCorrectionService.exists(questionCorrectionDto.getIdUser(), questionCorrectionDto.getIdQuestion())) {
            LOGGER.info("User with id:{} doesn't exist", questionCorrectionDto.getIdUser());

            questionCorrectionService.insertQuestionCorrection(questionCorrectionMapper.convertToEntity(questionCorrectionDto));
            return new ResponseEntity<>(HttpStatus.CREATED);
        }

        LOGGER.info("Question correction {} added successfully!", questionCorrectionDto.getIdQuestion());
        return new ResponseEntity<>("The object already exists", HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_USER + "') or hasRole('" + Roles.ROLE_MENTOR + "') or hasRole('" + Roles.ROLE_ADMINISTRATOR + "')")
    @ApiOperation(value = "Update a question correction")
    @PutMapping(value = "/{idQuestion}")
    public ResponseEntity<String> updateQuestionCorrection(@PathVariable("idQuestion") int idQuestion,
                                                           @RequestBody QuestionCorrectionDto questionCorrectionDto) {
        String idUser = SecurityUtil.getUsername();

        LOGGER.info("Updating question correction");

        questionCorrectionDto.setIdUser(idUser);
        questionCorrectionDto.setIdQuestion(idQuestion);

        if (questionCorrectionService.exists(idUser, idQuestion)) {
            LOGGER.info("Question with id:{} doesn't exist", idQuestion);

            questionCorrectionService.updateQuestionCorrection(questionCorrectionMapper.convertToEntity(questionCorrectionDto));
            return new ResponseEntity<>(HttpStatus.OK);
        }

        LOGGER.info("Correction for question {} updated successfully", idQuestion);
        return new ResponseEntity<>("Couldn't find the object you are trying to update", HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_USER + "') or hasRole('" + Roles.ROLE_MENTOR + "') or hasRole('" + Roles.ROLE_ADMINISTRATOR + "')")
    @ApiOperation(value = "Delete a question correction")
    @DeleteMapping(value = "/{idQuestion}")
    public ResponseEntity<String> deleteQuestionCorrection(@PathVariable("idQuestion") int idQuestion) {
        String idUser = SecurityUtil.getUsername();

        LOGGER.info("Deleting correction for question:" + idQuestion);

        if (questionCorrectionService.exists(idUser, idQuestion)) {
            LOGGER.info("Question with id:{} doesn't exists", idQuestion);

            questionCorrectionService.deleteQuestionCorrection(idUser, idQuestion);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        LOGGER.info("Correction for question {} deleted successfully!", idQuestion);
        return new ResponseEntity<>("Couldn't find the object you are trying to delete", HttpStatus.NOT_FOUND);
    }
}
