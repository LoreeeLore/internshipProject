package com.studlabs.bll.services;

import com.studlabs.bll.exceptions.BllException;
import com.studlabs.bll.exceptions.DaoException;
import com.studlabs.dao.TagDaoImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TagServiceTest {
    @Mock
    private TagDaoImpl dao;

    @InjectMocks
    private TagServiceImpl service;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAll() throws DaoException, BllException {
        service.getAll();
        verify(dao).findAll();
    }


    @Test
    public void saveList() throws BllException, DaoException {
        Map<String, List<String>> tags = new HashMap<>();
        tags.put("tags", new ArrayList<>());

        service.saveTagList(tags.get("tags"));
        verify(dao).saveTagList(tags.get("tags"));
    }

    @Test
    public void deleteList() throws BllException, DaoException {
        Map<String, List<String>> tags = new HashMap<>();
        tags.put("tags", new ArrayList<>());

        service.deleteTagList(tags.get("tags"));
        verify(dao).deleteTagList(tags.get("tags"));
    }

    @Test(expected = BllException.class)
    public void getDaoException() throws DaoException, BllException {
        when(dao.findAll()).thenThrow(DaoException.class);
        service.getAll();
    }

    @Test(expected = BllException.class)
    public void saveTest() throws DaoException, BllException {
        doThrow(new DaoException()).when(dao).saveTagList(new ArrayList<>());
        service.saveTagList(new ArrayList<>());
    }

    @Test(expected = BllException.class)
    public void deleteTest() throws DaoException, BllException {
        doThrow(new DaoException()).when(dao).deleteTagList(new ArrayList<>());
        service.deleteTagList(new ArrayList<>());
    }
}
