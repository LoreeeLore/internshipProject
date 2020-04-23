package com.proiect.colectiv.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthToken {

    private String accessToken;
    private String tokenType = "Bearer";
    private String username;
    private Long id;

    public AuthToken(String accessToken, String username, Long id) {
        this.accessToken = accessToken;
        this.id = id;
        this.username = username;
    }

    @Override
    public String toString() {
        return tokenType + " " + accessToken;
    }
}
