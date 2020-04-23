package com.proiect.colectiv.controller;

import com.proiect.colectiv.model.User;
import com.proiect.colectiv.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api
@RestController
@RequestMapping("/users")
public class UserRestController  {

    private UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAuthority('USER')  or hasAuthority('ADMINISTRATOR') or hasAuthority('MENTOR')")
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ResponseEntity<User> update(@RequestBody User user) {
        userService.update(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMINISTRATOR') or hasAuthority('MENTOR')")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResponseEntity<List<User>> findAll() {
        List<User> result = userService.findAll();
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

}
