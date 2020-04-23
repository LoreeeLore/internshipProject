package com.studlabs.quiz.service;

import com.studlabs.quiz.exception.*;
import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.dao.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.*;
import org.springframework.transaction.support.*;

import java.util.*;

@Service
public class PlayQuizService {

    private PlayQuizRepository playQuizRepository;
    private PlayQuestionRepository playQuestionRepository;
    private QuizRepository quizRepository;
    private QuizAccessRepository quizAccessRepository;
    private QuestionQuizRepository questionQuizRepository;
    private AnswerRepository answerRepository;
    private PlatformTransactionManager platformTransactionManager;
    private QuestionRepository questionRepository;

    @Autowired
    public PlayQuizService(PlayQuizRepository playQuizRepository, PlayQuestionRepository playQuestionRepository, QuizRepository quizRepository,
                           QuizAccessRepository quizAccessRepository, QuestionQuizRepository questionQuizRepository, AnswerRepository answerRepository,
                           QuestionRepository questionRepository, PlatformTransactionManager platformTransactionManager) {
        this.playQuizRepository = playQuizRepository;
        this.playQuestionRepository = playQuestionRepository;
        this.quizRepository = quizRepository;
        this.quizAccessRepository = quizAccessRepository;
        this.questionQuizRepository = questionQuizRepository;
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.platformTransactionManager = platformTransactionManager;
    }

    public String getUser(int id) {
        return playQuizRepository.getUser(id);
    }

    public List<PlayQuiz> findAllByUserIdAndQuizId(String idUser, int idQuiz) {
        return playQuizRepository.findAllByUserIdAndQuizId(idUser, idQuiz);
    }

    public PlayQuiz findById(int id) {
        return playQuizRepository.findById(id);
    }

    public boolean exists(int id) {
        return playQuizRepository.exists(id);
    }

    public void updateStartTime(int idPlayQuiz) {
        playQuizRepository.updateStartTime(idPlayQuiz);
    }

    public void updateEndTime(int idPlayQuiz) {
        playQuizRepository.updateEndTime(idPlayQuiz);
    }

    public int insert(String idUser, int idQuiz) throws DataTransactionException {
        TransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = platformTransactionManager.getTransaction(def);

        try {
            List<QuestionQuiz> questionQuizList = questionQuizRepository.findAllByQuizId(idQuiz);

            if (!checkIfQuizValid(questionQuizList)) {
                throw new InvalidQuizException("Quiz has less than 5 questions");
            } else if (!checkIfQuestionsValid(questionQuizList)) {
                throw new InvalidQuestionsException("Questions don't have at least 1 correct answer");
            }

            int idPlayQuiz = playQuizRepository.insert(idUser, idQuiz);

            for (QuestionQuiz questionQuiz : questionQuizList) {
                playQuestionRepository.insert(new PlayQuestion(questionQuiz.getIdQuestion(), idPlayQuiz));
            }

            platformTransactionManager.commit(status);
            return idPlayQuiz;
        } catch (DataAccessException e) {
            platformTransactionManager.rollback(status);
            throw new DataTransactionException("Failed to make transaction", e);
        }
    }

    public void finishQuiz(int id) {
        PlayQuiz playQuiz = playQuizRepository.findById(id);
        Quiz quiz = quizRepository.findById(playQuiz.getIdQuiz());
        PlayQuizStatus status;
        //double completionRatePlayQuiz = (100 * playQuiz.getRate()) / calculateRateForQuiz(quiz);
        double completionRatePlayQuiz = calculateRateForQuiz(quiz) /10;
        if (completionRatePlayQuiz >= quiz.getCompletionRate()/10) {
            status = PlayQuizStatus.PASSED;
        } else {
            status = PlayQuizStatus.FAILED;
        }
        playQuizRepository.updateQuizStatus(id, status, completionRatePlayQuiz);
    }

    public List<PlayQuiz> findAllQuizzesForAUser(String idUser) {
        return playQuizRepository.findAllQuizzesForAUser(idUser);
    }

    public boolean hasAccess(String idUser, int idQuiz) {
        Quiz quiz = quizRepository.findById(idQuiz);

        if (quiz.isPublic()) {
            return true;
        }

        return quizAccessRepository.exists(idUser, idQuiz);
    }


    private double calculateRateForQuiz(Quiz quiz) {

        List<QuestionQuiz> questionQuizList = questionQuizRepository.findAllByQuizId(quiz.getIdQuiz());
        Question question;
        double rate = 0;

        for (QuestionQuiz questionQuiz : questionQuizList) {
            question = questionRepository.findQuestionById(questionQuiz.getIdQuestion());

            int currentPoints = 0;
            switch(question.getDifficulty()) {
                case EASY:
                    currentPoints=5;
                    break;
                case MODERATE:
                    currentPoints=15;
                    break;
                case HARD:
                    currentPoints=25;
                    break;
            }

            rate = rate + currentPoints; // / 100;

        }

        return rate;
    }


    private boolean checkIfQuizValid(List<QuestionQuiz> questionQuizList) {
        return questionQuizList.size() >= 3;
    }

    private boolean checkIfQuestionsValid(List<QuestionQuiz> questionQuizList) {
        List<Answer> answers;

        for (QuestionQuiz questionQuiz : questionQuizList) {
            answers = answerRepository.findCorrectAnswersByQuestionId(questionQuiz.getIdQuestion());

            if (answers.isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
