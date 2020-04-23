package com.studlabs.controllers;

import com.studlabs.bll.model.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CorsFilter implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(CorsFilter.class);

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        logger.info("Setting origin response headers");
        response.addHeader("Access-Control-Allow-Origin", Constants.ORIGIN);
        response.setHeader("Access-Control-Allow-Methods", "*");
        return true;
    }
}
