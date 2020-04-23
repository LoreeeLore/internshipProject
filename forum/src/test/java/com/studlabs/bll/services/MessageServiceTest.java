package com.studlabs.bll.services;

import com.studlabs.bll.exceptions.*;
import com.studlabs.bll.model.Message;
import com.studlabs.dao.MessageDaoImpl;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MessageServiceTest {
    @Mock
    private ThreadServiceImpl threadService;

    @Mock
    private MessageDaoImpl dao;

    @InjectMocks
    private MessageServiceImpl service;

    @Mock(name = "client")
    private RestHighLevelClient clientMock;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void save() throws Exception {
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        Message message = new Message(1, 1, "u", "salutare iQuest", aDateTime, 0, 0, new ArrayList<>(), new ArrayList<>());

        when(dao.save(message)).thenReturn(message);
        doReturn(new IndexResponse()).when(clientMock).index(Mockito.any(), Mockito.any());

        assertEquals(message, service.save(message));
        verify(clientMock).index(Mockito.any(), Mockito.any());

    }

    @Test
    public void update() throws Exception {
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        Message message = new Message(1, 1, "u", "salutare iQuest", aDateTime, 0, 0, new ArrayList<>(), new ArrayList<>());
        doReturn(new UpdateResponse()).when(clientMock).update(Mockito.any(), Mockito.any());

        service.update(message);
        verify(dao).update(message);

        verify(clientMock).update(Mockito.any(), Mockito.any());
    }

    @Test
    public void findByIdNull() throws Exception {
        when(dao.findById(1)).thenReturn(Optional.empty());
        assertEquals(Optional.empty(), service.findById(1));
    }

    @Test
    public void findById2() throws Exception {
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        Message message = new Message(1, 1, "u", "salutare iQuest", aDateTime, 0, 0);
        when(dao.findById(1)).thenReturn(Optional.of(message));
        assertEquals(message, service.findById(1).get());
    }

    @Test
    public void findAll() throws Exception {
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        LocalDateTime anotherDateTime = LocalDateTime.of(2016, Month.JULY, 29, 19, 30, 40);

        List<Message> messageList = new ArrayList<>();
        messageList.add(new Message(1, 1, "u", "salutare iQuest", aDateTime, 0, 0));
        messageList.add(new Message(1, 1, "u", "salutare iQuest", anotherDateTime, 0, 0));

        when(dao.findAll()).thenReturn(messageList);
        assertEquals(messageList, service.findAll());
    }

    @Test
    public void remove() throws Exception {
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        Message message = new Message(1, 1, "u", "salutare iQuest", aDateTime, 0, 0);
        doReturn(new DeleteResponse()).when(clientMock).delete(Mockito.any(), Mockito.any());


        service.remove(message.getId());
        verify(dao).remove(message.getId());
        verify(clientMock).delete(Mockito.any(), Mockito.any());

    }

    @Test(expected = NoMessageException.class)
    public void checkIfMessageExistsThreadNotFound() throws Exception {
        final int threadId = 2;
        final int msgId = 2;

        when(dao.getMessageFromThread(threadId, msgId)).thenReturn(Optional.empty());

        service.checkIfMessageExists(threadId, msgId);
    }

    @Test
    public void checkIfMessageExists() throws Exception {
        final int threadId = 2;
        final int msgId = 2;

        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        Message message = new Message(1, 1, "u", "salutare iQuest", aDateTime, 0, 0);

        when(dao.getMessageFromThread(threadId, msgId)).thenReturn(Optional.of(message));

        service.checkIfMessageExists(threadId, msgId);
    }

    @Test
    public void findAllSorted() throws Exception {
        final int threadId = 1;
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        LocalDateTime anotherDateTime = LocalDateTime.of(2016, Month.JULY, 29, 19, 30, 40);

        List<Message> messageList = new ArrayList<>();
        messageList.add(new Message(1, 1, "u", "salutare iQuest", aDateTime, 0, 0));
        messageList.add(new Message(1, 1, "u", "salutare iQuest", anotherDateTime, 0, 0));

        when(dao.findAll(threadId, Optional.of("text"), Optional.of("asc"), Optional.empty(), Optional.empty())).thenReturn(messageList);
        assertEquals(messageList, service.findAllSorted(threadId, Optional.of("text"), Optional.of("asc"), Optional.empty(), Optional.empty()));
    }

    //pagination

    @Test
    public void findAllPagination() throws Exception {
        final int threadId = 1;
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        LocalDateTime anotherDateTime = LocalDateTime.of(2016, Month.JULY, 29, 19, 30, 40);

        List<Message> messageList = new ArrayList<>();
        messageList.add(new Message(1, 1, "u", "salutare iQuest", aDateTime, 0, 0));
        messageList.add(new Message(1, 1, "u", "salutare iQuest", anotherDateTime, 0, 0));

        when(dao.findAll(threadId, Optional.of("text"), Optional.of("asc"), Optional.of(1), Optional.of(2))).thenReturn(messageList);
        assertEquals(messageList, service.findAllSorted(threadId, Optional.of("text"), Optional.of("asc"), Optional.of(1), Optional.of(2)));
    }

    //exceptions

    @Test(expected = BadRequestException.class)
    public void saveException() throws Exception {
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        Message message = new Message(1, 1, "u", "salutare iQuest", aDateTime, 0, 0, new ArrayList<>(), new ArrayList<>());
        when(dao.save(message)).thenThrow(new InvalidParamException());

        service.save(message);
    }

    @Test(expected = BadRequestException.class)
    public void updateException() throws Exception {
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        Message message = new Message(1, 1, "u", "salutare iQuest", aDateTime, 0, 0, new ArrayList<>(), new ArrayList<>());
        doThrow(new InvalidParamException()).when(dao).update(message);

        service.update(message);
    }

    @Test(expected = BllException.class)
    public void findByIdNullException() throws Exception {
        when(dao.findById(1)).thenThrow(new DaoException());
        service.findById(1);
    }

    @Test(expected = BllException.class)
    public void findAllException() throws Exception {
        final int threadId = 1;
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        LocalDateTime anotherDateTime = LocalDateTime.of(2016, Month.JULY, 29, 19, 30, 40);

        List<Message> messageList = new ArrayList<>();
        messageList.add(new Message(1, 1, "u", "salutare iQuest", aDateTime, 0, 0));
        messageList.add(new Message(1, 1, "u", "salutare iQuest", anotherDateTime, 0, 0));

        when(dao.findAll()).thenThrow(new DaoException());
        service.findAll();
    }

    @Test(expected = BllException.class)
    public void removeBllException() throws Exception {
        doThrow(new DaoException()).when(dao).remove(1);
        service.remove(1);
    }

    @Test(expected = NotFoundException.class)
    public void removeNotFoundException() throws Exception {
        doThrow(new NoMessageException()).when(dao).remove(1);
        service.remove(1);
    }

    @Test(expected = BllException.class)
    public void saveBllException() throws Exception {
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        Message message = new Message(1, 1, "u", "salutare iQuest", aDateTime, 0, 0, new ArrayList<>(), new ArrayList<>());
        when(dao.save(message)).thenThrow(new DaoException());

        service.save(message);
    }

    @Test(expected = BllException.class)
    public void updateBllException() throws Exception {
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        Message message = new Message(1, 1, "u", "salutare iQuest", aDateTime, 0, 0, new ArrayList<>(), new ArrayList<>());
        doThrow(new DaoException()).when(dao).update(message);

        service.update(message);
    }

    @Test(expected = NotFoundException.class)
    public void updateNotFoundException() throws Exception {
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        Message message = new Message(1, 1, "u", "salutare iQuest", aDateTime, 0, 0, new ArrayList<>(), new ArrayList<>());
        doThrow(new NoMessageException()).when(dao).update(message);

        service.update(message);
    }

}