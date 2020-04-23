package com.studlabs.bll.exceptions;

public class NoThreadException extends NotFoundException {
    public NoThreadException() {
    }

    public NoThreadException(String message) {
        super(message);
    }

    public NoThreadException(String message, Throwable cause) {
        super(message, cause);
    }

}
