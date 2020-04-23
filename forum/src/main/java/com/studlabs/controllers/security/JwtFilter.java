package com.studlabs.controllers.security;

import com.studlabs.bll.exceptions.AccessDeniedException;
import io.jsonwebtoken.Claims;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.*;
import org.springframework.security.web.authentication.*;
import org.springframework.util.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

public class JwtFilter implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    private static final String HEADER_STRING = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer";
    private static final int STATUS_CODE_OK = 200;
    private String authenticationUrl=getAuthenticationServiceUrl();

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private JwtTokenProvider jwtTokenUtil;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {

       final String token = request.getHeader("Authorization");
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            logger.info("Missing or invalid Authorization header.");
//            throw new AccessDeniedException("Missing or invalid Authorization header.");
//        }
//
//        final String token = authHeader.substring(7); // The part after "Bearer "

        //very important
        //verify token before parsing it
        //send token to authorization server
        //********************************************

        if (token == null || !token.startsWith(TOKEN_PREFIX)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        if (!authenticate(request, token)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        return true;

        //********************************************

//        try {
//            logger.info("getting claims from token");
//            final Claims claims = tokenProvider.getClaimsFromToken(token);
//
//            //verify access to endpoint
//            if (handler instanceof HandlerMethod) {
//                // there are cases where this handler isn't an instance of HandlerMethod, so the cast fails.
//                HandlerMethod handlerMethod = (HandlerMethod) handler;
//                Method method = handlerMethod.getMethod();
//
//                logger.info("request goes to method {}", method.getName());
//
//                //annotation on method has higher priority than on class(overrides class annotation)
//                //so first, check annotation on method
//                Access accessAnnotation = method.getAnnotation(Access.class);
//
//                //if method is annotated
//                if (accessAnnotation != null) {
//                    logger.info("check annotation on method");
//                    checkAnnotation(accessAnnotation, request, claims);
//                } else {//check if controller is annotated
//                    logger.info("check annotation on controller");
//                    accessAnnotation = handlerMethod.getBean().getClass().getAnnotation(Access.class);
//                    if (accessAnnotation != null) {
//                        checkAnnotation(accessAnnotation, request, claims);
//                    }
//                }
//
//                //also, if no annotation is present then every user can access the endpoint
//                logger.info("setting claims");
//                request.setAttribute("claims", claims);
//            }
//        } catch (final SignatureException e) {
//            logger.warn("invalid token ", e);
//            throw new BadRequestException("Invalid token.");
//        }

        //return true;
    }

    private boolean authenticate(HttpServletRequest request, String token) {
        if (!StringUtils.isEmpty(authenticationUrl)) {
            if (authenticationLoginCheck(token).getStatusCodeValue() != STATUS_CODE_OK) {
                return false;
            }
        }

        token = token.replace(TOKEN_PREFIX, "");

        if (!jwtTokenUtil.isTokenValid(token)) {
            return false;
        }

        String uniqueName = jwtTokenUtil.getUniqueNameFromToken(token);
        if (uniqueName.isEmpty()) {
            return false;
        }

        String nameId = jwtTokenUtil.getNameIdFromToken(token);
        if (nameId.isEmpty()) {
            return false;
        }

        UsernamePasswordAuthenticationToken authentication = jwtTokenUtil.getAuthentication(token);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return true;
    }

    private ResponseEntity<String> authenticationLoginCheck(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_STRING, token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(authenticationUrl, HttpMethod.POST, entity, String.class);
    }
    private String getAuthenticationServiceUrl() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classloader.getResourceAsStream("authentication.properties");

        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (String) properties.get("URL");
    }


    private void checkAnnotation(Access accessAnnotation, HttpServletRequest request, Claims claims) throws AccessDeniedException {
        List<AccessRole> roles = Arrays.asList(accessAnnotation.role());

        logger.info("getting method roles: {}", roles);
        //every user has access if the access level is USER
        if (roles.size() > 0 && !roles.contains(AccessRole.USER)) {

            List<AccessRole> rolesFromClaim = tokenProvider.getRolesFromClaim(claims);
            logger.info("getting request roles: {}", rolesFromClaim);

            //if role is admin, then he has access
            if (rolesFromClaim.contains(AccessRole.ADMINISTRATOR)) {
                logger.info("checkAnnotation - admin");
                return;
            }

            //if there isn't a common role, then the user doesn't have access to the endpoint
            Collection<AccessRole> commonRoles = CollectionUtils.intersection(roles, rolesFromClaim);
            if (commonRoles.isEmpty()) {
                logger.warn("Access denied to endpoint {}", request.getRequestURL());
                throw new AccessDeniedException("Access denied to endpoint " +
                        request.getRequestURL());
            }
        }
    }
}
