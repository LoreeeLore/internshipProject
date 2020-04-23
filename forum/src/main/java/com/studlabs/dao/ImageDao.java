package com.studlabs.dao;

import com.studlabs.bll.exceptions.DaoException;
import com.studlabs.bll.exceptions.InvalidParamException;
import com.studlabs.bll.model.Image;

import java.util.List;

public interface ImageDao {
    /**
     * @throws DaoException database error
     */
    List<Image> findByMessageId(int messageId) throws DaoException;

    /**
     * @throws DaoException database error
     */
    Image save(Image object) throws DaoException;

    /**
     * @throws DaoException database error
     */
    void remove(int id) throws DaoException, InvalidParamException;

}
