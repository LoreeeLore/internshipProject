package com.studlabs.controllers.errors;

import lombok.Getter;

import java.util.Date;

@Getter
//@AllArgsConstructor
public class ErrorDetails {
    private Date timestamp;
    private String message;
    private String details;

    public ErrorDetails(Date date, String msg, String pathInfo) {
        this.timestamp=date;
        this.message=msg;
        this.details=pathInfo;
    }
}