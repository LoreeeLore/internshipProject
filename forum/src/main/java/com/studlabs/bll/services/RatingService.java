package com.studlabs.bll.services;

import com.studlabs.bll.exceptions.BadRequestException;
import com.studlabs.bll.exceptions.BllException;
import com.studlabs.bll.exceptions.NotFoundException;
import com.studlabs.bll.model.Rating;

import java.util.Optional;

public interface RatingService {
    /**
     * @throws com.studlabs.bll.exceptions.BllException        other error
     * @throws com.studlabs.bll.exceptions.BadRequestException invalid request
     */
    Rating save(Rating object) throws BllException, BadRequestException;

    /**
     * @throws com.studlabs.bll.exceptions.BllException      other error
     * @throws com.studlabs.bll.exceptions.NotFoundException invalid request
     */
    void update(Rating object) throws BllException, NotFoundException;

    /**
     * @throws com.studlabs.bll.exceptions.BllException      other error
     * @throws com.studlabs.bll.exceptions.NotFoundException invalid request
     */
    void remove(int messageId, String user) throws BllException, NotFoundException;

    /**
     * @throws com.studlabs.bll.exceptions.BllException other error
     */
    Optional<Rating> findById(int messageId, String user) throws BllException;
}
