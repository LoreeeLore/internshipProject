package com.studlabs.controllers;

import com.studlabs.bll.services.MessageServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MessageElasticSearchControllerTest {
    @Mock
    private MessageServiceImpl service;

    @InjectMocks
    private MessageElasticSearchController controller;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void fullTextSearch() throws IOException {
        List<String> answer = new ArrayList<>();
        when(service.fullTextSearch("abc")).thenReturn(answer);
        assertEquals(controller.fullTextSearch("abc").getBody(), answer);
    }
}