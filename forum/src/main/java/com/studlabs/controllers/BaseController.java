package com.studlabs.controllers;

import com.studlabs.bll.exceptions.AccessDeniedException;
import com.studlabs.bll.model.Constants;
import com.studlabs.controllers.errors.ErrorDetails;
import com.studlabs.controllers.security.AccessRole;
import com.studlabs.controllers.security.TokenProvider;
import io.jsonwebtoken.Claims;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@CrossOrigin(origins = {Constants.ORIGIN}, allowedHeaders = "*")
public class BaseController {
    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    protected TokenProvider tokenProvider;

    protected ResponseEntity<?> createErrorResponse(String msg, HttpStatus status,
                                                    HttpServletRequest request) {
        return new ResponseEntity<>(new ErrorDetails(new Date(),
                msg,
                request.getPathInfo()), status);
    }

    protected void checkUserFromToken(HttpServletRequest request,
                                      String user,
                                      List<AccessRole> accessRoles) throws AccessDeniedException {

        if (user == null) {
            logger.info("checkUserFromToken - user was null");
            return;
        }

        Object claim = request.getAttribute("claims");

        //if the filter is active, the claim is always there
        //but it is not there in unit tests
        if (claim != null) {
            String userInToken = tokenProvider.getUserNameFromClaim((Claims) claim);
            List<AccessRole> rolesFromClaim = tokenProvider.getRolesFromClaim((Claims) claim);

            if (rolesFromClaim.contains(AccessRole.ADMINISTRATOR)) {
                logger.info("checkUserFromToken - accessed by user:{}, admin", userInToken);
                return;
            }

            //endpoint can be accessed by a user with the correct username
            //OR somebody with another role
            if (accessRoles != null) {
                Collection<AccessRole> commonRoles = CollectionUtils.intersection(accessRoles, rolesFromClaim);
                if (!commonRoles.isEmpty()) {
                    logger.info("checkUserFromToken - accessed by user:{}, roles: {}", userInToken, rolesFromClaim);
                    return;
                }
            }

            if (!userInToken.equals(user)) {
                logger.info("bad user in path {}, from token {}", user, userInToken);
                throw new AccessDeniedException("Cannot access this endpoint");
            }
        }
    }
}
