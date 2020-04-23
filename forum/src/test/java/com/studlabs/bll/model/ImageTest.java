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

public class ImageTest {

    private Validator validator;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testCorrectFull() {
        Image image = new Image(1, 1, "aasd");

        Set<ConstraintViolation<Image>> violations = validator.validate(image);
        assertEquals(0, violations.size());
    }

    @Test
    public void testCorrectPartialImageData() {
        Image image = new Image(1, 1, "");

        Set<ConstraintViolation<Image>> violations = validator.validate(image);
        assertEquals(0, violations.size());
    }

    @Test
    public void testCorrectPartialMessageId() {
        Image image = new Image(1, 1, "");

        Set<ConstraintViolation<Image>> violations = validator.validate(image);
        assertEquals(0, violations.size());
    }


    @Test
    public void test() {
        Image image = new Image(1, 1, "");

        Set<ConstraintViolation<Image>> violations = validator.validate(image);
        assertEquals(0, violations.size());
    }

    @Test
    public void testEmptyImageNullId() {
        Image image = new Image(null, 1, "");

        Set<ConstraintViolation<Image>> violations = validator.validate(image);
        assertTrue(violations.size() > 0);
    }

    @Test
    public void testNullImageNullId() {
        Image image = new Image(null, 1, null);

        Set<ConstraintViolation<Image>> violations = validator.validate(image);
        assertTrue(violations.size() > 0);
    }

    @Test
    public void testTooLargeImage() {
        Image image = new Image(null, 1,
                StringUtils.repeat("*", Constants.MAX_IMAGE_SIZE + 1));

        Set<ConstraintViolation<Image>> violations = validator.validate(image);
        assertTrue(violations.size() > 0);
    }
}