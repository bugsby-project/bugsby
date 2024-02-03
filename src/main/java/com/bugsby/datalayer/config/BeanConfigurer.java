package com.bugsby.datalayer.config;

import com.bugsby.datalayer.swagger.ai.api.DefaultApi;
import com.bugsby.datalayer.swagger.github.api.ActionsApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfigurer {
    @Value("${ai.url}")
    private String aiBaseUrl;
    @Value("${github.api.url}")
    private String githubApiBaseUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @Primary
    public com.bugsby.datalayer.swagger.ai.api.DefaultApi defaultApi() {
        com.bugsby.datalayer.swagger.ai.api.DefaultApi defaultApi = new DefaultApi();
        defaultApi.getApiClient().setBasePath(aiBaseUrl);
        return defaultApi;
    }

    @Bean
    @Primary
    public com.bugsby.datalayer.swagger.github.api.ActionsApi actionsApi() {
        com.bugsby.datalayer.swagger.github.api.ActionsApi actionsApi = new ActionsApi();
        actionsApi.getApiClient().setBasePath(githubApiBaseUrl);
        return actionsApi;
    }
}
