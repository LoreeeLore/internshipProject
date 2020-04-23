package com.studlabs.bll.model;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImageValidator.class)
@Documented
public @interface ValidImage {
    String message() default "Both image data and id cannot be null";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

