package com.bugsby.datalayer.controllers.mappers;

import com.bugsby.datalayer.model.User;
import com.bugsby.datalayer.swagger.model.UserRequest;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class UserRequestMapper implements Function<UserRequest, User> {
    @Override
    public User apply(UserRequest userRequest) {
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setPassword(userRequest.getPassword());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        return user;
    }
}
