package com.studlabs.bll.services;

import com.studlabs.bll.exceptions.*;
import com.studlabs.bll.model.ForumThread;
import com.studlabs.dao.ThreadDaoImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ThreadServiceTest {
    @Mock
    private ThreadDaoImpl dao;

    @InjectMocks
    private ThreadServiceImpl service;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void save() throws ForumException {
        ForumThread ft = new ForumThread(1000, "IT", "public", "Software architectures", Arrays.asList("school"), LocalDateTime.now());
        when(dao.save(ft)).thenReturn(ft);
        assertEquals(ft, service.save(ft));
    }

    @Test
    public void findByIdNull() throws ForumException {
        when(dao.findById(1)).thenReturn(Optional.empty());
        assertEquals(Optional.empty(), service.findById(1));
    }

    @Test
    public void findById2() throws ForumException {
        ForumThread forumThread = new ForumThread(1, "cat", "public", "title", Arrays.asList(), LocalDateTime.now());
        when(dao.findById(1)).thenReturn(Optional.of(forumThread));
        assertEquals(forumThread, service.findById(1).get());
    }

    @Test
    public void findAll() throws ForumException {
        List<ForumThread> forumThreadList = new ArrayList<>();
        forumThreadList.add(new ForumThread(1, "cat", "public", "title", Arrays.asList(), LocalDateTime.now()));
        forumThreadList.add(new ForumThread(2, "cat2", "public", "title", Arrays.asList("book", "school"), LocalDateTime.now()));

        when(dao.findAllOpen()).thenReturn(forumThreadList);
        assertEquals(forumThreadList, service.findAllOpen());
    }

    @Test
    public void update() throws ForumException {
        ForumThread ft = new ForumThread(1000, "IT", "public", "Software architectures", Arrays.asList("Prog"), LocalDateTime.now());
        service.update(ft);
        verify(dao).update(ft);
    }

    @Test
    public void filterThreadsByMainCategory() throws ForumException {
        List<ForumThread> threads = new ArrayList<>();
        threads.add(new ForumThread(1000, "IT", "public", "Software architectures"));
        threads.add(new ForumThread(1001, "IT", "public", "Pogramming"));

        when(dao.filterThreads(Optional.of("IT"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty())).thenReturn(threads);
        assertEquals(threads, service.filterThreads(Optional.of("IT"), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty()));
    }

    @Test
    public void filterThreadsByTags() throws ForumException {
        List<ForumThread> threads = new ArrayList<>();
        threads.add(new ForumThread(1000, "IT", "public", "Software architectures"));
        threads.add(new ForumThread(1001, "IT", "public", "Pogramming"));

        List<String> tags = Arrays.asList("java", "CPU");

        when(dao.filterThreads(Optional.empty(), Optional.of(tags), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty())).thenReturn(threads);
        assertEquals(threads, service.filterThreads(Optional.empty(), Optional.of(tags), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty()));
    }

    @Test
    public void findByMainCategoryAndTags() throws ForumException {
        List<ForumThread> forumThreadList = new ArrayList<>();
        List<String> tags = Arrays.asList("java", "prog", "music");

        forumThreadList.add(new ForumThread(1, "cat", "public", "title"));

        when(dao.filterThreads(Optional.of("cat"), Optional.of(tags), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty())).thenReturn(forumThreadList);
        assertEquals(forumThreadList, service.filterThreads(Optional.of("cat"), Optional.of(tags), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty()));
    }

    @Test
    public void findSortedByCategoryAsc() throws ForumException {
        List<ForumThread> forumThreadList = new ArrayList<>();
        List<String> tags = Arrays.asList("java", "prog", "music");

        forumThreadList.add(new ForumThread(1, "cat", "public", "title"));

        when(dao.filterThreads(Optional.of("cat"), Optional.of(tags),
                Optional.of("category"), Optional.empty(), Optional.empty(), Optional.empty())).thenReturn(forumThreadList);
        assertEquals(forumThreadList, service.filterThreads(Optional.of("cat"), Optional.of(tags),
                Optional.of("category"), Optional.empty(), Optional.empty(), Optional.empty()));
    }

    @Test
    public void findSortedByCategoryDesc() throws ForumException {
        List<ForumThread> forumThreadList = new ArrayList<>();
        List<String> tags = Arrays.asList("java", "prog", "music");

        forumThreadList.add(new ForumThread(1, "cat", "public", "title"));

        when(dao.filterThreads(Optional.of("cat"), Optional.of(tags),
                Optional.of("category"), Optional.of("desc"), Optional.empty(), Optional.empty())).thenReturn(forumThreadList);
        assertEquals(forumThreadList, service.filterThreads(Optional.of("cat"), Optional.of(tags),
                Optional.of("category"), Optional.of("desc"), Optional.empty(), Optional.empty()));
    }

    @Test
    public void delete() throws ForumException {
        service.remove(1000);
        verify(dao).remove(1000);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateThreadWithNonexistentId() throws ForumException {
        ForumThread forumThread = new ForumThread(1, "", "", "", Collections.emptyList());
        doThrow(new NoThreadException()).when(dao).update(forumThread);
        service.update(forumThread);
    }

    @Test(expected = NotFoundException.class)
    public void testRemoveThreadWithNonexistentId() throws ForumException {
        doThrow(new NoThreadException()).when(dao).remove(1);
        service.remove(1);
    }

    @Test(expected = NotFoundException.class)
    public void testFindNonexistentId() throws ForumException {
        doThrow(new NoThreadException()).when(dao).findById(1);
        service.findById(1);
    }

    @Test(expected = BadRequestException.class)
    public void testFilterThreadsByInvalidSortOrder() throws ForumException {
        doThrow(new InvalidParamException()).when(dao).filterThreads(Optional.empty(),
                Optional.empty(),
                Optional.of("invalid"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());
        service.filterThreads(Optional.empty(),
                Optional.empty(),
                Optional.of("invalid"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());

    }

    @Test(expected = BadRequestException.class)
    public void testFilterThreadsByInvalidField() throws ForumException {
        doThrow(new InvalidParamException()).when(dao).filterThreads(Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of("invalid"),
                Optional.empty(),
                Optional.empty());
        service.filterThreads(Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of("invalid"),
                Optional.empty(),
                Optional.empty());

    }

    @Test(expected = BllException.class)
    public void findAllDaoException() throws ForumException {
        doThrow(new DaoException()).when(dao).findAllOpen();
        service.findAllOpen();
    }

    @Test(expected = BllException.class)
    public void saveDaoException() throws ForumException {
        ForumThread forumThread = new ForumThread(1000, "IT", "public", "Software architectures", Arrays.asList("school"), LocalDateTime.now());
        when(dao.save(forumThread)).thenThrow(new DaoException());
        service.save(forumThread);
    }

    @Test(expected = BllException.class)
    public void updateDaoException() throws ForumException {
        ForumThread forumThread = new ForumThread();
        doThrow(new DaoException()).when(dao).update(forumThread);
        service.update(forumThread);
    }

    @Test(expected = BllException.class)
    public void removeDaoException() throws ForumException {
        ForumThread forumThread = new ForumThread();
        doThrow(new DaoException()).when(dao).remove(1);
        service.remove(1);
    }

    @Test(expected = BllException.class)
    public void findByIdDaoException() throws ForumException {
        when(dao.findById(1)).thenThrow(new DaoException());
        service.findById(1);
    }

    @Test(expected = BllException.class)
    public void filterDaoException() throws ForumException {
        when(dao.filterThreads(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty())).thenThrow(new DaoException());
        service.filterThreads(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty());
    }
}
