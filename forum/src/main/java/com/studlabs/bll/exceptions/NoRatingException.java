package com.studlabs.bll.exceptions;

public class NoRatingException extends ForumException {
    public NoRatingException() {
    }

    public NoRatingException(String message) {
        super(message);
    }

}
