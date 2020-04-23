package com.studlabs.bll.services;

import com.studlabs.bll.exceptions.BadRequestException;
import com.studlabs.bll.exceptions.BllException;
import com.studlabs.bll.exceptions.NotFoundException;
import com.studlabs.bll.model.ForumThread;

import java.util.List;
import java.util.Optional;

public interface ThreadService {

    /**
     * @throws com.studlabs.bll.exceptions.BllException        if any database problem exists
     * @throws com.studlabs.bll.exceptions.BadRequestException if parameters are malformed
     */
    List<ForumThread> filterThreads(Optional<String> category, Optional<List<String>> tags, Optional<String> sortBy, Optional<String> order,
                                    Optional<Integer> limit, Optional<Integer> offset) throws BllException, BadRequestException;

    /**
     * @throws com.studlabs.bll.exceptions.BllException if any database problem exists
     */
    List<ForumThread> findAllOpen() throws BllException;

    /**
     * @throws com.studlabs.bll.exceptions.BllException        if any database problem exists
     * @throws com.studlabs.bll.exceptions.BadRequestException private thread without user field
     */
    ForumThread save(ForumThread object) throws BllException, BadRequestException;

    /**
     * @throws com.studlabs.bll.exceptions.NoThreadException if thread doesn't exist
     * @throws com.studlabs.bll.exceptions.BllException      if any database problem exists
     */
    void update(ForumThread object) throws NotFoundException, BllException;

    /**
     * @throws com.studlabs.bll.exceptions.NoThreadException if thread doesn't exist
     * @throws com.studlabs.bll.exceptions.BllException      if any database problem exists
     */
    void remove(int id) throws NotFoundException, BllException;

    /**
     * @throws com.studlabs.bll.exceptions.NoThreadException if thread doesn't exist
     * @throws com.studlabs.bll.exceptions.BllException      if any database problem exists
     */
    Optional<ForumThread> findById(int id) throws NotFoundException, BllException;

    /**
     * @throws com.studlabs.bll.exceptions.NoThreadException   if thread doesn't exist
     * @throws com.studlabs.bll.exceptions.BllException        if any database problem exists
     * @throws com.studlabs.bll.exceptions.BadRequestException if thread is public
     */
    List<String> getUsers(int id) throws NotFoundException, BllException, BadRequestException;

    /**
     * @throws com.studlabs.bll.exceptions.NoThreadException   if thread doesn't exist
     * @throws com.studlabs.bll.exceptions.BllException        if any database problem exists
     * @throws com.studlabs.bll.exceptions.BadRequestException users do not exist or if thread is public
     */
    void addUsers(int id, List<String> users) throws NotFoundException, BllException, BadRequestException;

    /**
     * @throws com.studlabs.bll.exceptions.NoThreadException   if thread doesn't exist
     * @throws com.studlabs.bll.exceptions.BllException        if any database problem exists
     * @throws com.studlabs.bll.exceptions.BadRequestException users do not exist OR user is not in privacy list
     *                                                            or if thread is public
     */
    void removeUsers(int id, List<String> users) throws NotFoundException, BllException, BadRequestException;

    /**
     * @throws com.studlabs.bll.exceptions.NoThreadException if thread doesn't exist
     * @throws com.studlabs.bll.exceptions.BllException      if any database problem exists
     */
    void close(int id) throws NotFoundException, BllException;

    /**
     * @throws com.studlabs.bll.exceptions.NoThreadException if thread doesn't exist
     * @throws com.studlabs.bll.exceptions.BllException      if any database problem exists
     */
    void validate(int id) throws NotFoundException, BllException;

}
