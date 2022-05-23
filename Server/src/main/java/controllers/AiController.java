package controllers;

import exceptions.AiServiceException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import service.Service;

@RestController
@CrossOrigin
public class AiController {
    private final Service service;

    {
        service = new ClassPathXmlApplicationContext("classpath:spring.xml").getBean(Service.class);
    }

    @GetMapping(value = "/suggested-severity")
    public ResponseEntity<?> getSuggestedSeverity(@RequestParam(value = "title") String title) {
        try {
            return new ResponseEntity<>(service.predictSeverityLevel(title), HttpStatus.OK);
        } catch (AiServiceException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/suggested-type")
    public ResponseEntity<?> getSuggestedType(@RequestParam(value = "title") String title) {
        try {
            return new ResponseEntity<>(service.predictIssueType(title), HttpStatus.OK);
        } catch (AiServiceException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
