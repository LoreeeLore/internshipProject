package com.studlabs.bll.services;

import com.studlabs.bll.exceptions.*;
import com.studlabs.bll.model.ForumThread;
import com.studlabs.dao.ThreadDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ThreadServiceImpl implements ThreadService {
    private static final Logger logger = LoggerFactory.getLogger(ThreadServiceImpl.class);


    @Autowired
    private ThreadDao threadDaoImpl;

    @Override
    public List<ForumThread> findAllOpen() throws BllException {
        try {
            return threadDaoImpl.findAllOpen();
        } catch (DaoException e) {
            logger.warn(e.getMessage());
            throw new BllException(e.getMessage(), e);
        }
    }

    @Override
    public ForumThread save(ForumThread object) throws BllException, BadRequestException {
        try {
            //check if thread is private, then it must contain a user
            if (object.getAccess().equals("private") && object.getUser() == null) {
                logger.warn("private thread must include a user");
                throw new BadRequestException("private thread must include a user");
            }
            return threadDaoImpl.save(object);
        } catch (DaoException e) {
            logger.warn(e.getMessage());
            throw new BllException(e.getMessage(), e);
        }
    }

    @Override
    public void update(ForumThread object) throws NotFoundException, BllException {
        try {
            threadDaoImpl.update(object);
        } catch (NoThreadException e) {
            logger.warn(e.getMessage());
            throw new NotFoundException(e.getMessage(), e);
        } catch (DaoException e) {
            logger.warn(e.getMessage());
            throw new BllException(e.getMessage(), e);
        }
    }

    @Override
    public void remove(int id) throws NotFoundException, BllException {
        try {
            threadDaoImpl.remove(id);
        } catch (NoThreadException e) {
            logger.warn(e.getMessage());
            throw new NotFoundException(e.getMessage(), e);
        } catch (DaoException e) {
            logger.warn(e.getMessage());
            throw new BllException(e.getMessage(), e);
        }
    }

    @Override
    public Optional<ForumThread> findById(int id) throws NotFoundException, BllException {
        try {
            return threadDaoImpl.findById(id);
        } catch (NoThreadException e) {
            logger.warn(e.getMessage());
            throw new NotFoundException(e.getMessage(), e);
        } catch (DaoException e) {
            logger.warn(e.getMessage());
            throw new BllException(e.getMessage(), e);
        }
    }

    @Override
    public List<ForumThread> filterThreads(Optional<String> category,
                                           Optional<List<String>> tags,
                                           Optional<String> sortBy,
                                           Optional<String> order,
                                           Optional<Integer> limit,
                                           Optional<Integer> offset) throws BadRequestException, BllException {
        try {
            return threadDaoImpl.filterThreads(category, tags, sortBy, order, limit, offset);
        } catch (InvalidParamException e) {
            logger.warn(e.getMessage());
            throw new BadRequestException(e.getMessage(), e);
        } catch (DaoException e) {
            logger.warn(e.getMessage());
            throw new BllException(e.getMessage(), e);
        }
    }

    @Override
    public List<String> getUsers(int id) throws NotFoundException, BllException, BadRequestException {
        try {
            return threadDaoImpl.getUsers(id);
        } catch (InvalidParamException e) {
            logger.warn(e.getMessage());
            throw new BadRequestException(e.getMessage(), e);
        } catch (NoThreadException e) {
            logger.warn(e.getMessage());
            throw new NotFoundException(e.getMessage(), e);
        } catch (DaoException e) {
            logger.warn(e.getMessage());
            throw new BllException(e.getMessage(), e);
        }
    }

    @Override
    public void addUsers(int id, List<String> users) throws NotFoundException,
            BllException, BadRequestException {
        try {
            threadDaoImpl.addUsers(id, users);
        } catch (InvalidParamException e) {
            logger.warn(e.getMessage());
            throw new BadRequestException(e.getMessage(), e);
        } catch (NoThreadException e) {
            logger.warn(e.getMessage());
            throw new NotFoundException(e.getMessage(), e);
        } catch (DaoException e) {
            logger.warn(e.getMessage());
            throw new BllException(e.getMessage(), e);
        }
    }

    @Override
    public void removeUsers(int id, List<String> users) throws NotFoundException,
            BllException, BadRequestException {
        try {
            threadDaoImpl.removeUsers(id, users);
        } catch (InvalidParamException e) {
            logger.warn(e.getMessage());
            throw new BadRequestException(e.getMessage(), e);
        } catch (NoThreadException e) {
            logger.warn(e.getMessage());
            throw new NotFoundException(e.getMessage(), e);
        } catch (DaoException e) {
            logger.warn(e.getMessage());
            throw new BllException(e.getMessage(), e);
        }
    }

    @Override
    public void close(int id) throws NotFoundException, BllException {
        try {
            threadDaoImpl.close(id);
        } catch (NoThreadException e) {
            logger.warn(e.getMessage());
            throw new NotFoundException(e.getMessage(), e);
        } catch (DaoException e) {
            logger.warn(e.getMessage());
            throw new BllException(e.getMessage(), e);
        }
    }

    @Override
    public void validate(int id) throws NotFoundException, BllException {
        try {
            threadDaoImpl.validate(id);
        } catch (NoThreadException e) {
            logger.warn(e.getMessage());
            throw new NotFoundException(e.getMessage(), e);
        } catch (DaoException e) {
            logger.warn(e.getMessage());
            throw new BllException(e.getMessage(), e);
        }
    }
}
