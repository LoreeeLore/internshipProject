package com.studlabs.bll.services;

import com.studlabs.bll.exceptions.BadRequestException;
import com.studlabs.bll.exceptions.BllException;
import com.studlabs.bll.exceptions.NoMessageException;
import com.studlabs.bll.exceptions.NotFoundException;
import com.studlabs.bll.model.Message;

import java.util.List;
import java.util.Optional;

public interface MessageService {
    /**
     * @throws com.studlabs.bll.exceptions.BadRequestException
     * @throws com.studlabs.bll.exceptions.BllException        other error
     */
    Message save(Message object) throws BllException, BadRequestException;

    /**
     * @throws com.studlabs.bll.exceptions.BllException        other error
     * @throws com.studlabs.bll.exceptions.NotFoundException
     * @throws com.studlabs.bll.exceptions.BadRequestException validation on images failed
     */
    void update(Message object) throws BllException, NotFoundException, BadRequestException;

    /**
     * @throws com.studlabs.bll.exceptions.BllException      other error
     * @throws com.studlabs.bll.exceptions.NotFoundException
     */
    void remove(int id) throws BllException, NotFoundException;

    /**
     * @throws com.studlabs.bll.exceptions.BllException other error
     */
    Optional<Message> findById(int id) throws BllException;

    /**
     * @throws com.studlabs.bll.exceptions.BllException       other error
     * @throws com.studlabs.bll.exceptions.NoMessageException no message exists for the thread
     */
    void checkIfMessageExists(int threadId, int msgId) throws BllException, NoMessageException;

    /**
     * @throws com.studlabs.bll.exceptions.BadRequestException
     * @throws com.studlabs.bll.exceptions.BllException        other error
     */
    List<Message> findAllSorted(int threadId, Optional<String> sortBy,
                                Optional<String> order, Optional<Integer> limit, Optional<Integer> offset) throws BllException, BadRequestException;

    /**
     * @throws com.studlabs.bll.exceptions.BllException other error
     */
    List<Message> findAll() throws BllException;

    void indexAllMessages() throws BllException;
}
