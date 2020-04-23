package com.studlabs.controllers;


import com.studlabs.bll.exceptions.BllException;
import com.studlabs.bll.services.TagService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TagControllerTest {

    @Mock
    private TagService service;

    @InjectMocks
    private TagController controller;

    @Mock
    private HttpServletRequest request;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAll() throws BllException {
        when(service.getAll()).thenReturn(new ArrayList<>());
        assertEquals(controller.getAll().getStatusCode(), HttpStatus.OK);
    }


    @Test
    public void saveList() throws BllException {
        Map<String, List<String>> tags = new HashMap<>();
        tags.put("tags", new ArrayList<>());

        controller.save(tags);
        verify(service).saveTagList(tags.get("tags"));
    }

    @Test
    public void deleteList() throws BllException {
        Map<String, List<String>> tags = new HashMap<>();
        tags.put("tags", new ArrayList<>());

        controller.delete(tags);
        verify(service).deleteTagList(tags.get("tags"));
    }

    @Test
    public void saveInternalServerError() throws BllException {
        Map<String, List<String>> tags = new HashMap<>();
        tags.put("tags", new ArrayList<>());
        doThrow(new BllException()).when(service).saveTagList(tags.get("tags"));

        assertEquals(controller.save(tags).getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void deleteInternalServerError() throws BllException {
        Map<String, List<String>> tags = new HashMap<>();
        tags.put("tags", new ArrayList<>());

        doThrow(new BllException()).when(service).deleteTagList(tags.get("tags"));

        assertEquals(controller.delete(tags).getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void getAllInternalServerError() throws BllException {
        when(service.getAll()).thenThrow(new BllException());

        assertEquals(controller.getAll().getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void saveBadRequest() {
        Map<String, List<String>> tags = new HashMap<>();
        tags.put("tag", new ArrayList<>());

        assertEquals(controller.save(tags).getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void deleteBadRequest() {
        Map<String, List<String>> tags = new HashMap<>();
        tags.put("tag", new ArrayList<>());

        assertEquals(controller.delete(tags).getStatusCode(), HttpStatus.BAD_REQUEST);
    }
}
