package com.studlabs.controllers;

import com.studlabs.controllers.security.AccessRole;
import com.studlabs.controllers.security.TokenProvider;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/login")
public class AuthenticationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private TokenProvider tokenProvider;

    @GetMapping(value = "/role")
    public List<AccessRole> getRole(final HttpServletRequest request) {
        logger.info("getRole");
        return tokenProvider.getRolesFromClaim((Claims) request.getAttribute("claims"));
    }

    @GetMapping(value = "/username")
    public String getUserName(final HttpServletRequest request) {
        logger.info("getUserName");
        return tokenProvider.getUserNameFromClaim((Claims) request.getAttribute("claims"));
    }

    @GetMapping(value = "/name")
    public String getName(final HttpServletRequest request) {
        logger.info("getName");
        return tokenProvider.getFullNameFromClaim((Claims) request.getAttribute("claims"));
    }
}
