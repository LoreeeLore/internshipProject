package com.studlabs.controllers;

import com.studlabs.bll.exceptions.BadRequestException;
import com.studlabs.bll.exceptions.BllException;
import com.studlabs.bll.exceptions.ForumException;
import com.studlabs.bll.exceptions.NotFoundException;
import com.studlabs.bll.model.ForumThread;
import com.studlabs.bll.model.Message;
import com.studlabs.bll.model.Rating;
import com.studlabs.bll.model.RatingType;
import com.studlabs.bll.services.MessageServiceImpl;
import com.studlabs.bll.services.RatingServiceImpl;
import com.studlabs.bll.services.ThreadService;
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
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MessageControllerTest {

    @Mock
    private MessageServiceImpl service;

    @Mock
    private RatingServiceImpl ratingService;

    @Mock
    private ThreadService threadService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private MessageController controller;

    private Validator validator;

    @Mock
    private BindingResult bindingResult;

    @Before
    public void initMocks() throws BllException {
        MockitoAnnotations.initMocks(this);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        //role-based auth
        when(service.findById(1)).thenReturn(Optional.empty());
    }


    @Test
    public void save() throws Exception {
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        Message message = new Message(1, "u", "salutare iQuest", aDateTime);

        when(service.save(message)).thenReturn(message);
        validateMessage(message);

        assertEquals(message, controller.save(message, bindingResult, 1).getBody());
        assertEquals(HttpStatus.OK, controller.save(message, bindingResult, 1).getStatusCode());
    }

    @Test
    public void findByIdNull() throws ForumException {
        when(service.findById(1)).thenReturn(Optional.empty());
        assertEquals(HttpStatus.NOT_FOUND, controller.getById(1).getStatusCode());
    }

    @Test
    public void findById2() throws ForumException {
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        Message message = new Message(1, 1, "u", "salutare iQuest", aDateTime, 0, 0);
        when(service.findById(1)).thenReturn(Optional.of(message));
        assertEquals(message, controller.getById(1).getBody());
        assertEquals(HttpStatus.OK, controller.getById(1).getStatusCode());
    }

    @Test
    public void findAll() throws ForumException {
        final int threadId = 1;
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        LocalDateTime anotherDateTime = LocalDateTime.of(2016, Month.JULY, 29, 19, 30, 40);

        List<Message> messageList = new ArrayList<>();
        messageList.add(new Message(1, 1, "u", "salutare iQuest", aDateTime, 0, 0));
        messageList.add(new Message(1, 1, "u", "salutare iQuest", anotherDateTime, 0, 0));

        when(service.findAllSorted(threadId, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty())).
                thenReturn(messageList);
        assertEquals(messageList, controller.getAll(threadId, Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty()).getBody());

        assertEquals(HttpStatus.OK, controller.getAll(threadId, Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty()).getStatusCode());
    }

    @Test
    public void remove() throws ForumException {
        controller.remove(1);
        verify(service).remove(1);
    }

    @Test
    public void update() throws Exception {
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        Message message = new Message(1000, 1, "u", "salutare iQuest", aDateTime, 0, 0);
        validateMessage(message);

        controller.update(message, bindingResult, 1000);
        verify(service).update(message);
    }

    @Test
    public void updateBadId() throws Exception {
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        Message message = new Message(1, 1, "u", "salutare iQuest", aDateTime, 0, 0);

        when(request.getPathInfo()).thenReturn("threads/1/messages/1");
        validateMessage(message);

        assertEquals(HttpStatus.BAD_REQUEST, controller.update(message, bindingResult, 100).getStatusCode());
    }

    @Test
    public void findAllSorted() throws ForumException {
        final int threadId = 1;
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        LocalDateTime anotherDateTime = LocalDateTime.of(2016, Month.JULY, 29, 19, 30, 40);

        List<Message> messageList = new ArrayList<>();
        messageList.add(new Message(1, 1, "u", "salutare iQuest", aDateTime, 0, 0));
        messageList.add(new Message(1, 1, "u", "salutare iQuest", anotherDateTime, 0, 0));

        when(service.findAllSorted(threadId, Optional.of("text"), Optional.of("asc"), Optional.empty(), Optional.empty())).thenReturn(messageList);
        assertEquals(messageList, controller.getAll(threadId, Optional.of("text"), Optional.of("asc"),
                Optional.empty(), Optional.empty()).getBody());
    }

    @Test
    public void findAllSortedDesc() throws ForumException {
        final int threadId = 1;
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        LocalDateTime anotherDateTime = LocalDateTime.of(2016, Month.JULY, 29, 19, 30, 40);

        List<Message> messageList = new ArrayList<>();
        messageList.add(new Message(1, 1, "u", "salutare iQuest", aDateTime, 0, 0));
        messageList.add(new Message(1, 1, "u", "salutare iQuest", anotherDateTime, 0, 0));

        when(service.findAllSorted(threadId, Optional.of("text"), Optional.of("desc"), Optional.empty(), Optional.empty())).thenReturn(messageList);
        assertEquals(messageList, controller.getAll(threadId, Optional.of("text"), Optional.of("desc"),
                Optional.empty(), Optional.empty()).getBody());
    }

    @Test
    public void findAllSortedDateDesc() throws ForumException {
        final int threadId = 1;
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        LocalDateTime anotherDateTime = LocalDateTime.of(2016, Month.JULY, 29, 19, 30, 40);

        List<Message> messageList = new ArrayList<>();
        messageList.add(new Message(1, 1, "u", "salutare iQuest", aDateTime, 0, 0));
        messageList.add(new Message(1, 1, "u", "salutare iQuest", anotherDateTime, 0, 0));

        when(service.findAllSorted(threadId, Optional.of("date"), Optional.of("desc"), Optional.empty(), Optional.empty())).thenReturn(messageList);
        assertEquals(messageList, controller.getAll(threadId, Optional.of("date"), Optional.of("desc"),
                Optional.empty(), Optional.empty()).getBody());
    }
    //pagination

    @Test
    public void findAllSortedPagination() throws ForumException {
        final int threadId = 1;
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        LocalDateTime anotherDateTime = LocalDateTime.of(2016, Month.JULY, 29, 19, 30, 40);

        List<Message> messageList = new ArrayList<>();
        messageList.add(new Message(1, 1, "u", "salutare iQuest", aDateTime, 0, 0));
        messageList.add(new Message(1, 1, "u", "salutare iQuest", anotherDateTime, 0, 0));

        when(service.findAllSorted(threadId, Optional.of("date"), Optional.of("desc"), Optional.of(1), Optional.of(2))).thenReturn(messageList);
        assertEquals(messageList, controller.getAll(threadId, Optional.of("date"), Optional.of("desc"),
                Optional.of(1), Optional.of(2)).getBody());
    }

    //for rating

    @Test
    public void removeRating() throws ForumException {
        controller.removeRating(1, "u");
        verify(ratingService).remove(1, "u");
    }

    @Test
    public void updateRating() throws Exception {

        Rating rating = new Rating(1, "u", RatingType.UPVOTE);
        validateRating(rating);

        controller.updateRating(rating, bindingResult, 1, "u");
        verify(ratingService).update(rating);
    }

    @Test
    public void saveRating() throws Exception {
        Rating rating = new Rating(1, "u", RatingType.DOWNVOTE);
        when(ratingService.save(rating)).thenReturn(rating);
        validateRating(rating);

        assertEquals(rating, controller.saveRating(rating, bindingResult, 1).getBody());
        assertEquals(HttpStatus.OK, controller.saveRating(rating, bindingResult, 1).getStatusCode());
    }

    @Test
    public void findById2Rating() throws ForumException {
        Rating rating = new Rating(1, "u", RatingType.UPVOTE);
        when(ratingService.findById(1, "u")).thenReturn(Optional.of(rating));
        assertEquals(rating, controller.getRatingById(1, "u").getBody());
        assertEquals(HttpStatus.OK, controller.getRatingById(1, "u").getStatusCode());
    }

    //alternate flow
    @Test
    public void saveBadThreadIdInMessage() throws Exception {
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        Message message = new Message(2, "u", "salutare iQuest", aDateTime);

        validateMessage(message);

        assertEquals(HttpStatus.BAD_REQUEST, controller.save(message, bindingResult, 1).getStatusCode());
    }

    @Test
    public void saveBadNULLThreadIdInMessage() throws Exception {
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        Message message = new Message(null, "u", "salutare iQuest", aDateTime);

        LocalDateTime localDateTime = LocalDateTime.of(2018, 4, 1, 1, 1);
        ForumThread ft = new ForumThread("IT", "public", "Software architectures",
                Arrays.asList("nothing"), localDateTime);
        ft.setState("validated");
        when(service.save(message)).thenReturn(message);
        validateMessage(message);

        assertEquals(HttpStatus.OK, controller.save(message, bindingResult, 1).getStatusCode());
    }

    @Test
    public void updateRatingNotValid() throws Exception {
        Rating rating = new Rating(1, "u", null);
        validateRating(rating);

        assertEquals(HttpStatus.BAD_REQUEST, controller.updateRating(rating,
                bindingResult, 1, "u").getStatusCode());
    }

    @Test
    public void saveRatingNotValid() throws Exception {
        Rating rating = new Rating(1, "u", null);
        validateRating(rating);

        assertEquals(HttpStatus.BAD_REQUEST, controller.saveRating(rating, bindingResult, 1).getStatusCode());
    }

    @Test
    public void updateMessageNotValidText() throws Exception {
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        Message message = new Message(1000, 1, "u",
                "", aDateTime, 0, 0);
        validateMessage(message);

        assertEquals(HttpStatus.BAD_REQUEST, controller.update(message, bindingResult, 1000).getStatusCode());
    }

    @Test
    public void updateMessageNotValid() throws Exception {
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        Message message = new Message(1000, 1, null,
                "dasdad", aDateTime, 0, 0);
        validateMessage(message);

        assertEquals(HttpStatus.BAD_REQUEST, controller.update(message, bindingResult, 1000).getStatusCode());
    }

    @Test
    public void updateMessageWithoutDate() throws Exception {
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        Message message = new Message(1001, 1, "u",
                "dasdad", null, 0, 0);
        validateMessage(message);

        assertEquals(HttpStatus.OK, controller.update(message, bindingResult, 1001).getStatusCode());
    }

    @Test
    public void saveRatingBadMessageId() throws Exception {
        Rating rating = new Rating(2, "u", RatingType.DOWNVOTE);
        validateRating(rating);

        assertEquals(HttpStatus.BAD_REQUEST, controller.saveRating(rating, bindingResult, 1).getStatusCode());
    }

    @Test
    public void saveRatingNullMessageId() throws Exception {
        Rating rating = new Rating(null, "u", RatingType.DOWNVOTE);
        when(ratingService.save(rating)).thenReturn(rating);
        validateRating(rating);

        assertEquals(HttpStatus.OK, controller.saveRating(rating, bindingResult, 1).getStatusCode());
    }

    //BadRequestException

    @Test
    public void saveException() throws Exception {
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        Message message = new Message(1, "u", "salutare iQuest", aDateTime);

        when(service.save(message)).thenThrow(new BadRequestException());
        validateMessage(message);

        assertEquals(HttpStatus.BAD_REQUEST, controller.save(message, bindingResult, 1).getStatusCode());
    }

    @Test
    public void findAllException() throws ForumException {
        when(service.findAllSorted(eq(1), any(), any(), any(), any()))
                .thenThrow(new BadRequestException());

        assertEquals(HttpStatus.BAD_REQUEST, controller.getAll(1, Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty()).getStatusCode());
    }

    @Test
    public void removeException() throws ForumException {
        doThrow(new NotFoundException()).when(service).remove(1);
        assertEquals(HttpStatus.NOT_FOUND, controller.remove(1).getStatusCode());
    }

    @Test
    public void updateException() throws Exception {
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        Message message = new Message(1000, 1, "u", "salutare iQuest", aDateTime, 0, 0);
        validateMessage(message);
        doThrow(new BadRequestException()).when(service).update(message);

        assertEquals(HttpStatus.BAD_REQUEST, controller.update(message, bindingResult, 1000).getStatusCode());
    }


    //rating

    @Test
    public void saveBadRequestException() throws Exception {
        Rating rating = new Rating(1, "u", RatingType.DOWNVOTE);
        when(ratingService.save(rating)).thenThrow(new BadRequestException());

        assertEquals(HttpStatus.BAD_REQUEST,
                controller.saveRating(rating, bindingResult, 1).getStatusCode());
    }

    @Test
    public void updateBadRequestException() throws Exception {
        Rating rating = new Rating(1, "u", RatingType.DOWNVOTE);
        doThrow(new NotFoundException()).when(ratingService).update(rating);
        assertEquals(HttpStatus.NOT_FOUND,
                controller.updateRating(rating, bindingResult, 1, "u").getStatusCode());
    }

    @Test
    public void updateBadRequestExceptionBadId() throws Exception {
        Rating rating = new Rating(1, "u", RatingType.DOWNVOTE);
//        doThrow(new NotFoundException()).when(ratingService).update(rating);
        assertEquals(HttpStatus.BAD_REQUEST,
                controller.updateRating(rating, bindingResult, 2, "u").getStatusCode());
    }


    @Test
    public void removeBadRequestException() throws Exception {
        Rating rating = new Rating(1, "u", RatingType.DOWNVOTE);
        doThrow(new NotFoundException()).when(ratingService).remove(rating.getMessageId(), rating.getUser());

        assertEquals(HttpStatus.NOT_FOUND,
                controller.removeRating(rating.getMessageId(), rating.getUser()).getStatusCode());
    }

    //BllException

    @Test
    public void saveBllException() throws Exception {
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        Message message = new Message(1, "u", "salutare iQuest", aDateTime);

        when(service.save(message)).thenThrow(new BllException());
        validateMessage(message);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.save(message, bindingResult, 1).getStatusCode());
    }

    @Test
    public void findAllBllException() throws ForumException {
        when(service.findAllSorted(eq(1), any(), any(), any(), any()))
                .thenThrow(new BllException());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.getAll(1, Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty()).getStatusCode());
    }

    @Test
    public void removeBllException() throws ForumException {
        doThrow(new BllException()).when(service).remove(1);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.remove(1).getStatusCode());
    }

    @Test
    public void updateBllException() throws Exception {
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        Message message = new Message(1000, 1, "u", "salutare iQuest", aDateTime, 0, 0);
        validateMessage(message);
        doThrow(new BllException()).when(service).update(message);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.update(message, bindingResult, 1000).getStatusCode());
    }

    @Test
    public void updateNotFoundException() throws Exception {
        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        Message message = new Message(1000, 1, "u", "salutare iQuest", aDateTime, 0, 0);
        validateMessage(message);
        doThrow(new NotFoundException()).when(service).update(message);

        assertEquals(HttpStatus.NOT_FOUND, controller.update(message, bindingResult, 1000).getStatusCode());
    }

    //rating

    @Test
    public void saveBadRequestBllException() throws Exception {
        Rating rating = new Rating(1, "u", RatingType.DOWNVOTE);
        when(ratingService.save(rating)).thenThrow(new BllException());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
                controller.saveRating(rating, bindingResult, 1).getStatusCode());
    }

    @Test
    public void updateBadRequestBllException() throws Exception {
        Rating rating = new Rating(1, "u", RatingType.DOWNVOTE);
        doThrow(new BllException()).when(ratingService).update(rating);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
                controller.updateRating(rating, bindingResult, 1, "u").getStatusCode());
    }

    @Test
    public void removeBadRequestBllException() throws Exception {
        Rating rating = new Rating(1, "u", RatingType.DOWNVOTE);
        doThrow(new BllException()).when(ratingService).remove(rating.getMessageId(), rating.getUser());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
                controller.removeRating(rating.getMessageId(), rating.getUser()).getStatusCode());
    }

    @Test
    public void getRatingByIdBadRequestBllException() throws Exception {
        Rating rating = new Rating(1, "u", RatingType.DOWNVOTE);
        doThrow(new BllException()).when(ratingService).findById(rating.getMessageId(), rating.getUser());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
                controller.getRatingById(rating.getMessageId(), rating.getUser()).getStatusCode());
    }

    //message

    @Test
    public void findByIdException() throws ForumException {
        when(service.findById(1)).thenThrow(new BllException());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.getById(1).getStatusCode());
    }

    private void validateMessage(Message message) {
        if (!validator.validate(message).isEmpty()) {
            when(bindingResult.hasErrors()).thenReturn(true);
        }
    }

    private void validateRating(Rating rating) {
        if (!validator.validate(rating).isEmpty()) {
            when(bindingResult.hasErrors()).thenReturn(true);
        }
    }
}