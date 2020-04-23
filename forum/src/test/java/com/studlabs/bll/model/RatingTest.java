package com.studlabs.bll.model;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RatingTest {
    private Validator validator;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testCorrectFull() {
        Rating rating = new Rating(1, "u", RatingType.DOWNVOTE);

        Set<ConstraintViolation<Rating>> violations = validator.validate(rating);
        assertEquals(0, violations.size());
    }

    @Test
    public void testCorrectPartial() {
        Rating rating = new Rating(null, "u", RatingType.DOWNVOTE);

        Set<ConstraintViolation<Rating>> violations = validator.validate(rating);
        assertEquals(0, violations.size());
    }

    @Test
    public void testUserNull() {
        Rating rating = new Rating(1, null, RatingType.DOWNVOTE);

        Set<ConstraintViolation<Rating>> violations = validator.validateProperty(rating, "user");
        assertTrue(violations.size() > 0);
    }

    @Test
    public void testUserTooLarge() {
        Rating rating = new Rating(1,
                StringUtils.repeat("u", Constants.MAX_USERNAME_LENGTH + 1), RatingType.DOWNVOTE);

        Set<ConstraintViolation<Rating>> violations = validator.validateProperty(rating, "user");
        assertTrue(violations.size() > 0);
    }

    @Test
    public void testTypeNull() {
        Rating rating = new Rating(1, "u", null);

        Set<ConstraintViolation<Rating>> violations = validator.validateProperty(rating, "type");
        assertTrue(violations.size() > 0);
    }
}