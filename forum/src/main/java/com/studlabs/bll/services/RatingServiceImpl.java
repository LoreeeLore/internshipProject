package com.studlabs.bll.services;

import com.studlabs.bll.exceptions.*;
import com.studlabs.bll.model.Rating;
import com.studlabs.dao.RatingDaoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RatingServiceImpl implements RatingService {
    private static final Logger logger = LoggerFactory.getLogger(RatingServiceImpl.class);

    @Autowired
    private RatingDaoImpl ratingDaoImpl;

    @Override
    @SuppressWarnings("Duplicates")
    public Rating save(Rating rating) throws BllException,
            BadRequestException {
        try {
            return ratingDaoImpl.save(rating);
        } catch (InvalidParamException e) {
            logger.warn(e.getMessage());
            throw new BadRequestException(e.getMessage(), e);
        } catch (DaoException e) {
            logger.warn(e.getMessage());
            throw new BllException(e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings("Duplicates")
    public void update(Rating object) throws BllException,
            NotFoundException {
        try {
            ratingDaoImpl.update(object);
        } catch (NoRatingException e) {
            logger.warn(e.getMessage());
            throw new NotFoundException(e.getMessage(), e);
        } catch (DaoException e) {
            logger.warn(e.getMessage());
            throw new BllException(e.getMessage(), e);
        }
    }

    @Override
    public void remove(int messageId, String user) throws BllException, NotFoundException {
        try {
            ratingDaoImpl.remove(messageId, user);
        } catch (NoRatingException e) {
            logger.warn(e.getMessage());
            throw new NotFoundException(e.getMessage(), e);
        } catch (DaoException e) {
            logger.warn(e.getMessage());
            throw new BllException(e.getMessage(), e);
        }
    }

    @Override
    public Optional<Rating> findById(int messageId, String user) throws BllException {
        try {
            return ratingDaoImpl.findById(messageId, user);
        } catch (DaoException e) {
            logger.warn(e.getMessage());
            throw new BllException(e.getMessage(), e);
        }
    }

}

