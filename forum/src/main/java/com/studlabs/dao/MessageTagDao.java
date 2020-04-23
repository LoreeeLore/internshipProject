package com.studlabs.dao;

import com.studlabs.bll.exceptions.DaoException;
import com.studlabs.bll.exceptions.InvalidParamException;
import com.studlabs.bll.model.MessageTag;

public interface MessageTagDao {
    /**
     * @throws com.studlabs.bll.exceptions.DaoException          database error
     * @throws com.studlabs.bll.exceptions.InvalidParamException bad username or message_id
     */
    void deleteTag(MessageTag messageTag) throws DaoException, InvalidParamException;

    /**
     * @throws com.studlabs.bll.exceptions.DaoException          database error
     * @throws com.studlabs.bll.exceptions.InvalidParamException bad username or message_id
     */
    void addTag(MessageTag messageTag) throws DaoException, InvalidParamException;
}
