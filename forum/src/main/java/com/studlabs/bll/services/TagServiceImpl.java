package com.studlabs.bll.services;

import com.studlabs.bll.exceptions.BllException;
import com.studlabs.bll.exceptions.DaoException;
import com.studlabs.bll.model.Tag;
import com.studlabs.controllers.TagController;
import com.studlabs.dao.TagDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    private static final Logger logger = LoggerFactory.getLogger(TagController.class);

    @Autowired
    private TagDao tagDao;

    @Override
    public List<Tag> getAll() throws BllException {
        try {
            return tagDao.findAll();
        } catch (DaoException e) {
            throw new BllException(e.getMessage(), e);
        }
    }

    @Override
    public void saveTagList(List<String> tags) throws BllException {
        try {
            tagDao.saveTagList(tags);
        } catch (DaoException e) {
            logger.warn(e.getMessage());
            throw new BllException(e.getMessage(), e);
        }
    }

    @Override
    public void deleteTagList(List<String> tags) throws BllException {
        try {
            tagDao.deleteTagList(tags);
        } catch (DaoException e) {
            logger.warn(e.getMessage());
            throw new BllException(e.getMessage(), e);
        }
    }
}
