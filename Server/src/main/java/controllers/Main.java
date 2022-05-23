package controllers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Collections;

@SpringBootApplication(scanBasePackages = {
        "filters",
        "controllers",
        "security"
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
