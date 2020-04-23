package com.studlabs.quiz.service;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.*;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.junit.*;
import org.springframework.transaction.*;

import java.time.*;
import java.util.*;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PlayAnswerServiceTest {

    @Mock
    PlayAnswerRepository playAnswerRepository;

    @Mock
    AnswerRepository answerRepository;

    @Mock
    PlayQuestionRepository playQuestionRepository;

    @Mock
    PlayQuizRepository playQuizRepository;

    @Mock
    QuestionRepository questionRepository;

    @Mock
    PlatformTransactionManager platformTransactionManager;

    private PlayAnswerService playAnswerService;

    @Before
    public void setUp() {
        playAnswerService = new PlayAnswerService(playAnswerRepository, answerRepository, playQuestionRepository, playQuizRepository,
                questionRepository, platformTransactionManager);
    }

    @Test
    public void testInsertValidCorrectAnswer() {
        int playQuestionId = 1;

        List<Integer> answerIds = new ArrayList<>();
        answerIds.add(1);

        PlayQuestion playQuestion = new PlayQuestion();
        playQuestion.setIdQuestion(1);
        playQuestion.setStartTime(LocalDateTime.MIN);
        playQuestion.setIdPlayQuiz(1);

        PlayQuiz playQuiz = new PlayQuiz();
        playQuiz.setRate(24.5);

        Question question = new Question();
        question.setIdQuestion(1);
        question.setDifficulty(QuestionDifficulty.EASY);

        List<Answer> answers = new ArrayList<>();
        Answer answer = new Answer();
        answer.setCorrect(true);
        answer.setIdQuestion(question.getIdQuestion());
        answer.setIdAnswer(1);
        answers.add(answer);

        answerIds.add(answer.getIdAnswer());

        when(playQuestionRepository.findPlayQuestionById(playQuestionId)).thenReturn(playQuestion);
        when(questionRepository.findQuestionById(playQuestion.getIdQuestion())).thenReturn(question);
        when(answerRepository.findAnswersByIds(answerIds)).thenReturn(answers);
        when(answerRepository.findCorrectAnswersByQuestionId(question.getIdQuestion())).thenReturn(answers);
        when(playQuizRepository.findById(playQuestion.getIdPlayQuiz())).thenReturn(playQuiz);

        playAnswerService.insert(answerIds, playQuestionId, null);

        verify(playQuestionRepository).findPlayQuestionById(playQuestionId);
        verify(questionRepository).findQuestionById(playQuestion.getIdQuestion());
        verify(answerRepository).findAnswersByIds(answerIds);
        verify(playQuizRepository).findById(playQuestion.getIdPlayQuiz());
        verify(playQuizRepository).updateRate(playQuestion.getIdPlayQuiz(), 25);
    }

    @Test
    public void testInsertValidIncorrectAnswer() {
        int playQuestionId = 1;

        List<Integer> answerIds = new ArrayList<>();
        List<Answer> answers = new ArrayList<>();

        PlayQuestion playQuestion = new PlayQuestion();
        playQuestion.setIdQuestion(1);
        playQuestion.setStartTime(LocalDateTime.MIN);
        playQuestion.setIdPlayQuiz(1);

        Question question = new Question();

        when(playQuestionRepository.findPlayQuestionById(playQuestionId)).thenReturn(playQuestion);
        when(questionRepository.findQuestionById(playQuestion.getIdQuestion())).thenReturn(question);
        when(answerRepository.findAnswersByIds(answerIds)).thenReturn(answers);

        playAnswerService.insert(answerIds, playQuestionId, null);

        verify(playQuestionRepository).findPlayQuestionById(playQuestionId);
        verify(questionRepository).findQuestionById(playQuestion.getIdQuestion());
        verify(answerRepository).findAnswersByIds(answerIds);
        verify(playQuizRepository, times(0)).findById(playQuestion.getIdPlayQuiz());
        verify(playQuizRepository, times(0)).updateRate(playQuestion.getIdPlayQuiz(), 25);
    }

    @Test
    public void testInsertInvalidExpiredTime() {
        int playQuestionId = 1;

        List<Integer> answerIds = new ArrayList<>();

        PlayQuestion playQuestion = new PlayQuestion();
        playQuestion.setIdQuestion(1);
        playQuestion.setStartTime(LocalDateTime.now());
        playQuestion.setIdPlayQuiz(1);

        Question question = new Question();
        question.setIdQuestion(1);
        question.setDifficulty(QuestionDifficulty.EASY);

        when(playQuestionRepository.findPlayQuestionById(playQuestionId)).thenReturn(playQuestion);
        when(questionRepository.findQuestionById(playQuestion.getIdQuestion())).thenReturn(question);

        playAnswerService.insert(answerIds, playQuestionId, null);

        verify(playQuestionRepository).findPlayQuestionById(playQuestionId);
        verify(questionRepository).findQuestionById(playQuestion.getIdQuestion());
        verify(playQuizRepository, times(0)).findById(playQuestion.getIdPlayQuiz());
        verify(playQuizRepository, times(0)).updateRate(playQuestion.getIdPlayQuiz(), 25);
    }

    @Test
    public void testFindPlayAnswerByIdPlayQuestion() {
        int idPlayQuestionId = 1;
        PlayAnswer playAnswer = new PlayAnswer();

        when(playAnswerRepository.findAnswerByIdPlayQuestion(idPlayQuestionId)).thenReturn(playAnswer);

        Assert.assertEquals(playAnswerService.findAnswerByIdPlayQuestion(idPlayQuestionId).getIdPlayAnswer(), playAnswer.getIdPlayAnswer());
    }
}
