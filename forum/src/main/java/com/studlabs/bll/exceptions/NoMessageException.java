package com.studlabs.bll.exceptions;

public class NoMessageException extends NotFoundException {
    public NoMessageException() {
    }

    public NoMessageException(String message) {
        super(message);
    }

    public NoMessageException(String message, Throwable cause) {
        super(message, cause);
    }

}
