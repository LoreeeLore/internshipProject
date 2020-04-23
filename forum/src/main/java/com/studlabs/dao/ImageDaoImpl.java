package com.studlabs.dao;

import com.studlabs.bll.exceptions.DaoException;
import com.studlabs.bll.exceptions.InvalidParamException;
import com.studlabs.bll.model.Image;
import com.studlabs.dao.mappers.ImageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ImageDaoImpl implements ImageDao {
    private static final Logger logger = LoggerFactory.getLogger(ImageDaoImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Image save(Image object) throws DaoException {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);

        byte[] imageByte = Base64.getDecoder().decode(object.getImage());

        Blob imageBlob = null;
        try {
            imageBlob = new SerialBlob(imageByte);
        } catch (SQLException e) {
            logger.warn(e.getMessage());
            throw new RuntimeException("System error");
        }

        insert.setTableName("image");
        insert.usingGeneratedKeyColumns("id");
        Map<String, Object> map = new HashMap<>();
        map.put("message_id", object.getMessageId());
        map.put("img_blob", imageBlob);

        try {
            object.setId(insert.executeAndReturnKey(map).intValue());
        } catch (DataAccessException e) {
            logger.warn(e.getMessage());
            throw new DaoException("cannot save image");
        }

        logger.info("Image added with id" + object.getId());

        return object;
    }

    @Override
    public void remove(int id) throws DaoException, InvalidParamException {
        try {
            if (jdbcTemplate.update("DELETE FROM image WHERE id = ?", id) == 0) {
                throw new InvalidParamException("Bad id");
            }
        } catch (DataAccessException e) {
            logger.warn(e.getMessage());
            throw new DaoException("cannot remove image");
        }
    }

    @Override
    public List<Image> findByMessageId(int messageId) throws DaoException {
        try {
            return jdbcTemplate.query("SELECT * FROM image where message_id = ?", new ImageMapper(), messageId);
        } catch (DataAccessException e) {
            logger.warn(e.getMessage());
            throw new DaoException("cannot find image by message_id " + messageId);
        }
    }
}
