package com.studlabs.dao;

import com.studlabs.bll.exceptions.DaoException;
import com.studlabs.bll.exceptions.InvalidParamException;
import com.studlabs.bll.exceptions.NoMessageException;
import com.studlabs.bll.model.Message;

import java.util.List;
import java.util.Optional;


public interface MessageDao {
    /**
     * @throws com.studlabs.bll.exceptions.InvalidParamException search parameters are invalid(limit or offset are
     *                                                              smaller than 0, invalid sortBy field, invalid ordering)
     * @throws com.studlabs.bll.exceptions.DaoException          database error
     */
    List<Message> findAll(int threadId, Optional<String> sortBy, Optional<String> order,
                          Optional<Integer> limit, Optional<Integer> offset)
            throws DaoException, InvalidParamException;

    /**
     * @throws com.studlabs.bll.exceptions.DaoException database error
     */
    Optional<Message> getMessageFromThread(int threadId, int msgId)
            throws DaoException;

    /**
     * @throws com.studlabs.bll.exceptions.DaoException database error
     */
    List<Message> findAll()
            throws DaoException;

    /**
     * @throws com.studlabs.bll.exceptions.InvalidParamException username is invalid
     * @throws com.studlabs.bll.exceptions.DaoException          database error
     */
    Message save(Message object)
            throws DaoException, InvalidParamException;

    /**
     * @throws com.studlabs.bll.exceptions.InvalidParamException username is invalid or image limit was exceeded or message does not exist
     * @throws com.studlabs.bll.exceptions.DaoException          database error
     * @throws com.studlabs.bll.exceptions.NoMessageException    message does not exist
     */
    void update(Message object)
            throws DaoException, InvalidParamException, NoMessageException;

    /**
     * @throws com.studlabs.bll.exceptions.DaoException       database error
     * @throws com.studlabs.bll.exceptions.NoMessageException message does not exist
     */
    void remove(int id)
            throws DaoException, NoMessageException;

    /**
     * @throws com.studlabs.bll.exceptions.DaoException database error
     */
    Optional<Message> findById(int id)
            throws DaoException;
}
