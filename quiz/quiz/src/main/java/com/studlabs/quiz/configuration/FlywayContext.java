package com.studlabs.quiz.configuration;

import org.flywaydb.core.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.*;
import org.springframework.stereotype.*;

import javax.sql.*;
import java.io.*;

@Component
public class FlywayContext implements Serializable, SmartLifecycle {

    private final DataSource dataSource;

    @Autowired
    public FlywayContext(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public boolean isRunning() {
        System.out.println("flywayContext isRunning returned false");
        return false;
    }

    @Override
    public void start() {
        System.out.println("Starting flyaway context calling flyway-init");
        flywayInit();
    }

    @Override
    public void stop() {
        System.out.println("stoping flyway context");
    }

    private void flywayInit() {
        Flyway flyway = Flyway.configure().dataSource(dataSource).load();

        // Start the migration
        flyway.migrate();
    }
}
