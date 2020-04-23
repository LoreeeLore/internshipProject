package com.studlabs.quiz.configuration;

import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.studlabs.quiz")
public class ApplicationConfiguration {
}
