package com.studlabs.bll.exceptions;

public class InvalidParamException extends ForumException {
    public InvalidParamException() {
    }

    public InvalidParamException(String message) {
        super(message);
    }

    public InvalidParamException(String message, Throwable cause) {
        super(message, cause);
    }

}
