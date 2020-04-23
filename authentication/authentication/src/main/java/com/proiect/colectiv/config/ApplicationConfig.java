package com.proiect.colectiv.config;

import com.proiect.colectiv.service.MailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableScheduling
public class ApplicationConfig {

    @Bean(name = "asyncExecutor")
    public TaskExecutor getAsyncTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setCorePoolSize(5);
        taskExecutor.setThreadNamePrefix("mailSenderExecutor-");
        return taskExecutor;
    }

    @Bean
    public MailService getMailServiceBean() {
        return new MailService();
    }
}
