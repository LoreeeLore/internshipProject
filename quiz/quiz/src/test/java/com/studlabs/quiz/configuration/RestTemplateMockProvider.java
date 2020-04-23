package com.studlabs.quiz.configuration;

import org.mockito.*;
import org.springframework.context.annotation.*;
import org.springframework.web.client.*;

@Configuration
public class RestTemplateMockProvider {

    @Bean
    public RestTemplate restTemplate() {
        return Mockito.mock(RestTemplate.class);
    }
}
