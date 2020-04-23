package com.studlabs;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.web.WebAppConfiguration;

@Configuration
@WebAppConfiguration
@PropertySource("classpath:config.properties")
@ImportResource("classpath:spring-servlet-config.xml")
public class TestConfig {
    @Value("${elasticsearch.hostname}")
    private String hostName;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public RestHighLevelClient makeClient() {
        return new RestHighLevelClient(
                RestClient.builder(new HttpHost(hostName, 9200, "http")));
    }

}
