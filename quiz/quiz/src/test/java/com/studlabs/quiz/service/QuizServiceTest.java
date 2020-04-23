package com.studlabs.quiz.service;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.*;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.junit.*;
import org.springframework.transaction.*;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.Silent.class)
public class QuizServiceTest {

    private QuizService quizService;

    @Mock
    private QuizAccessRepository quizAccessRepository;

    private QuizAccessService quizAccessService;

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuestionQuizRepository questionQuizRepository;

    @Mock
    private PlatformTransactionManager platformTransactionManager;

    @Before
    public void setUp() {
        quizService = new QuizService(quizRepository, questionRepository, questionQuizRepository, platformTransactionManager);
        quizAccessService = new QuizAccessService(quizAccessRepository);
    }

    @Test
    public void testFindQuizById() {
        Quiz quiz = new Quiz(1, "english", QuizDifficulty.HARD, 4.0,120, true, true);

        when(quizRepository.findById(1)).thenReturn(quiz);
        Quiz quizResult = quizService.findById(1);

        assertEquals(quizResult, quiz);
    }

    @Test
    public void testFindAllQuizzes() {
        List<Quiz> quizzes = new ArrayList<>();

        Quiz quiz1 = new Quiz(1, "english", QuizDifficulty.HARD, 4.0,120, true, true);
        Quiz quiz2 = new Quiz(2, "math", QuizDifficulty.HARD, 6.0,120, true, true);

        quizzes.add(quiz1);
        quizzes.add(quiz2);

        when(quizRepository.findAll()).thenReturn(quizzes);
        List<Quiz> result = quizService.findAll();

        assertEquals(result.get(0), quiz1);
        assertEquals(result.get(1), quiz2);
    }

    @Test
    public void testAddQuiz() {
        Quiz quiz = new Quiz(1, "english", QuizDifficulty.HARD, 4.0, 120,true, true);
        quizService.insert(quiz);

        verify(quizRepository, times(1)).insert(quiz);
    }

    @Test
    public void testUpdateQuiz() {
        Quiz quiz = new Quiz(1, "english", QuizDifficulty.HARD, 4.0, 120,true, true);
        quizService.update(quiz);

        verify(quizRepository, times(1)).update(quiz);
    }

    @Test
    public void testDeleteQuiz() {
        quizService.delete(1);

        verify(quizRepository, times(1)).delete(1);
    }

    @Test
    public void testExistsQuiz() {
        quizService.exists(1);

        verify(quizRepository, times(1)).exists(1);
    }

    @Test
    public void testbrowseAllQuizzesWhenThereArePublicQuizzes() {
        List<Quiz> quizzes = new ArrayList<>();

        Quiz quiz1 = new Quiz(1, "english", QuizDifficulty.HARD, 4.0,120, true, true);
        Quiz quiz2 = new Quiz(2, "english", QuizDifficulty.HARD, 5.0, 180,true, false);

        quizzes.add(quiz1);
        quizzes.add(quiz2);

        when(quizRepository.browsePublicQuizzes()).thenReturn(quizzes);
        when(quizRepository.findAll()).thenReturn(quizzes);


        List<Quiz> result = quizService.findAll();

        assertEquals(result.get(0), quiz1);
        assertEquals(result.get(1), quiz2);
    }

    @Test
    public void testbrowseAllQuizzesWhenThereArePrivateQuizzes() {
        List<Quiz> quizzes = new ArrayList<>();

        Quiz quiz1 = new Quiz(1, "english", QuizDifficulty.HARD, 4.0,120, true, true);
        Quiz quiz2 = new Quiz(2, "english", QuizDifficulty.HARD, 5.0,120, true, false);
        Quiz quiz3 = new Quiz(3, "english", QuizDifficulty.HARD, 6.0, 100,false, false);

        quizzes.add(quiz1);
        quizzes.add(quiz2);
        quizzes.add(quiz3);

        List<QuizAccess> privateAccessQuizzes = new ArrayList<>();
        privateAccessQuizzes.add(new QuizAccess(quiz3.getIdQuiz(), "2"));

        quizAccessService.assignQuizToUser("2", quiz3.getIdQuiz());

        List<Quiz> privateQuizzes = new ArrayList<>();
        privateQuizzes.add(quiz3);

        when(quizRepository.browsePrivateQuizzes("2")).thenReturn(privateQuizzes);
        when(quizAccessRepository.findAll()).thenReturn(privateAccessQuizzes);

        assertEquals(privateAccessQuizzes.size(), 1);
        assertEquals(privateAccessQuizzes.get(0).getIdQuiz(), privateQuizzes.get(0).getIdQuiz());
        assertFalse(privateQuizzes.get(0).isPublic());
    }

