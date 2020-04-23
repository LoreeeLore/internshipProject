package com.studlabs.bll.model;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public abstract class Constants {
    public static final int MAX_IMAGES = 5;
    public static final int MAX_USERNAME_LENGTH = 100;
    public static final int MAX_MESSAGE_TEXT_LENGTH = 1000;
    public static final int MAX_IMAGE_SIZE = 16777210;

    //cannot pass array to annotation
    public static final String ORIGIN = "*";

    //this URL is only for mock(for now)
    public static final String TOKEN_VERIFY_ENDPOINT = ServletUriComponentsBuilder.
            fromCurrentContextPath().build().toString() + "/token";
//    public static final String TOKEN_VERIFY_ENDPOINT =
//            "https://10.13.62.200:4433/api/authentication/login/check";
}
