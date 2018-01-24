package com.zenika.snapshotmatcherexample.configuration;

import static java.util.Collections.singletonList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {

    @Autowired
    private UserAgentRequestInterceptor requestInterceptor;

    @Bean
    public RestTemplate restTemplate(){
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.setInterceptors(singletonList(requestInterceptor));

        return restTemplate;
    }
}