    @Test
    public void testbrowseAllQuizzes() {
        List<Quiz> quizzes = new ArrayList<>();

        Quiz quiz1 = new Quiz(1, "english", QuizDifficulty.HARD, 4.0,120, true, true);
        Quiz quiz2 = new Quiz(2, "english", QuizDifficulty.HARD, 5.0, 180,true, false);
        Quiz quiz3 = new Quiz(3, "english", QuizDifficulty.HARD, 6.0, 210,false, false);

        quizzes.add(quiz1);
        quizzes.add(quiz2);
        quizzes.add(quiz3);

        List<QuizAccess> privateAccessQuizzes = new ArrayList<>();
        privateAccessQuizzes.add(new QuizAccess(quiz3.getIdQuiz(), "2"));

        quizAccessService.assignQuizToUser("2", quiz3.getIdQuiz());

        when(quizRepository.browseAllQuizzes("2")).thenReturn(quizzes);
        when(quizRepository.findAll()).thenReturn(quizzes);

        List<Quiz> allQuizzes = quizRepository.browseAllQuizzes("2");

        assertEquals(allQuizzes.size(), 3);
        assertEquals(allQuizzes.get(0), quiz1);
        assertEquals(allQuizzes.get(1), quiz2);
        assertEquals(allQuizzes.get(2), quiz3);
    }

    @Test
    public void testFilterQuizzesByDifficultyAndCategory() {
        List<Quiz> quizzes = new ArrayList<>();

        Quiz quiz1 = new Quiz(1, "english", QuizDifficulty.HARD, 4.0,120, true, true);
        Quiz quiz2 = new Quiz(2, "math", QuizDifficulty.HARD, 6.0, 120,true, true);

        quizzes.add(quiz1);
        quizzes.add(quiz2);

        List<QuizDifficulty> difficulties = new ArrayList<>();
        difficulties.add(QuizDifficulty.HARD);
        List<String> categories = new ArrayList<>();
        categories.add("english");
        categories.add("math");

        when(quizRepository.filterQuizzesByFields(difficulties, categories)).thenReturn(quizzes);
        List<Quiz> result = quizService.filterQuizzesByFields(difficulties, categories);

        assertEquals(result.get(0), quiz1);
        assertEquals(result.get(1), quiz2);
        assertEquals(result.size(), 2);
    }

    @Test
    public void testGenerateQuiz() {
        List<Question> questions = new ArrayList<>();

        Question question1 = new Question(3, "desc1", "cat1", "",
                QuestionDifficulty.EASY,  false);
        Question question2 = new Question(4, "desc2", "cat1", "",
                QuestionDifficulty.MODERATE,  false);

        questions.add(question1);
        questions.add(question2);

        when(quizRepository.generateQuiz("football",120)).thenReturn(1);
        when(questionRepository.findQuestionsByCategory("football")).thenReturn(questions);

        quizService.generateQuiz("football", 6,120);

        verify(questionRepository, times(1)).findQuestionsByCategory("football");

        for (Question question : questions) {
            verify(questionQuizRepository, times(1)).assignQuestionToQuiz(1, question.getIdQuestion());
        }
    }

    @Test
    public void testQuizExists() {
        quizService.exists(1);
        verify(quizRepository, times(1)).exists(1);
    }

    @Test
    public void testSetQuizPublic() {
        quizService.setPublic(1, true);
        verify(quizRepository, times(1)).setPublic(1, true);
    }
}