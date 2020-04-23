package com.studlabs.quiz.controller;
import com.studlabs.quiz.dto.QuestionDto;
import com.studlabs.quiz.mapper.QuestionMapper;
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
import java.util.stream.Collectors;

@Api(value = "question quiz controller")
@RestController
@RequestMapping("/quizQuestion")
public class QuestionQuizController {

    private static final Logger LOGGER = LogManager.getLogger(QuestionQuizController.class);
    public static final int MAX_NUMBER_OF_QUESTIONS = 15;
    private final QuestionQuizService questionQuizService;
    private QuestionService questionService;
    private QuestionMapper questionMapper;

    @Autowired
    public QuestionQuizController(QuestionQuizService questionQuizService, QuestionService questionService) {
        this.questionQuizService = questionQuizService;
        this.questionService = questionService;
        this.questionMapper = new QuestionMapper();
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "') or hasRole('" + Roles.ROLE_MENTOR + "')")
    @ApiOperation(value = "Find all questions for a quiz")
    @GetMapping(value = "/{idQuiz}")
    public ResponseEntity<List<QuestionDto>> findAllQuestionsOfQuiz(@PathVariable("idQuiz") int idQuiz) {
        LOGGER.info("Getting all questions for a quiz");
        List<QuestionQuiz> result = questionQuizService.findAllByQuizID(idQuiz);
        List<Question> filteredresult=new ArrayList<>();
        int idQuestion=0;
        for( QuestionQuiz element: result){
            idQuestion=element.getIdQuestion();
            Question foundQuestion = questionService.findQuestionById(idQuestion);
            filteredresult.add(foundQuestion);

        }
        List<QuestionDto> questionDtos = filteredresult.stream()
                .map(questionMapper::convertToDTO)
                .collect(Collectors.toList());

        LOGGER.info("Returning {} questions for all quizzes", questionDtos.size());
        return new ResponseEntity<>(questionDtos, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "') or hasRole('" + Roles.ROLE_MENTOR + "')")
    @ApiOperation(value = "Assign a question to a quiz")
    @PostMapping(value = "/{idQuiz}/{idQuestion}")
    public ResponseEntity<String> assignQuestionToQuiz(@PathVariable("idQuiz") int idQuiz,
                                                       @PathVariable("idQuestion") int idQuestion) {
        LOGGER.info("Adding question:{} to quiz:{}", idQuestion, idQuiz);

        if (questionQuizService.exists(idQuestion, idQuiz)) {
            LOGGER.info("Question:{} already assigned to quiz {}", idQuestion, idQuiz);
            return new ResponseEntity<>("Already assigned", HttpStatus.BAD_REQUEST);
        } else if (!questionQuizService.checkIfValidNumberOfQuestions(idQuiz)) {
            LOGGER.info("Quiz:{} has already {} questions", MAX_NUMBER_OF_QUESTIONS, idQuiz);
            return new ResponseEntity<>("Too many questions", HttpStatus.BAD_REQUEST);
        }

        questionQuizService.assignQuestionToQuiz(idQuiz, idQuestion);

        LOGGER.info("Question:{} added to quiz:{} successfully!", idQuestion, idQuiz);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "') or hasRole('" + Roles.ROLE_MENTOR + "')")
    @ApiOperation(value = "Delete a question for a quiz")
    @DeleteMapping(value = "/{idQuiz}/{idQuestion}")
    public ResponseEntity<Void> deleteQuestionFromQuiz(@PathVariable("idQuiz") int idQuiz,
                                                       @PathVariable("idQuestion") int idQuestion) {
        LOGGER.info("Deleting question {} from quiz {} ", idQuestion, idQuiz);

        if (!questionQuizService.exists(idQuestion, idQuiz)) {
            LOGGER.info("Question with id:{} doesn't exist", idQuestion);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        questionQuizService.deleteQuestionFromQuiz(idQuiz, idQuestion);

        LOGGER.info("Question {} deleted successfully", idQuestion);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}