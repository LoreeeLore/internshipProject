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
import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class ForumThreadTest {
    private Validator validator;
    private LocalDateTime date = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);


    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testCorrect() {
        ForumThread forumThread = new ForumThread(1, "IT", "public", "Title",
                Arrays.asList("TAG"), date);

        Set<ConstraintViolation<ForumThread>> violations = validator.validate(forumThread);
        assertEquals(0, violations.size());
    }

    @Test
    public void testEmptyCategory() {
        ForumThread forumThread = new ForumThread(1, "", "public", "Title",
                Arrays.asList("TAG"), date);

        Set<ConstraintViolation<ForumThread>> violations = validator.validate(forumThread);
        assertEquals(1, violations.size());
    }

    @Test
    public void testNullCategory() {
        ForumThread forumThread = new ForumThread(1, null, "public", "Title",
                Arrays.asList("TAG"), date);

        Set<ConstraintViolation<ForumThread>> violations = validator.validate(forumThread);
        assertEquals(2, violations.size());
    }

    @Test
    public void testInvalidAccess() {
        ForumThread forumThread = new ForumThread(1, null, "invalid", "Title",
                Arrays.asList("TAG"), date);

        Set<ConstraintViolation<ForumThread>> violations = validator.validate(forumThread);
        assertEquals(3, violations.size());
    }

    @Test
    public void testEmptyTitle() {
        ForumThread forumThread = new ForumThread(1, "Cat", "public", "",
                Arrays.asList("TAG"), date);

        Set<ConstraintViolation<ForumThread>> violations = validator.validate(forumThread);
        assertEquals(1, violations.size());
    }

    @Test
    public void testNullTitle() {
        ForumThread forumThread = new ForumThread(1, "Cat", "public", null,
                Arrays.asList("TAG"), date);

        Set<ConstraintViolation<ForumThread>> violations = validator.validate(forumThread);
        assertEquals(1, violations.size());
    }

    @Test
    public void testExceededTitleSize() {
        ForumThread forumThread = new ForumThread(1, "Cat", "public", StringUtils.repeat("*", 31),
                Arrays.asList("TAG"), date);

        Set<ConstraintViolation<ForumThread>> violations = validator.validate(forumThread);
        assertEquals(1, violations.size());
    }

    @Test
    public void testNullTagList() {
        ForumThread forumThread = new ForumThread(1, "IT", "public", "Title",
                null, date);

        Set<ConstraintViolation<ForumThread>> violations = validator.validate(forumThread);
        assertEquals(1, violations.size());
    }

    @Test
    public void testExceededNumberOfTags() {
        ForumThread forumThread = new ForumThread(1, "IT", "public", "Title",
                new ArrayList<>(Collections.nCopies(30, "*")), date);

        Set<ConstraintViolation<ForumThread>> violations = validator.validate(forumThread);
        assertEquals(1, violations.size());
    }

    @Test
    public void testTagWithExceededNumberOfCharacters() {
        ForumThread forumThread = new ForumThread(1, "IT", "public", "Title",
                Arrays.asList(StringUtils.repeat("*", 30)), date);

        Set<ConstraintViolation<ForumThread>> violations = validator.validate(forumThread);
        assertEquals(1, violations.size());
    }

    @Test
    public void testThreadWithoutIdAndDate() {
        ForumThread forumThread = new ForumThread("IT", "public", "Title",
                Arrays.asList());

        Set<ConstraintViolation<ForumThread>> violations = validator.validate(forumThread);
        assertEquals(0, violations.size());
    }
}