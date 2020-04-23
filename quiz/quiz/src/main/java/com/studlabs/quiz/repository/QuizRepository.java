package com.studlabs.quiz.repository;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.mapper.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.support.*;
import org.springframework.stereotype.*;

import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

@Repository
public class QuizRepository {

    public static final String ID = "id";
    public static final String ID_USER = "id_user";
    public static final String CATEGORY = "category";
    public static final String DIFFICULTY = "difficulty";
    public static final String IS_PUBLIC = "is_public";
    public static final String RATE = "rate";
    public static final String TIME = "time_in_minutes";
    public static final String IS_RANDOM = "is_random";
    public static final int NUMBER_OF_DIFFICULTIES = 5;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public QuizRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public Quiz findById(int id) {
        String sql = "SELECT * from quiz WHERE " + ID + "=:id";
        List<Quiz> result = namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource().addValue(ID, id), new QuizRowMapper());

        return result.isEmpty() ? null : result.get(0);
    }

    public boolean exists(int id) {
        String sql = "SELECT * from quiz WHERE " + ID + "=:id";
        List<Quiz> result = namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource().addValue(ID, id), new QuizRowMapper());

        return !result.isEmpty();
    }

    public List<Quiz> findAll() {
        String sql = "SELECT * from quiz";

        return namedParameterJdbcTemplate.query(sql, getSqlParameterSourceFromModel(null), new QuizRowMapper());
    }

    public List<Quiz> filterQuizzesByFields(List<QuizDifficulty> difficulties, List<String> categories) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<String> difficultyList = difficulties.stream().map(Enum::toString).collect(Collectors.toList());
        String sql = "SELECT * from quiz WHERE true ";
        if (!categories.isEmpty()) {
            parameters.addValue(CATEGORY, categories);
            sql += " and " + CATEGORY + " IN (:category)";
        }
        if (!difficulties.isEmpty()) {
            parameters.addValue(DIFFICULTY, difficultyList);
            sql += " and " + DIFFICULTY + " IN (:difficulty)";
        }
        return namedParameterJdbcTemplate.query(sql, parameters, new QuizRowMapper());
    }

    public List<Quiz> browsePublicQuizzes() {

        String sql = "SELECT * from quiz WHERE " + IS_PUBLIC + "=true";

        return namedParameterJdbcTemplate.query(sql, getSqlParameterSourceFromModel(null), new QuizRowMapper());
    }

    public List<Quiz> browsePrivateQuizzes(String idUser) {

        String sql = "SELECT * from quiz WHERE id IN (SELECT id_quiz from quiz_access WHERE " + ID_USER + "=:id_user)";

        return namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource().addValue(ID_USER, idUser), new QuizRowMapper());
    }

    public List<Quiz> browseAllQuizzes(String idUser) {

        List<Quiz> allQuizzes = this.browsePublicQuizzes();
        List<Quiz> privateQuizzes = this.browsePrivateQuizzes(idUser);

        allQuizzes.addAll(privateQuizzes);
        return allQuizzes;
    }

    public int insert(Quiz quiz) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sql = MessageFormat.format("INSERT INTO quiz({0}, {1}, {2}, {3}, {4}, {5}) VALUES (:{0}, :{1}, :{2}, :{3}, :{4}, :{5})",
                CATEGORY, DIFFICULTY, RATE, TIME, IS_PUBLIC,  IS_RANDOM);

        namedParameterJdbcTemplate.update(sql, getSqlParameterSourceFromModel(quiz), keyHolder);

        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : 0;
    }

    public void update(Quiz quiz) {
        String sql = MessageFormat.format("UPDATE quiz SET {0}=:{0},  {1}=:{1},  {2}=:{2}, {3}=:{3}, {4}=:{4}, {5}=:{5} WHERE  {6}=:{6}",
                CATEGORY, DIFFICULTY, IS_PUBLIC, RATE, TIME, IS_RANDOM, ID);

        namedParameterJdbcTemplate.update(sql, getSqlParameterSourceFromModel(quiz));
    }

    public void setPublic(int id, boolean isPublic) {
        String sql = "UPDATE quiz SET " + IS_PUBLIC + "=:is_public WHERE " + ID + " =:id";

        namedParameterJdbcTemplate.update(sql, getSqlParameterByParameter(id, isPublic));
    }

    public void delete(int idQuiz) {
        String sql = MessageFormat.format("DELETE FROM quiz WHERE {0}=:{0}", ID);
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource().addValue(ID, idQuiz));
    }

    public int generateQuiz(String category, long timeInMinutes) {
        QuizDifficulty quizDifficulty = QuizDifficulty.values()[ThreadLocalRandom.current().nextInt(NUMBER_OF_DIFFICULTIES)];
        double completionRate = quizDifficulty.getNeededCompletionRate();

        String sqlInsertQuiz = "INSERT INTO quiz(" + CATEGORY + "," + DIFFICULTY + "," + IS_PUBLIC + "," +
                RATE + "," + TIME  + "," + IS_RANDOM + ") " +
                "VALUES(:category, :difficulty, :is_public, :rate, :time_in_minutes, :is_random)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sqlInsertQuiz, getSqlParameterSourceForGenerateQuiz(category, quizDifficulty, completionRate, timeInMinutes), keyHolder);

        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : 0;
    }

    private SqlParameterSource getSqlParameterSourceForGenerateQuiz(String category, QuizDifficulty quizDifficulty, double completionRate, long timeInMinutes) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        parameterSource.addValue(CATEGORY, category);
        parameterSource.addValue(DIFFICULTY, quizDifficulty.toString());
        parameterSource.addValue(IS_PUBLIC, true);
        parameterSource.addValue(RATE, completionRate);
        parameterSource.addValue(TIME, timeInMinutes);
        parameterSource.addValue(IS_RANDOM, true);

        return parameterSource;
    }

    private SqlParameterSource getSqlParameterSourceFromModel(Quiz quiz) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        if (quiz != null) {
            parameterSource.addValue(ID, quiz.getIdQuiz());
            parameterSource.addValue(CATEGORY, quiz.getCategory());

            if (quiz.getDifficulty() != null) {
                parameterSource.addValue(DIFFICULTY, quiz.getDifficulty().toString());
            } else {
                parameterSource.addValue(DIFFICULTY, quiz.getDifficulty());

            }

            parameterSource.addValue(IS_PUBLIC, quiz.isPublic());
            parameterSource.addValue(RATE, quiz.getCompletionRate());
            parameterSource.addValue(TIME, quiz.getTimeInMinutes());
            parameterSource.addValue(IS_RANDOM, quiz.isRandom());

        }
        return parameterSource;
    }

    private SqlParameterSource getSqlParameterByParameter(int idQuiz, boolean isPublic) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        parameterSource.addValue(ID, idQuiz);
        parameterSource.addValue(IS_PUBLIC, isPublic);
        return parameterSource;
    }

}
