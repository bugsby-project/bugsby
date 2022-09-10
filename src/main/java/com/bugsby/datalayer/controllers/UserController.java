package com.bugsby.datalayer.controllers;

import com.bugsby.datalayer.controllers.dtos.UserDto;
import com.bugsby.datalayer.model.User;
import com.bugsby.datalayer.service.Service;
import com.bugsby.datalayer.service.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Function;

@RestController
@CrossOrigin
@RequestMapping(value = "users")
public class UserController {
    @Autowired
    private Service service;
    @Autowired
    private Function<User, UserDto> userMapper;

    /**
     * Handler responsible for creating new accounts
     *
     * @param user: User, the details of the user
     * @return - a response containing the user with an assigned id, if there are no errors
     * - the error message, otherwise
     */
    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody User user) {
        try {
            User result = service.createAccount(user);
            return new ResponseEntity<>(userMapper.apply(result), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid data", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Handler responsible for retrieving the information of a user, based on their username
     *
     * @param username, the username of the desired user
     * @return - a response containing the user, if there is a user with the given username
     * - a 404 Not Found, otherwise
     */
    @GetMapping
    public ResponseEntity<?> getUser(@RequestParam(value = "username") String username) {
        try {
            User user = service.login(username);
            return new ResponseEntity<>(userMapper.apply(user), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Handler responsible for retrieving the usernames of all the memorised users
     *
     * @return a response containing all the usernames
     */
    @GetMapping(value = "/usernames")
    public ResponseEntity<?> getAllUsernames() {
        return new ResponseEntity<>(service.getAllUsernames(), HttpStatus.OK);
    }
}
