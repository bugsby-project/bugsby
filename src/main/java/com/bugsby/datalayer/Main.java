package com.bugsby.datalayer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Collections;

@SpringBootApplication(scanBasePackages = {
        "com.bugsby.datalayer.filters",
        "com.bugsby.datalayer.controllers",
        "com.bugsby.datalayer.security"
})
public class Main {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("classpath:spring.xml");
        String port = System.getenv("PORT") != null ? System.getenv("PORT") : "8080";
        SpringApplication app = new SpringApplication(Main.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", port));
        app.run(args);
    }
}
