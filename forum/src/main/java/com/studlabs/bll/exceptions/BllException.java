package com.studlabs.bll.exceptions;


public class BllException extends ForumException {
    public BllException() {
    }

    public BllException(String message) {
        super(message);
    }

    public BllException(String message, Throwable cause) {
        super(message, cause);
    }

}
