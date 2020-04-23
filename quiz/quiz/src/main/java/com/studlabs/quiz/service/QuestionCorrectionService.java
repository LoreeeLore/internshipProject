package com.studlabs.quiz.service;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class QuestionCorrectionService {

    private QuestionCorrectionRepository questionCorrectionRepository;

    public QuestionCorrectionService(QuestionCorrectionRepository questionCorrectionRepository) {
        this.questionCorrectionRepository = questionCorrectionRepository;
    }

    public boolean exists(String idUser, int idQuestion) {
        return questionCorrectionRepository.exists(idUser, idQuestion);
    }

    public List<QuestionCorrection> findAll() {
        return questionCorrectionRepository.findAll();
    }

    public void insertQuestionCorrection(QuestionCorrection questionCorrection) {
        questionCorrectionRepository.insertQuestionCorrection(questionCorrection);
    }

    public void updateQuestionCorrection(QuestionCorrection questionCorrection) {
        questionCorrectionRepository.updateQuestionCorrection(questionCorrection);
    }

    public void deleteQuestionCorrection(String idUser, int idQuestion) {
        questionCorrectionRepository.deleteQuestionCorrection(idUser, idQuestion);
    }
}
