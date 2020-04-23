package com.studlabs.bll.exceptions;

public class ForumException extends Exception {
    public ForumException() {
    }

    public ForumException(String message) {
        super(message);
    }

    public ForumException(String message, Throwable cause) {
        super(message, cause);
    }

}
