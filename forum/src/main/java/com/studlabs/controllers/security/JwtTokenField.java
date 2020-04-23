package com.studlabs.controllers.security;

public enum JwtTokenField {
    UNIQUE_NAME("unique_name"),
    NAME_ID("nameid"),
    ROLE("role");

    private String name;

    JwtTokenField(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
