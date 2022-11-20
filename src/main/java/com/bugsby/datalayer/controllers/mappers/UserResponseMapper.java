package com.bugsby.datalayer.controllers.mappers;

import com.bugsby.datalayer.model.User;
import com.bugsby.datalayer.swagger.model.UserResponse;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class UserResponseMapper implements Function<User, UserResponse> {
    @Override
    public UserResponse apply(User user) {
        return new UserResponse()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail());
    }
}
