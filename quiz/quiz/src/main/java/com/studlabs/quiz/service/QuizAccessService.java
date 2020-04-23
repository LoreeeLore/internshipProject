package com.studlabs.quiz.service;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class QuizAccessService {
    private QuizAccessRepository quizAccessRepository;

    public QuizAccessService(QuizAccessRepository quizAccessRepository) {
        this.quizAccessRepository = quizAccessRepository;
    }

    public List<QuizAccess> findAll() {
        return quizAccessRepository.findAll();
    }

    public void assignQuizToUser(String idUser, int idQuiz) {
        quizAccessRepository.assignQuizToUser(idUser, idQuiz);
    }

    public boolean exists(String idUser, int idQuiz) {
        return quizAccessRepository.exists(idUser, idQuiz);
    }

    public void deleteUserFromQuiz(String idUser, int idQuiz) {
        quizAccessRepository.deleteUserFromQuiz(idUser, idQuiz);
    }

}
