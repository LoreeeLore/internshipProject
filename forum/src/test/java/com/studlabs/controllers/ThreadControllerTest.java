package com.studlabs.controllers;

import com.studlabs.bll.exceptions.BadRequestException;
import com.studlabs.bll.exceptions.BllException;
import com.studlabs.bll.exceptions.ForumException;
import com.studlabs.bll.exceptions.NotFoundException;
import com.studlabs.bll.model.ForumThread;
import com.studlabs.bll.services.ThreadServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ThreadControllerTest {

    @Mock
    private ThreadServiceImpl service;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ThreadController controller;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void save() throws ForumException {
        ForumThread ft = new ForumThread(1000, "IT", "public", "Software architectures", Arrays.asList());
        when(service.save(ft)).thenReturn(ft);
        when(bindingResult.hasErrors()).thenReturn(false);
        assertEquals(ft, controller.save(ft, bindingResult).getBody());
    }

    @Test
    public void findByIdNull() throws NotFoundException, BllException {
        when(service.findById(1)).thenThrow(new NotFoundException());
        assertEquals(HttpStatus.NOT_FOUND, controller.getById(1).getStatusCode());
    }

    @Test
    public void findById2() throws NotFoundException, BllException {
        ForumThread forumThread = new ForumThread(1, "cat", "public", "title");
        when(service.findById(1)).thenReturn(Optional.of(forumThread));
        assertEquals(forumThread, controller.getById(1).getBody());
    }

    @Test
    public void findAll() throws BadRequestException, BllException {
        List<ForumThread> forumThreadList = new ArrayList<>();
        forumThreadList.add(new ForumThread(1, "cat", "public", "title"));
        forumThreadList.add(new ForumThread(2, "cat2", "public", "title"));

        when(service.filterThreads(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty())).thenReturn(forumThreadList);
        assertEquals(forumThreadList, controller.getAll(Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()).getBody());
    }

    @Test

    public void delete() throws NotFoundException, BllException, BadRequestException {
        controller.remove(1000);
        verify(service).remove(1000);
    }

    @Test
    public void findByCategory() throws BadRequestException, BllException {
        List<ForumThread> forumThreadList = new ArrayList<>();

        forumThreadList.add(new ForumThread(1, "cat", "public", "title"));
        forumThreadList.add(new ForumThread(2, "cat", "public", "title"));

        when(service.filterThreads(Optional.of("cat"), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty())).thenReturn(forumThreadList);
        assertEquals(forumThreadList, controller.getAll(Optional.of("cat"), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()).getBody());
    }

    @Test
    public void findByTags() throws BadRequestException, BllException {
        List<ForumThread> forumThreadList = new ArrayList<>();
        List<String> tags = Arrays.asList("java", "prog", "music");

        forumThreadList.add(new ForumThread(1, "cat", "public", "title"));

        when(service.filterThreads(Optional.empty(), Optional.of(tags), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty())).thenReturn(forumThreadList);
        assertEquals(forumThreadList, controller.getAll(Optional.empty(), Optional.of(tags),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()).getBody());
    }

    @Test
    public void findByMainCategoryAndTags() throws ForumException {
        List<ForumThread> forumThreadList = new ArrayList<>();
        List<String> tags = Arrays.asList("java", "prog", "music");

        forumThreadList.add(new ForumThread(1, "cat", "public", "title"));

        when(service.filterThreads(Optional.of("cat"), Optional.of(tags), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty())).thenReturn(forumThreadList);
        assertEquals(forumThreadList, controller.getAll(Optional.of("cat"), Optional.of(tags),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()).getBody());
    }


    @Test
    public void update() throws NotFoundException, BllException, BadRequestException {
        ForumThread ft = new ForumThread(1000, "IT", "public", "Software architectures", Arrays.asList());
        when(bindingResult.hasErrors()).thenReturn(false);
        controller.update(ft, bindingResult, 1000);
        verify(service).update(ft);
    }

    @Test
    public void updateBadId() throws Exception {
        ForumThread ft = new ForumThread(1000, "IT", "public", "Software architectures", Arrays.asList());
        when(bindingResult.hasErrors()).thenReturn(false);
        assertEquals(HttpStatus.BAD_REQUEST, controller.update(ft, bindingResult, 1).getStatusCode());
    }

    @Test
    public void findSortedByCategoryAsc() throws BadRequestException, BllException {
        List<ForumThread> forumThreadList = new ArrayList<>();
        List<String> tags = Arrays.asList("java", "prog", "music");

        forumThreadList.add(new ForumThread(1, "cat", "public", "title"));

        when(service.filterThreads(Optional.of("cat"), Optional.of(tags),
                Optional.of("category"), Optional.empty(), Optional.empty(), Optional.empty())).thenReturn(forumThreadList);
        assertEquals(forumThreadList, controller.getAll(Optional.of("cat"), Optional.of(tags),
                Optional.of("category"), Optional.empty(), Optional.empty(), Optional.empty()).getBody());
    }

    @Test
    public void findSortedByCategoryDesc() throws BadRequestException, BllException {
        List<ForumThread> forumThreadList = new ArrayList<>();
        List<String> tags = Arrays.asList("java", "prog", "music");

        forumThreadList.add(new ForumThread(1, "cat", "public", "title"));

        when(service.filterThreads(Optional.of("cat"), Optional.of(tags),
                Optional.of("category"), Optional.of("desc"), Optional.empty(), Optional.empty())).thenReturn(forumThreadList);
        assertEquals(forumThreadList, controller.getAll(Optional.of("cat"), Optional.of(tags),
                Optional.of("category"), Optional.of("desc"), Optional.empty(), Optional.empty()).getBody());
    }

    @Test
    public void saveInvalidThread() throws ForumException {
        ForumThread ft = new ForumThread(1000, "IT", "public", "Software architectures");
        when(bindingResult.hasErrors()).thenReturn(true);
        assertEquals(HttpStatus.BAD_REQUEST, controller.save(ft, bindingResult).getStatusCode());
    }

    @Test
    public void testInvalidSortingField() throws BadRequestException, BllException {
        when(service.filterThreads(Optional.empty(), Optional.empty(), Optional.of("invalid"), Optional.empty(),
                Optional.empty(), Optional.empty()))
                .thenThrow(BadRequestException.class);
        assertEquals(HttpStatus.BAD_REQUEST, controller.getAll(Optional.empty(), Optional.empty(), Optional.of("invalid"),
                Optional.empty(), Optional.empty(), Optional.empty()).getStatusCode());
    }

    @Test
    public void testInvalidOrder() throws BadRequestException, BllException {
        when(service.filterThreads(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("invalid"),
                Optional.empty(), Optional.empty()))
                .thenThrow(BadRequestException.class);
        assertEquals(HttpStatus.BAD_REQUEST, controller.getAll(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of("invalid"), Optional.empty(), Optional.empty()).getStatusCode());
    }

    @Test
    public void testUpdateThreadWithInvalidId() throws NotFoundException, BllException, BadRequestException {
        ForumThread forumThread = new ForumThread(1, "", "", "", Collections.emptyList());
        doThrow(new NotFoundException()).when(service).update(forumThread);
        assertEquals(HttpStatus.NOT_FOUND, controller.update(forumThread, bindingResult, 1).getStatusCode());
    }

    @Test
    public void testRemoveBadId() throws NotFoundException, BllException, BadRequestException {
        doThrow(new NotFoundException()).when(service).remove(1);
        assertEquals(HttpStatus.NOT_FOUND, controller.remove(1).getStatusCode());
    }

    @Test
    public void saveBllException() throws ForumException {
        ForumThread forumThread = new ForumThread();
        when(bindingResult.hasErrors()).thenReturn(false);

        when(service.save(forumThread)).thenThrow(new BllException());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.save(forumThread, bindingResult).getStatusCode());
    }

    @Test
    public void updateBllException() throws NotFoundException, BllException, BadRequestException {
        ForumThread forumThread = new ForumThread(1, "", "", "");
        when(bindingResult.hasErrors()).thenReturn(false);

        doThrow(new BllException()).when(service).update(forumThread);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.update(forumThread, bindingResult, 1).getStatusCode());
    }

    @Test
    public void removeNoFoundException() throws NotFoundException, BllException, BadRequestException {
        doThrow(new NotFoundException()).when(service).remove(1);
        assertEquals(HttpStatus.NOT_FOUND, controller.remove(1).getStatusCode());
    }

    @Test
    public void filterBadRequestException() throws BadRequestException, BllException {
        when(service.filterThreads(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty())).thenThrow(new BadRequestException());
        assertEquals(HttpStatus.BAD_REQUEST, controller.getAll(Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()).getStatusCode());
    }

    @Test
    public void filterBllException() throws ForumException {
        when(service.filterThreads(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty())).thenThrow(new BllException());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.getAll(Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()).getStatusCode());
    }

    @Test
    public void findByIdNotFoundException() throws NotFoundException, BllException {
        when(service.findById(1)).thenThrow(new NotFoundException());
        assertEquals(HttpStatus.NOT_FOUND, controller.getById(1).getStatusCode());
    }

    @Test
    public void findByIdBllException() throws NotFoundException, BllException {
        when(service.findById(1)).thenThrow(new BllException());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.getById(1).getStatusCode());
    }

    @Test
    public void removeBllException() throws NotFoundException, BllException, BadRequestException {
        doThrow(new BllException()).when(service).remove(1);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.remove(1).getStatusCode());
    }

}