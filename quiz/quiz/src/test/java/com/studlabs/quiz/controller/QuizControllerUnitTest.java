package com.studlabs.quiz.controller;

import com.studlabs.quiz.dto.*;
import com.studlabs.quiz.exception.*;
import com.studlabs.quiz.mapper.*;
import com.studlabs.quiz.model.*;
import com.studlabs.quiz.security.*;
import com.studlabs.quiz.service.*;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.junit.*;

import java.util.*;
import java.util.stream.*;

import static com.studlabs.quiz.util.TestAuthenticationProvider.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.Silent.class)
public class QuizControllerUnitTest {

    private QuizController quizController;

    @Mock
    private QuizService quizService;

    private QuizMapper quizMapper = new QuizMapper();

    @Before
    public void setUp() {
        quizController = new QuizController(quizService);
        configureTestAuthentication("1", Roles.ROLE_ADMINISTRATOR);
    }

    @Test
    public void testFindQuizByIdValid() throws ConvertBlobException {
        Quiz quiz = new Quiz(1, "english", QuizDifficulty.HARD, 4.0,100, true, true);

        when(quizService.exists(quiz.getIdQuiz())).thenReturn(true);
        when(quizService.findById(quiz.getIdQuiz())).thenReturn(quiz);

        quizController.findQuizById(quiz.getIdQuiz());

        verify(quizService, times(1)).findById(quiz.getIdQuiz());
        Assert.assertEquals(quizController.findQuizById(quiz.getIdQuiz()).getBody(), quizMapper.convertToDTO(quiz));
    }

    @Test
    public void testFindQuizByIdInvalid() throws ConvertBlobException {
        when(quizService.exists(1)).thenReturn(false);

        quizController.findQuizById(1);

        verify(quizService, times(0)).findById(1);
    }

    @Test
    public void testFindAllQuizzes() {
        List<Quiz> quizzes = new ArrayList<>();
        Quiz quiz1 = new Quiz(1, "english", QuizDifficulty.HARD, 4.0,100, true, true);
        Quiz quiz2 = new Quiz(2, "math", QuizDifficulty.HARD, 6.0,120, true, true);

        quizzes.add(quiz1);
        quizzes.add(quiz2);

        List<QuizDifficulty> difficulties = new ArrayList<>();
        List<String> categories = new ArrayList<>();

        when(quizService.filterQuizzesByFields(difficulties, categories)).thenReturn(quizzes);
        when(quizService.findAll()).thenReturn(quizzes);

        List<QuizDto> resultDto = quizController.listAllQuizzes(difficulties, categories).getBody();

        List<Quiz> result = new ArrayList<>();

        assert resultDto != null;
        for (QuizDto quizDto : resultDto) {
            result.add(quizMapper.convertToEntity(quizDto));
        }

        assertNotNull(quizzes);
        assertEquals(2, quizzes.size());
        assertEquals(quizzes.get(0), result.get(0));
        assertEquals(quizzes.get(1), result.get(1));
        assertEquals(2, resultDto.size());
    }


    @Test
    public void testAddQuiz() {
        Quiz quiz = new Quiz(1, "english", QuizDifficulty.HARD, 4.0,120, true, true);
        quizController.add(quizMapper.convertToDTO(quiz));

        verify(quizService, times(1)).insert(quiz);
    }

    @Test
    public void testGenerateQuizValidNumberOfQuestions() {
        quizController.generate("football", 6,120);

        verify(quizService, times(1)).generateQuiz("football", 6,120);
    }

    @Test
    public void testGenerateQuizInvalidNumberOfQuestions() {
        quizController.generate("football", 2,120);

        verify(quizService, times(0)).generateQuiz("football", 6,120);
    }

    @Test(expected = DataTransactionException.class)
    public void testGenerateQuizInvalidTransaction() {
        List<Quiz> quizzes = quizService.findAll();

        doThrow(DataTransactionException.class).when(quizService).generateQuiz("football", 6,100);

        quizController.generate("football", 6,100);

        List<Quiz> quizzesAfterGenerate = quizService.findAll();

        assertEquals(quizzes.size(), quizzesAfterGenerate.size());
    }

    @Test
    public void testUpdateQuizValidId() {
        Quiz quiz = new Quiz(1, "english", QuizDifficulty.HARD, 4.0, 100,true, true);

        when(quizService.exists(quiz.getIdQuiz())).thenReturn(true);
        quizController.update(quiz.getIdQuiz(), quizMapper.convertToDTO(quiz));

        verify(quizService, times(1)).update(quiz);
    }

    @Test
    public void testUpdateQuizInvalidId() {
        Quiz quiz = new Quiz(1, "english", QuizDifficulty.HARD, 4.0,100, true, true);

        when(quizService.exists(quiz.getIdQuiz())).thenReturn(false);
        quizController.update(quiz.getIdQuiz(), quizMapper.convertToDTO(quiz));

        verify(quizService, times(0)).update(quiz);
    }

