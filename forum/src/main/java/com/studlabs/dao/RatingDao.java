package com.studlabs.dao;

import com.studlabs.bll.exceptions.DaoException;
import com.studlabs.bll.exceptions.InvalidParamException;
import com.studlabs.bll.exceptions.NoRatingException;
import com.studlabs.bll.model.Rating;

import java.util.Optional;

public interface RatingDao {
    /**
     * @throws com.studlabs.bll.exceptions.DaoException          database error
     * @throws com.studlabs.bll.exceptions.InvalidParamException bad messageId or user
     */
    Optional<Rating> findById(int messageId, String user) throws DaoException;

    /**
     * @throws com.studlabs.bll.exceptions.DaoException          database error
     * @throws com.studlabs.bll.exceptions.InvalidParamException bad messageId or user
     */
    Rating save(Rating rating) throws DaoException, InvalidParamException;

    /**
     * @throws com.studlabs.bll.exceptions.DaoException      database error
     * @throws com.studlabs.bll.exceptions.NoRatingException inexistent rating
     */
    void update(Rating object) throws DaoException, NoRatingException;

    /**
     * @throws com.studlabs.bll.exceptions.DaoException      database error
     * @throws com.studlabs.bll.exceptions.NoRatingException bad messageId or user
     */
    void remove(int messageId, String user) throws DaoException, NoRatingException;
}
