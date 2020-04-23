package com.studlabs.quiz.controller;

import com.studlabs.quiz.dto.*;
import com.studlabs.quiz.exception.*;
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

@Api(value = "quiz controller")
@RestController
@RequestMapping("/quiz")
public class QuizController {

    private static final Logger LOGGER = LogManager.getLogger(QuizController.class);
    private static final int MIN_NUMBER_OF_QUESTIONS = 3;
    private final QuizService quizService;
    private final QuizMapper quizMapper;

    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;

        this.quizMapper = new QuizMapper();
    }

    @RequestMapping(
            value = "/**",
            method = RequestMethod.OPTIONS
    )
    public ResponseEntity handle() {
        return new ResponseEntity(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_USER + "') or hasRole('" + Roles.ROLE_MENTOR + "') or hasRole('" + Roles.ROLE_ADMINISTRATOR + "')")
    @ApiOperation(value = "Find All Quizzes")
    @GetMapping(value = "/all")
    @CrossOrigin(origins = "*")
    public ResponseEntity<List<QuizDto>> listAllQuizzes(@RequestParam(required = false) List<QuizDifficulty> difficulties,
                                                        @RequestParam(required = false) List<String> categories) {
        LOGGER.info("Getting all quizzes");

        List<QuizDto> quizzesDtos = new ArrayList<>();
        if ((difficulties == null || difficulties.isEmpty()) && (categories == null || categories.isEmpty())) {
            List<Quiz> quizzes = quizService.findAll();
            for (Quiz quiz : quizzes) {
                quizzesDtos.add(quizMapper.convertToDTO(quiz));
            }
        } else {
            quizzesDtos = quizService.filterQuizzesByFields(difficulties, categories).stream().map(quizMapper::convertToDTO).collect(Collectors.toList());
        }

        LOGGER.info("Returning {} quizzes", quizzesDtos.size());
        return new ResponseEntity<>(quizzesDtos, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_ADMINISTRATOR + "') or hasRole('" + Roles.ROLE_MENTOR + "') or hasRole('" + Roles.ROLE_USER+ "')")
    @ApiOperation(value = "Find Quiz by Id")
    @GetMapping(value = "/{id}")
    public ResponseEntity<QuizDto> findQuizById(@PathVariable("id") int id) {
        LOGGER.info("Getting quiz with id={}", id);

        if (!quizService.exists(id)) {
            LOGGER.info("Quiz with id:{} doesn't exist", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        QuizDto quizDto = quizMapper.convertToDTO(quizService.findById(id));

        LOGGER.info("Quiz {} found successfully", id);
        return new ResponseEntity<>(quizDto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_USER + "') or hasRole('" + Roles.ROLE_MENTOR + "') or hasRole('" + Roles.ROLE_ADMINISTRATOR + "')")
    @ApiOperation(value = "Find Available Quizzes")
    @GetMapping(value = "/available")
    public ResponseEntity<List<QuizDto>> findAvailableQuizzes() {
        String id = SecurityUtil.getUsername();

        LOGGER.info("Getting all available quizzes");

        List<Quiz> quizzes = quizService.browseAllQuizzes(id);
        if (quizzes.isEmpty()) {
            LOGGER.info("User with id: {} doesn't have access to any quiz", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<QuizDto> quizzesDto = quizzes.stream().map(quizMapper::convertToDTO).collect(Collectors.toList());

        LOGGER.info("Finding available quizzes for user {} finished successfully: {}", id, quizzesDto);
        return new ResponseEntity<>(quizzesDto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_MENTOR + "') or hasRole('" + Roles.ROLE_ADMINISTRATOR + "')")
    @ApiOperation(value = "Add a Quiz")
    @PostMapping(value = "/")
    public ResponseEntity<Void> add(@RequestBody QuizDto quiz) {
        LOGGER.info("Adding a new quiz");
        quizService.insert(quizMapper.convertToEntity(quiz));

        LOGGER.info("Quiz {} added successfully", quiz.getIdQuiz());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_MENTOR + "') or hasRole('" + Roles.ROLE_ADMINISTRATOR + "')")
    @ApiOperation(value = "Update a Quiz")
    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> update(@PathVariable("id") int id, @RequestBody QuizDto quiz) {
        LOGGER.info("Updating  quiz with id:{}", id);

        if (!quizService.exists(id)) {
            LOGGER.info("Quiz with id:{} doesn't exists", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        quiz.setIdQuiz(id);
        System.out.println("quizzul care vine de pe angular"+quiz.toString());
        System.out.println("quizzul care se updateaza public:"+quizMapper.convertToEntity(quiz).isPublic()+"random:"+quizMapper.convertToEntity(quiz).isRandom());
        quizService.update(quizMapper.convertToEntity(quiz));
        LOGGER.info("Quiz with id:{} updated successfully", id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_MENTOR + "') or hasRole('" + Roles.ROLE_ADMINISTRATOR + "')")
    @ApiOperation(value = "Set quiz public")
    @PutMapping(value = "/public/{id}")
    public ResponseEntity<Void> setPublic(@PathVariable("id") int idQuiz,
                                          @RequestParam boolean isPublic) {
        LOGGER.info("Set quiz {} public/not public", idQuiz);

        if (!quizService.exists(idQuiz)) {
            LOGGER.info("Quiz {} doesn't exist", idQuiz);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        quizService.setPublic(idQuiz, isPublic);

        LOGGER.info("Quiz {} updated successfully", idQuiz);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_MENTOR + "') or hasRole('" + Roles.ROLE_ADMINISTRATOR + "')")
    @ApiOperation(value = "Delete a quiz")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") int id) throws ConvertBlobException {
        LOGGER.info("Deleting quiz with id={}", id);

        quizService.findById(id);
        if (!quizService.exists(id)) {
            LOGGER.info("Quiz with id:{} doesn't exists", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        quizService.delete(id);

        LOGGER.info("Quiz {} deleted successfully!", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('" + Roles.ROLE_USER + "') or hasRole('" + Roles.ROLE_MENTOR + "') or hasRole('" + Roles.ROLE_ADMINISTRATOR + "')")
    @ApiOperation(value = "Generate a random quiz")
    @PostMapping(value = "/generate")
    public ResponseEntity<String> generate(@RequestParam String category,
                                           @RequestParam int numberOfQuestions, @RequestParam long timeInMinutes) {
        LOGGER.info("Generating random quiz with {} questions and category={}", numberOfQuestions, category);

        if (numberOfQuestions < MIN_NUMBER_OF_QUESTIONS || numberOfQuestions > QuestionQuizController.MAX_NUMBER_OF_QUESTIONS) {
            LOGGER.info("Invalid number of questions: {}. Required between {} and {}", numberOfQuestions, MIN_NUMBER_OF_QUESTIONS, QuestionQuizController.MAX_NUMBER_OF_QUESTIONS);
            return new ResponseEntity<>("Number of questions should be between " + MIN_NUMBER_OF_QUESTIONS + " and " + QuestionQuizController.MAX_NUMBER_OF_QUESTIONS,
                    HttpStatus.BAD_REQUEST);
        }

        quizService.generateQuiz(category, numberOfQuestions, timeInMinutes);

        LOGGER.info("Quiz generated successfully!");
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
