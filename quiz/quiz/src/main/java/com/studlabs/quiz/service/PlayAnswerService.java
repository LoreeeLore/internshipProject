package com.studlabs.quiz.service;

import com.studlabs.quiz.exception.*;
import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.dao.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.*;
import org.springframework.transaction.support.*;

import java.time.*;
import java.util.*;

@Service
public class PlayAnswerService {

    private PlayAnswerRepository playAnswerRepository;
    private AnswerRepository answerRepository;
    private PlayQuestionRepository playQuestionRepository;
    private PlayQuizRepository playQuizRepository;
    private QuestionRepository questionRepository;
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    public PlayAnswerService(PlayAnswerRepository playAnswerRepository, AnswerRepository answerRepository, PlayQuestionRepository playQuestionRepository,
                             PlayQuizRepository playQuizRepository, QuestionRepository questionRepository, PlatformTransactionManager platformTransactionManager) {
        this.playAnswerRepository = playAnswerRepository;
        this.answerRepository = answerRepository;
        this.playQuestionRepository = playQuestionRepository;
        this.playQuizRepository = playQuizRepository;
        this.questionRepository = questionRepository;
        this.platformTransactionManager = platformTransactionManager;
    }

    public void insert(List<Integer> answerIds, int playQuestionId, String textAnswer) throws DataTransactionException {

        PlayQuestion playQuestion = playQuestionRepository.findPlayQuestionById(playQuestionId);
        Question question = questionRepository.findQuestionById(playQuestion.getIdQuestion());

         {
            TransactionDefinition def = new DefaultTransactionDefinition();
            TransactionStatus transactionStatus = platformTransactionManager.getTransaction(def);

            List<PlayAnswer> playAnswers = getPlayAnswersByAnswerIds(answerIds);
            boolean isAnswerCorrect = false;

            try {
                for (PlayAnswer playAnswer : playAnswers) {
                    playAnswer.setIdPlayQuestion(playQuestionId);
                    playAnswerRepository.insert(playAnswer);
                }

                if (checkIfCorrect(playQuestion.getIdQuestion(), playAnswers, textAnswer)) {
                    isAnswerCorrect = true;
                }

                //playQuestionRepository.updateEndTimeAndIsCorrect(playQuestionId, isAnswerCorrect);

                if (isAnswerCorrect) {
                    PlayQuiz playQuiz = playQuizRepository.findById(playQuestion.getIdPlayQuiz());

                    double newRate = receiveExperiencePoints(playQuiz.getRate(), question);
                    playQuizRepository.updateRate(playQuestion.getIdPlayQuiz(), newRate);
                }

                platformTransactionManager.commit(transactionStatus);
            } catch (DataAccessException e) {
                platformTransactionManager.rollback(transactionStatus);
                throw new DataTransactionException("Failed to make transaction", e);
            }
        }
    }

    public PlayAnswer findAnswerByIdPlayQuestion(int idPlayQuestion) {
        return playAnswerRepository.findAnswerByIdPlayQuestion(idPlayQuestion);
    }

    private double receiveExperiencePoints(double currentPoints, Question question) {
        int points = 0;
        switch(question.getDifficulty()) {
            case EASY:
                points=5;
                break;
            case MODERATE:
                points=15;
                break;
            case HARD:
                points=25;
                break;
        }
        return currentPoints + (((double) points) / 10);
    }

    private List<PlayAnswer> getPlayAnswersByAnswerIds(List<Integer> answerIds) {
        List<PlayAnswer> playAnswers = new ArrayList<>();
        List<Answer> answers = answerRepository.findAnswersByIds(answerIds);

        for (Answer answer : answers) {
            playAnswers.add(new PlayAnswer(answer.getIdAnswer(), answer.getText()));
        }

        return playAnswers;
    }

    public List<PlayAnswer> getPlayAnswersByPlayQuestionId(int playQuestionId) {
        List<PlayAnswer> playAnswers = new ArrayList<>();
        List<PlayAnswer> answers = playAnswerRepository.findAllByPlayQuestionId(playQuestionId);

        for (PlayAnswer answer : answers) {
            playAnswers.add(new PlayAnswer(answer.getIdAnswer(), answer.getText()));
        }


        return playAnswers;
    }

    private boolean checkIfCorrect(int questionId, List<PlayAnswer> playAnswers, String textAnswer) {
        List<Answer> correctAnswers = answerRepository.findCorrectAnswersByQuestionId(questionId);

        if (correctAnswers.isEmpty()) {
            return false;
        }

        if (correctAnswers.get(0).getText() != null) {
            return textAnswer.equalsIgnoreCase(correctAnswers.get(0).getText());
        }

        Set<Integer> correctAnswerIds = new HashSet<>();
        for (Answer correctAnswer : correctAnswers) {
            correctAnswerIds.add(correctAnswer.getIdAnswer());
        }

        Set<Integer> playAnswerIds = new HashSet<>();
        for (PlayAnswer playAnswer : playAnswers) {
            playAnswerIds.add(playAnswer.getIdAnswer());
        }

        return correctAnswerIds.equals(playAnswerIds);
    }
}
