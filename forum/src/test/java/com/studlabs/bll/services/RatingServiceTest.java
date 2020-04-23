package com.studlabs.bll.services;

import com.studlabs.bll.exceptions.*;
import com.studlabs.bll.model.Rating;
import com.studlabs.bll.model.RatingType;
import com.studlabs.dao.RatingDaoImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RatingServiceTest {
    @Mock
    private RatingDaoImpl dao;

    @InjectMocks
    private RatingServiceImpl service;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void save() throws Exception {
        Rating rating = new Rating(1, "u", RatingType.DOWNVOTE);
        when(dao.save(rating)).thenReturn(rating);

        assertEquals(rating, service.save(rating));
    }

    @Test
    public void update() throws Exception {

        Rating rating = new Rating(1, "u", RatingType.DOWNVOTE);

        service.update(rating);
        verify(dao).update(rating);
    }

    @Test
    public void remove() throws Exception {
        Rating rating = new Rating(1, "u", RatingType.DOWNVOTE);
        service.remove(rating.getMessageId(), rating.getUser());
        verify(dao).remove(rating.getMessageId(), rating.getUser());
    }

    @Test
    public void findByIdNull() throws Exception {
        when(dao.findById(1, "u")).thenReturn(Optional.empty());
        assertEquals(Optional.empty(), service.findById(1, "u"));
    }

    @Test
    public void findById() throws Exception {
        Rating rating = new Rating(1, "u", RatingType.DOWNVOTE);
        when(dao.findById(1, "u")).thenReturn(Optional.of(rating));
        assertEquals(rating, service.findById(1, "u").get());
    }

    //BadRequestException

    @Test(expected = BadRequestException.class)
    public void saveBadRequestException() throws Exception {
        Rating rating = new Rating(1, "u", RatingType.DOWNVOTE);
        when(dao.save(rating)).thenThrow(new InvalidParamException());

        assertEquals(rating, service.save(rating));
    }

    @Test(expected = NotFoundException.class)
    public void updateBadRequestException() throws Exception {
        Rating rating = new Rating(1, "u", RatingType.DOWNVOTE);
        doThrow(new NoRatingException()).when(dao).update(rating);
        service.update(rating);
    }


    @Test(expected = NotFoundException.class)
    public void removeBadRequestException() throws Exception {
        Rating rating = new Rating(1, "u", RatingType.DOWNVOTE);
        doThrow(new NoRatingException()).when(dao).remove(rating.getMessageId(), rating.getUser());

        service.remove(rating.getMessageId(), rating.getUser());
    }

    //BllException

    @Test(expected = BllException.class)
    public void saveBllRequestException() throws Exception {
        Rating rating = new Rating(1, "u", RatingType.DOWNVOTE);
        when(dao.save(rating)).thenThrow(new DaoException());

        assertEquals(rating, service.save(rating));
    }

    @Test(expected = BllException.class)
    public void updateBllRequestException() throws Exception {
        Rating rating = new Rating(1, "u", RatingType.DOWNVOTE);
        doThrow(new DaoException()).when(dao).update(rating);
        service.update(rating);
    }


    @Test(expected = BllException.class)
    public void removeBllRequestException() throws Exception {
        Rating rating = new Rating(1, "u", RatingType.DOWNVOTE);
        doThrow(new DaoException()).when(dao).remove(rating.getMessageId(), rating.getUser());

        service.remove(rating.getMessageId(), rating.getUser());
    }

    @Test(expected = BllException.class)
    public void findByIdBllRequestException() throws Exception {
        Rating rating = new Rating(1, "u", RatingType.DOWNVOTE);
        when(dao.findById(1, "u")).thenThrow(new DaoException());
        service.findById(1, "u");
    }
}
