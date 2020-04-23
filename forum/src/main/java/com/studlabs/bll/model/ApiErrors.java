package com.studlabs.bll.model;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

public class ApiErrors {

    private List<String> message = new ArrayList<>();

    public ApiErrors(BindingResult bindingResult) {
        List<FieldError> errors = bindingResult.getFieldErrors();
        for (FieldError error : errors) {
            message.add(error.getField() + " : " + error.getDefaultMessage());
        }
    }

    public List<String> getMessage() {
        return message;
    }

    public void setMessage(List<String> message) {
        this.message = message;
    }
}
