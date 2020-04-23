package com.proiect.colectiv.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
    public String username;
    public String password;
    public String firstName;
    public String lastName;
    @Email
    public String email;
}
