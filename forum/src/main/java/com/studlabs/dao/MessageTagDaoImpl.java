package com.studlabs.dao;

import com.studlabs.bll.exceptions.DaoException;
import com.studlabs.bll.exceptions.InvalidParamException;
import com.studlabs.bll.model.MessageTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class MessageTagDaoImpl implements MessageTagDao {
    private static final Logger logger = LoggerFactory.getLogger(MessageTagDaoImpl.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void deleteTag(MessageTag messageTag) throws DaoException, InvalidParamException {

        try {
            if (jdbcTemplate.update("DELETE FROM message_tag_user WHERE user=? AND message_id=?",
                    messageTag.getUser(),
                    messageTag.getMessageId()) == 0) {
                throw new InvalidParamException("Bad id");
            }
        } catch (DataAccessException e) {
            logger.warn(e.getMessage());
            throw new DaoException("cannot deleteElasticsearchEntry tag");
        }
    }

    @Override
    public void addTag(MessageTag messageTag) throws DaoException, InvalidParamException {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);

        insert.setTableName("message_tag_user");
        Map<String, Object> map = new HashMap<>();
        map.put("user", messageTag.getUser());
        map.put("message_id", messageTag.getMessageId());
        try {
            insert.execute(map);
        } catch (DataIntegrityViolationException e) {
            logger.warn(e.getMessage());
            throw new InvalidParamException("cannot add tag - bad parameters");
        } catch (DataAccessException e) {
            logger.warn(e.getMessage());
            throw new DaoException("cannot add tag");
        }
    }

}
