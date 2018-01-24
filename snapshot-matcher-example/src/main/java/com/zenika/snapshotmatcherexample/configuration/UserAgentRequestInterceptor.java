package com.zenika.snapshotmatcherexample.configuration;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

/**
 * Request interceptor which add a User-Agent header to each request.
 */
@Component
public class UserAgentRequestInterceptor implements ClientHttpRequestInterceptor {

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        httpRequest.getHeaders().set(HttpHeaders.USER_AGENT, applicationName);

        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }
}
