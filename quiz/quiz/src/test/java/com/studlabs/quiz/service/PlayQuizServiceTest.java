package com.studlabs.quiz.service;

import com.studlabs.quiz.exception.*;
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

@RunWith(MockitoJUnitRunner.class)
public class PlayQuizServiceTest {

    private PlayQuizService playQuizService;

    @Mock
    private PlayQuizRepository playQuizRepository;

    @Mock
    private PlayQuestionRepository playQuestionRepository;

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuizAccessRepository quizAccessRepository;

    @Mock
    private QuestionQuizRepository questionQuizRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private PlatformTransactionManager platformTransactionManager;

    @Mock
    private QuestionRepository questionRepository;


    @Before
    public void setUp() {
        playQuizService = new PlayQuizService(playQuizRepository, playQuestionRepository, quizRepository, quizAccessRepository,
                questionQuizRepository, answerRepository, questionRepository, platformTransactionManager);
    }

    @Test
    public void testInsertPlayQuizValid() {
        List<QuestionQuiz> questionQuizList = new ArrayList<>();

        Answer answer = new Answer();
        answer.setCorrect(true);

        List<Answer> answers = new ArrayList<>();
        answers.add(answer);

        for (int i = 0; i < 5; i++) {
            questionQuizList.add(new QuestionQuiz(i, 1));
        }

        when(questionQuizRepository.findAllByQuizId(1)).thenReturn(questionQuizList);

        for (QuestionQuiz questionQuiz : questionQuizList) {
            when(answerRepository.findCorrectAnswersByQuestionId(questionQuiz.getIdQuestion())).thenReturn(answers);
        }

        when(playQuizRepository.insert("1", 1)).thenReturn(1);

        assertEquals(1, playQuizService.insert("1", 1));

        verify(playQuizRepository, times(1)).insert("1", 1);
        verify(questionQuizRepository, times(1)).findAllByQuizId(1);
        verify(playQuestionRepository, times(1)).insert(new PlayQuestion(1, 1));
    }

    @Test(expected = InvalidQuizException.class)
    public void testInsertPlayQuizInvalidQuiz() {
        List<QuestionQuiz> questionQuizList = new ArrayList<>();
        questionQuizList.add(new QuestionQuiz(1, 1));

        when(questionQuizRepository.findAllByQuizId(1)).thenReturn(questionQuizList);

        playQuizService.insert("1", 1);

        verify(questionQuizRepository, times(1)).findAllByQuizId(1);
    }

    @Test(expected = InvalidQuestionsException.class)
    public void testInsertPlayQuizInvalidQuestions() {
        List<QuestionQuiz> questionQuizList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            questionQuizList.add(new QuestionQuiz(i, 1));
        }

        when(questionQuizRepository.findAllByQuizId(1)).thenReturn(questionQuizList);

        playQuizService.insert("1", 1);

