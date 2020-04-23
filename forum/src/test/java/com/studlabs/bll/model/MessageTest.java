package com.studlabs.bll.model;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MessageTest {
    private LocalDateTime localDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);

    private Validator validator;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testCorrectFull() {
        Message message = new Message(1, 1, "username",
                "salutare iQuest", localDateTime,
                0, 0, new ArrayList<>(), new ArrayList<>());

        Set<ConstraintViolation<Message>> violations = validator.validate(message);
        assertEquals(0, violations.size());
    }

    @Test
    public void testCorrectPartial() {
        Message message = new Message( null, "username",
                "salutare iQuest", null,
                0, 0, new ArrayList<>(), new ArrayList<>());

        Set<ConstraintViolation<Message>> violations = validator.validate(message);
        assertEquals(0, violations.size());
    }

    @Test
    public void testNullUser() {
        Message message = new Message(1, 1, null,
                "salutare iQuest", localDateTime,
                0, 0);

        Set<ConstraintViolation<Message>> violations = validator.validateProperty(message, "user");
        assertTrue(violations.size() > 0);
    }

    @Test
    public void testUserTooLong() {
        Message message = new Message(1, 1,
                StringUtils.repeat("u", Constants.MAX_USERNAME_LENGTH + 1),
                "salutare iQuest", localDateTime,
                0, 0);

        Set<ConstraintViolation<Message>> violations = validator.validateProperty(message, "user");
        assertTrue(violations.size() > 0);
    }

    @Test
    public void testEmptyText() {
        Message message = new Message(1, 1, "u",
                "", localDateTime,
                0, 0);

        Set<ConstraintViolation<Message>> violations = validator.validateProperty(message, "text");
        assertTrue(violations.size() > 0);
    }

    @Test
    public void testBlankText() {
        Message message = new Message(1, 1, "u",
                "                ", localDateTime,
                0, 0);

        Set<ConstraintViolation<Message>> violations = validator.validateProperty(message, "text");
        assertTrue(violations.size() > 0);
    }

    @Test
    public void testLargeText() {
        final int length = Constants.MAX_MESSAGE_TEXT_LENGTH + 1;

        Message message = new Message(1, 1, "u",
                StringUtils.repeat("*", length), localDateTime,
                0, 0);

        Set<ConstraintViolation<Message>> violations = validator.validateProperty(message, "text");
        assertTrue(violations.size() > 0);
    }

    @Test
    public void testTaggedUserNamesNull() {
        Message message = new Message(1, 1, "u",
                "aaa", localDateTime,
                0, 0, null, new ArrayList<>());

        Set<ConstraintViolation<Message>> violations = validator.validateProperty(message, "taggedUserNames");
        assertTrue(violations.size() > 0);
    }

    @Test
    public void testTaggedUserNameTooLong() {
        final int length = Constants.MAX_USERNAME_LENGTH + 1;

        Message message = new Message(1, 1, "u",
                "aaa", localDateTime,
                0, 0, Arrays.asList("u", StringUtils.repeat("a", length)),
                new ArrayList<>());

        Set<ConstraintViolation<Message>> violations = validator.validateProperty(message, "taggedUserNames");
        assertTrue(violations.size() > 0);
    }

    @Test
    public void testImagesNull() {
        Message message = new Message(1, 1, "u",
                "aaa", localDateTime,
                0, 0, new ArrayList<>(), null);

        Set<ConstraintViolation<Message>> violations = validator.validateProperty(message, "images");
        assertTrue(violations.size() > 0);
    }

}