    @Test
    public void testDeleteQuizValidId() {
        when(quizService.exists(1)).thenReturn(true);

        quizController.delete(1);

        verify(quizService, times(1)).delete(1);
    }

    @Test
    public void testDeleteQuizInvalidId() {
        when(quizService.exists(1)).thenReturn(false);

        quizController.delete(1);

        verify(quizService, times(0)).delete(1);
    }

    @Test
    public void testFilterQuizzesByDifficultyWhenListIsNotEmpty() {

        List<Quiz> quizzes = new ArrayList<>();

        Quiz quiz1 = new Quiz(1, "english", QuizDifficulty.HARD, 4.0, 100,true, true);
        Quiz quiz2 = new Quiz(2, "english", QuizDifficulty.HARD, 4.0, 100,true, false);

        quizzes.add(quiz1);
        quizzes.add(quiz2);


        List<QuizDifficulty> difficulties = new ArrayList<>();
        List<String> categories = new ArrayList<>();
        categories.add("english");
        difficulties.add(QuizDifficulty.HARD);

        when(quizService.filterQuizzesByFields(difficulties, categories)).thenReturn(quizzes);

        List<QuizDto> resultDto = quizController.listAllQuizzes(difficulties, categories).getBody();

        List<Quiz> result = Objects.requireNonNull(resultDto).stream().map(quizDto -> quizMapper.convertToEntity(quizDto)).collect(Collectors.toList());

        assertNotNull(quizzes);
        assertEquals(quizzes.get(0), result.get(0));
        assertEquals(quizzes.get(1), result.get(1));
        assertEquals(2, result.size());

    }

    @Test
    public void testFilterQuizzesByDifficultyWhenListIsEmpty() {
        List<Quiz> quizzes = quizService.findAll();

        List<QuizDifficulty> difficulties = new ArrayList<>();
        List<String> categories = new ArrayList<>();

        when(quizService.filterQuizzesByFields(difficulties, categories)).thenReturn(quizzes);
        List<QuizDto> result = quizController.listAllQuizzes(difficulties, categories).getBody();

        if (result != null) {
            assertEquals(result.size(), quizzes.size());
        }
    }

    @Test
    public void testSetPublicQuizWhenExist() {
        when(quizService.exists(1)).thenReturn(true);

        quizController.setPublic(1, true);

        verify(quizService, times(1)).exists(1);
        verify(quizService, times((1))).setPublic(1, true);
    }

    @Test
    public void testSetPublicQuizWhenDoesNotExist() {
        when(quizService.exists(1)).thenReturn(false);

        quizController.setPublic(1, true);

        verify(quizService, times(1)).exists(1);
        verify(quizService, times(0)).setPublic(1, true);
    }

    @Test
    public void testBrowseThroughAvailableQuizzesWhenListIsEmpty() {
        List<Quiz> allQuizzes = new ArrayList<>();

        List<QuizDifficulty> difficulties = new ArrayList<>();
        List<String> categories = new ArrayList<>();

        when(quizService.browseAllQuizzes("2")).thenReturn(allQuizzes);

        List<Quiz> result = new ArrayList<>();

        List<QuizDto> resultDto = quizController.listAllQuizzes(difficulties, categories).getBody();

        assert resultDto != null;
        for (QuizDto quiz : resultDto) {
            result.add(quizMapper.convertToEntity(quiz));
        }

        assertNotNull(allQuizzes);
        assertTrue(allQuizzes.isEmpty());
        assertTrue(resultDto.isEmpty());

    }

    @Test
    public void testBrowseThroughAvailableQuizzesWhenListIsNotEmpty() {
        List<Quiz> allQuizzes = new ArrayList<>();

        Quiz quiz1 = new Quiz(1, "english", QuizDifficulty.HARD, 4.0, 100,true, true);
        Quiz quiz2 = new Quiz(2, "math", QuizDifficulty.HARD, 6.0,120, true, true);

        allQuizzes.add(quiz1);
        allQuizzes.add(quiz2);

        List<QuizDifficulty> difficulties = new ArrayList<>();
        List<String> categories = new ArrayList<>();

        when(quizService.browseAllQuizzes("2")).thenReturn(allQuizzes);
        when(quizService.findAll()).thenReturn(allQuizzes);

        List<Quiz> result = new ArrayList<>();

        List<QuizDto> resultDto = quizController.listAllQuizzes(difficulties, categories).getBody();

        assert resultDto != null;
        for (QuizDto quiz : resultDto) {
            result.add(quizMapper.convertToEntity(quiz));
        }

        assertNotNull(allQuizzes);
        assertEquals(allQuizzes.get(0), quiz1);
        assertEquals(allQuizzes.get(1), quiz2);
        assertEquals(resultDto.size(), 2);
        assertEquals(result.size(), 2);

    }
}
