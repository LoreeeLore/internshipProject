package com.studlabs.bll.model;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ImageValidator implements ConstraintValidator<ValidImage, Image> {
    @Override
    public void initialize(ValidImage constraintAnnotation) {

    }

    @Override
    public boolean isValid(Image image, ConstraintValidatorContext constraintValidatorContext) {

        //when updating an image, image data can be empty, but id must appear there
        if (image.getId() == null && StringUtils.isEmpty(image.getImage())) {
            return false;
        }

        return true;
    }

}
