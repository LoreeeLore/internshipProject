package com.studlabs.dao;

import com.studlabs.bll.exceptions.DaoException;
import com.studlabs.bll.exceptions.InvalidParamException;
import com.studlabs.bll.exceptions.NoThreadException;
import com.studlabs.bll.model.ForumThread;

import java.util.List;
import java.util.Optional;

public interface ThreadDao {

    /**
     * @throws com.studlabs.bll.exceptions.InvalidParamException if parameters are invalid
     * @throws com.studlabs.bll.exceptions.DaoException          if any database problem exists
     */
    List<ForumThread> filterThreads(Optional<String> category,
                                    Optional<List<String>> tags,
                                    Optional<String> sortBy,
                                    Optional<String> order,
                                    Optional<Integer> limit,
                                    Optional<Integer> offset) throws DaoException, InvalidParamException;

    /**
     * @throws com.studlabs.bll.exceptions.DaoException if any database problem exists
     */
    List<ForumThread> findAll() throws DaoException;

    /**
     * @throws com.studlabs.bll.exceptions.DaoException if any database problem exists
     */
    List<ForumThread> findAllOpen() throws DaoException;

    /**
     * @throws com.studlabs.bll.exceptions.DaoException if any database problem exists
     */
    ForumThread save(ForumThread object) throws DaoException;


    /**
     * @throws com.studlabs.bll.exceptions.NoThreadException if thread doesn't exist
     * @throws com.studlabs.bll.exceptions.DaoException      if any database problem exists
     */
    void update(ForumThread object) throws DaoException, NoThreadException;

    /**
     * @throws com.studlabs.bll.exceptions.NoThreadException if doesn't exist any thread with {@param id}
     * @throws com.studlabs.bll.exceptions.DaoException      if any database problem exists
     */
    void remove(int id) throws DaoException, NoThreadException;

    /**
     * @throws com.studlabs.bll.exceptions.NoThreadException if doesn't exist any thread with {@param id}
     * @throws com.studlabs.bll.exceptions.DaoException      if any database problem exists
     */
    Optional<ForumThread> findById(int id) throws DaoException, NoThreadException;

    /**
     * @throws com.studlabs.bll.exceptions.NoThreadException     if doesn't exist any thread with {@param id}
     * @throws com.studlabs.bll.exceptions.DaoException          if any database problem exists
     * @throws com.studlabs.bll.exceptions.InvalidParamException if thread is public
     */
    List<String> getUsers(int id) throws DaoException, NoThreadException, InvalidParamException;

    /**
     * @throws com.studlabs.bll.exceptions.NoThreadException if doesn't exist any thread with {@param id}
     * @throws com.studlabs.bll.exceptions.DaoException      if any database problem exists
     */
    void close(int id) throws DaoException, NoThreadException;

    /**
     * @throws com.studlabs.bll.exceptions.NoThreadException     if doesn't exist any thread with {@param id}
     * @throws com.studlabs.bll.exceptions.DaoException          if any database problem exists
     * @throws com.studlabs.bll.exceptions.InvalidParamException if users do not exist or if thread is public
     */
    void addUsers(int id, List<String> users) throws DaoException, NoThreadException, InvalidParamException;

    /**
     * @throws com.studlabs.bll.exceptions.NoThreadException     if doesn't exist any thread with {@param id}
     * @throws com.studlabs.bll.exceptions.DaoException          if any database problem exists
     * @throws com.studlabs.bll.exceptions.InvalidParamException users do not exist OR user is not in privacy list
     *                                                              or if thread is public
     */
    void removeUsers(int id, List<String> users) throws DaoException, NoThreadException, InvalidParamException;

    /**
     * @throws com.studlabs.bll.exceptions.NoThreadException if doesn't exist any thread with {@param id}
     * @throws com.studlabs.bll.exceptions.DaoException      if any database problem exists
     */
    void validate(int id) throws DaoException, NoThreadException;
}
