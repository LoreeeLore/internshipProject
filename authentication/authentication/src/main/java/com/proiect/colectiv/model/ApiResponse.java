package com.proiect.colectiv.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse {

    private Long id;
    private String name;

    public ApiResponse(String name, Long id) {
        this.id = id;
        this.name = name;
    }

}
