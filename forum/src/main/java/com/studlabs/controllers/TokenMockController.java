package com.studlabs.controllers;

import com.studlabs.bll.exceptions.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/token")
public class TokenMockController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ThreadController.class);

    @Autowired
    private HttpServletRequest request;

    @PutMapping("")
    public ResponseEntity<?> verifyToken() throws BadRequestException {

        logger.info("verifyToken");

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadRequestException("Missing or invalid Authorization header.");
        }

        final String token = authHeader.substring(7); // The part after "Bearer "

        //only for testing purposes
        logger.info(token);

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