        verify(questionQuizRepository, times(1)).findAllByQuizId(1);
    }

    @Test
    public void testFindPlayQuizById() {
        PlayQuiz playQuiz = new PlayQuiz();

        when(playQuizRepository.findById(1)).thenReturn(playQuiz);

        Assert.assertEquals(playQuiz, playQuizService.findById(1));
    }

    @Test
    public void testExistsPlayQuiz() {
        when(playQuizRepository.exists(1)).thenReturn(true);

        assertTrue(playQuizRepository.exists(1));

        verify(playQuizRepository, times(1)).exists(1);
    }

    @Test
    public void testHasPublicAccessPlayQuiz() {
        Quiz quiz = new Quiz();
        quiz.setPublic(true);

        when(quizRepository.findById(1)).thenReturn(quiz);

        assertTrue(playQuizService.hasAccess("1", 1));

        verify(quizRepository, times(1)).findById(1);
        verify(quizAccessRepository, times(0)).exists("1", 1);
    }

    @Test
    public void testHasPrivateAccessPlayQuiz() {
        Quiz quiz = new Quiz();

        when(quizRepository.findById(1)).thenReturn(quiz);
        when(quizAccessRepository.exists("1", 1)).thenReturn(true);

        assertTrue(playQuizService.hasAccess("1", 1));

        verify(quizRepository, times(1)).findById(1);
        verify(quizAccessRepository, times(1)).exists("1", 1);
    }

    @Test
    public void testGetAllQuizzesForAUser() {
        List<PlayQuiz> playQuizzes = new ArrayList<>();

        PlayQuiz playQuiz1 = new PlayQuiz(1, "1");
        PlayQuiz playQuiz2 = new PlayQuiz(2, "1");

        playQuizzes.add(playQuiz1);
        playQuizzes.add(playQuiz2);

        when(playQuizRepository.findAllQuizzesForAUser("1")).thenReturn(playQuizzes);

        assertEquals(playQuizzes, playQuizService.findAllQuizzesForAUser("1"));
    }

    @Test
    public void testFindByUserIdAndQuizId() {
        List<PlayQuiz> playQuizzes = new ArrayList<>();
        PlayQuiz playQuiz1 = new PlayQuiz(1, 1, "1", null, null, 0, PlayQuizStatus.IN_PROGRESS);
        PlayQuiz playQuiz2 = new PlayQuiz(2, 1, "1", null, null, 0, PlayQuizStatus.IN_PROGRESS);

        playQuizzes.add(playQuiz1);
        playQuizzes.add(playQuiz2);

        when(playQuizRepository.findAllByUserIdAndQuizId("1", 1)).thenReturn(playQuizzes);

        assertEquals(playQuizzes, playQuizService.findAllByUserIdAndQuizId("1", 1));
    }

    @Test
    public void testFinishQuizWhenPassed() {
        PlayQuiz playQuiz = new PlayQuiz();
        playQuiz.setIdQuiz(1);

        Quiz quiz = new Quiz();
        quiz.setIdQuiz(1);
        quiz.setCompletionRate(10);

        Question question = new Question(1, "Scrie-ti corespondentul in baza 10 al numarului 1101.", "informatics", null, QuestionDifficulty.EASY, false);
        Question question1 = new Question(2, "Care este functia predefinita pt interschimbarea a 2 numere?", "informatics", null, QuestionDifficulty.EASY, false);
        QuestionQuiz questionQuiz = new QuestionQuiz(question.getIdQuestion(), quiz.getIdQuiz());
        QuestionQuiz questionQuiz1 = new QuestionQuiz(question1.getIdQuestion(), quiz.getIdQuiz());
        List<QuestionQuiz> list = new ArrayList<>();
        list.add(questionQuiz);
        list.add(questionQuiz1);

        when(playQuizRepository.findById(1)).thenReturn(playQuiz);
        when(quizRepository.findById(quiz.getIdQuiz())).thenReturn(quiz);
        when(questionQuizRepository.findAllByQuizId(quiz.getIdQuiz())).thenReturn(list);
        when(questionRepository.findQuestionById(1)).thenReturn(question);
        when(questionRepository.findQuestionById(2)).thenReturn(question1);

        playQuizService.finishQuiz(1);

        verify(playQuizRepository, times(1)).updateQuizStatus(1, PlayQuizStatus.PASSED,1.0);

    }

    @Test
    public void testFinishQuizWhenFailed() {
        PlayQuiz playQuiz = new PlayQuiz();
        playQuiz.setIdQuiz(1);

        Quiz quiz = new Quiz();
        quiz.setIdQuiz(1);
        quiz.setCompletionRate(50);

        Question question = new Question(1, "Scrie-ti corespondentul in baza 10 al numarului 1101.", "informatics", null, QuestionDifficulty.EASY, false);
        Question question1 = new Question(2, "Care este functia predefinita pt interschimbarea a 2 numere?", "informatics", null, QuestionDifficulty.EASY, false);
        QuestionQuiz questionQuiz = new QuestionQuiz(question.getIdQuestion(), quiz.getIdQuiz());
        QuestionQuiz questionQuiz1 = new QuestionQuiz(question1.getIdQuestion(), quiz.getIdQuiz());
        List<QuestionQuiz> list = new ArrayList<>();
        list.add(questionQuiz);
        list.add(questionQuiz1);

        when(playQuizRepository.findById(1)).thenReturn(playQuiz);
        when(quizRepository.findById(quiz.getIdQuiz())).thenReturn(quiz);
        when(questionQuizRepository.findAllByQuizId(quiz.getIdQuiz())).thenReturn(list);
        when(questionRepository.findQuestionById(1)).thenReturn(question);
        when(questionRepository.findQuestionById(2)).thenReturn(question1);

        playQuizService.finishQuiz(1);

        verify(playQuizRepository, times(1)).updateQuizStatus(1, PlayQuizStatus.FAILED,1.0);

    }

    @Test
    public void testUpdatePlayQuiz() {
        playQuizService.updateStartTime(1);

        verify(playQuizRepository, Mockito.times(1)).updateStartTime(1);
    }


}
