package com.bugsby.datalayer.config;

import com.bugsby.datalayer.swagger.ai.api.DefaultApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfigurer {
    @Value("${ai.url}")
    private String aiBaseUrl;

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
}
