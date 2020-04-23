package com.studlabs.quiz.service;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class PlayQuestionService {

    private PlayQuestionRepository playQuestionRepository;

    @Autowired
    public PlayQuestionService(PlayQuestionRepository playQuestionRepository) {
        this.playQuestionRepository = playQuestionRepository;
    }

    public int insert(PlayQuestion playQuestion) {
        return playQuestionRepository.insert(playQuestion);
    }

    public PlayQuestion findPlayQuestionById(int id) {
        return playQuestionRepository.findPlayQuestionById(id);
    }

    public List<PlayQuestion> findAllQuestionsForAQuiz(int idPlayQuiz) {
        return playQuestionRepository.findAllQuestionsForAQuiz(idPlayQuiz);
    }

    public boolean exists(int id) {
        return playQuestionRepository.exists(id);
    }

    public String getUser(int id) {
        return playQuestionRepository.getUser(id);
    }
}
