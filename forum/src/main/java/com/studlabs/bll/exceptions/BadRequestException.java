package com.studlabs.bll.exceptions;

public class BadRequestException extends ForumException {
    public BadRequestException() {
    }

    public BadRequestException(String message) {
        super(message);
    }


    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

}
