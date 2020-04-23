package com.studlabs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static springfox.documentation.builders.PathSelectors.regex;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket productApi() {
        List<ResponseMessage> responseMessages = Arrays.asList(
                new ResponseMessageBuilder().code(200)
                        .message("OK")
                        .build(),
                new ResponseMessageBuilder().code(400)
                        .message("Bad Request - ex. invalid input")
                        .responseModel(new ModelRef("Error"))
                        .build(),
                new ResponseMessageBuilder().code(401)
                        .message("Unauthorized")
                        .responseModel(new ModelRef("Error"))
                        .build(),
                new ResponseMessageBuilder().code(404)
                        .message("Not Found")
                        .responseModel(new ModelRef("Error"))
                        .build(),
                new ResponseMessageBuilder().code(405)
                        .message("Method Not Allowed")
                        .responseModel(new ModelRef("Error"))
                        .build(),
                new ResponseMessageBuilder().code(500)
                        .message("Internal Server Error")
                        .responseModel(new ModelRef("Error"))
                        .build());

        List<SecurityScheme> schemeList = new ArrayList<>();
        schemeList.add(new ApiKey(HttpHeaders.AUTHORIZATION, "JWT", "header"));

        return new Docket(DocumentationType.SWAGGER_2)
                .securitySchemes(schemeList)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.studlabs.controllers"))
                .paths(regex("/.*"))
                .build()
                .apiInfo(apiInfo())
                .globalResponseMessage(RequestMethod.GET, responseMessages)
                .globalResponseMessage(RequestMethod.POST, responseMessages)
                .globalResponseMessage(RequestMethod.PUT, responseMessages)
                .globalResponseMessage(RequestMethod.DELETE, responseMessages);
    }

    private ApiInfo apiInfo() {

        return new ApiInfo(
                "Forum API",
                "Forum app API description",
                "1.0",
                "Terms of Service",
                new Contact("iQuest Group", "url of app",
                        "email"),
                "license",
                "license URL"
        );
    }
}
