package com.studlabs.quiz.configuration;

import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.datasource.*;
import org.springframework.transaction.*;

import javax.sql.*;
import java.io.*;
import java.util.*;

@Configuration
public class DatasourceConfiguration {

    @Bean
    public NamedParameterJdbcTemplate geNamedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    public PlatformTransactionManager txManager() throws IOException {
        return new DataSourceTransactionManager(getDataSource());
    }

    @Bean
    public DataSource getDataSource() throws IOException {
        Properties properties = getDatabaseProperties();

        String url = (String) properties.get("URL");
        String username = (String) properties.get("username");
        String password = (String) properties.get("password");
        String driver = (String) properties.get("driver");


        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(driver);
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);

        return ds;
    }

    private Properties getDatabaseProperties() throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classloader.getResourceAsStream("local_database.properties");

        Properties properties = new Properties();
        properties.load(Objects.requireNonNull(inputStream));

        return properties;
    }

}
