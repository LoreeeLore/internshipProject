package com.studlabs.dao;

import com.studlabs.bll.exceptions.DaoException;
import com.studlabs.bll.exceptions.InvalidParamException;
import com.studlabs.bll.exceptions.NoRatingException;
import com.studlabs.bll.model.Rating;
import com.studlabs.dao.mappers.RatingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class RatingDaoImpl implements RatingDao {

    private static final Logger logger = LoggerFactory.getLogger(RatingDaoImpl.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Rating> findById(int messageId, String user) throws DaoException {
        List<Rating> ratings;

        try {
            ratings = jdbcTemplate.query("SELECT * FROM rating WHERE (message_id = ? AND user= ?)",
                    new RatingMapper(), messageId, user);
        } catch (DataAccessException e) {
            logger.warn(e.getMessage());
            throw new DaoException("database error - cannot find rating by messageId " + messageId
                    + " user " + user, e);
        }
        logger.info("Found rating with messageId {} and user {}", messageId, user);
        return ratings.isEmpty() ? Optional.empty() : Optional.of(ratings.get(0));
    }

    @Override
    public Rating save(Rating rating) throws DaoException, InvalidParamException {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
        insert.setTableName("rating");

        Map<String, Object> map = new HashMap<>();
        map.put("message_id", rating.getMessageId());
        map.put("user", rating.getUser());
        map.put("type", rating.getType());

        try {
            insert.execute(map);
        } catch (DataIntegrityViolationException e) {
            logger.warn(e.getMessage());
            throw new InvalidParamException("cannot save rating - bad parameters");
        } catch (DataAccessException e) {
            logger.warn(e.getMessage());
            throw new DaoException("cannot save rating - database error", e);
        }

        logger.info("Persisted rating");
        return rating;
    }

    @Override
    public void update(Rating object) throws DaoException, NoRatingException {
        String sql = "update Rating set type = ? where (message_id = ? and user = ?)";

        try {
            if (jdbcTemplate.update(sql, object.getType().name(),
                    object.getMessageId(), object.getUser()) == 0) {
                throw new NoRatingException("inexistent rating");
            }
        } catch (DataAccessException e) {
            logger.warn(e.getMessage());
            throw new DaoException("cannot update rating with messageId "
                    + object.getMessageId() + " +userId of " + object.getUser(), e);
        }

        logger.info("Updated rating with messageID = {} and userID = {}", object.getMessageId(), object.getUser());
    }

    @Override
    public void remove(int messageId, String user) throws DaoException, NoRatingException {
        String SQL = "DELETE FROM rating WHERE (message_id = ? AND user = ?)";

        try {
            if (jdbcTemplate.update(SQL, messageId, user) == 0) {
                throw new NoRatingException("Bad id");
            }
        } catch (DataAccessException e) {
            logger.warn(e.getMessage());
            throw new DaoException("cannot remove rating", e);
        }

        logger.info("Deleted rating");
    }

}
