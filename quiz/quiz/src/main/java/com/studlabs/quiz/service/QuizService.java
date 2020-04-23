package com.studlabs.quiz.service;

import com.studlabs.quiz.exception.*;
import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.*;
import org.springframework.dao.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.*;
import org.springframework.transaction.support.*;

import java.util.*;
import java.util.concurrent.*;

@Service
public class QuizService {

    private QuizRepository quizRepository;
    private QuestionRepository questionRepository;
    private QuestionQuizRepository questionQuizRepository;
    private PlatformTransactionManager platformTransactionManager;
    private static final String ALL = "all";

    public QuizService(QuizRepository quizRepository, QuestionRepository questionRepository, QuestionQuizRepository questionQuizRepository,
                       PlatformTransactionManager platformTransactionManager) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.questionQuizRepository = questionQuizRepository;
        this.platformTransactionManager = platformTransactionManager;
    }

    public Quiz findById(int id) {
        return quizRepository.findById(id);
    }

    public List<Quiz> findAll() {
        return quizRepository.findAll();
    }

    public List<Quiz> filterQuizzesByFields(List<QuizDifficulty> difficulties, List<String> categories) {
        return quizRepository.filterQuizzesByFields(difficulties, categories);
    }

    public List<Quiz> browseAllQuizzes(String idUser) {
        return quizRepository.browseAllQuizzes(idUser);
    }

    public List<Quiz> browsePrivateQuizzes(String idUser) {
        return quizRepository.browsePrivateQuizzes(idUser);
    }

    public List<Quiz> browsePublicQuizzes() {
        return quizRepository.browsePublicQuizzes();
    }

    public int insert(Quiz quiz) {
        return quizRepository.insert(quiz);
    }

    public void update(Quiz quiz) {
        quizRepository.update(quiz);
    }

    public void setPublic(int idQuiz, boolean isPublic) {
        quizRepository.setPublic(idQuiz, isPublic);
    }

    public void delete(int idQuiz) {
        quizRepository.delete(idQuiz);
    }

    public boolean exists(int id) {
        return quizRepository.exists(id);
    }

    public void generateQuiz(String category, int numberOfQuestions, long timeInMinutes) throws DataTransactionException {
        TransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = platformTransactionManager.getTransaction(def);

        try {
            int idQuiz = quizRepository.generateQuiz(category,timeInMinutes);
            generateQuestionsForQuiz(idQuiz, numberOfQuestions, category);

            platformTransactionManager.commit(status);
        } catch (DataAccessException e) {
            platformTransactionManager.rollback(status);
            throw new DataTransactionException("Failed to make transaction", e);
        }
    }

    private void generateQuestionsForQuiz(int idQuiz, int numberOfQuestions, String category) {
        List<Question> questions = retrieveQuestionsByCategory(category);

        if (numberOfQuestions >= questions.size()) {
            for (Question question : questions) {
                questionQuizRepository.assignQuestionToQuiz(idQuiz, question.getIdQuestion());
            }
        } else {
            for (int i = 0; i < numberOfQuestions; i++) {
                int questionNumber = ThreadLocalRandom.current().nextInt(questions.size());

                questionQuizRepository.assignQuestionToQuiz(idQuiz, questions.get(questionNumber).getIdQuestion());
                questions.remove(questionNumber);
            }
        }
    }

    private List<Question> retrieveQuestionsByCategory(String category) {
        if (category.equalsIgnoreCase(ALL)) {
            return questionRepository.findAll();
        }

        return questionRepository.findQuestionsByCategory(category);
    }
}
