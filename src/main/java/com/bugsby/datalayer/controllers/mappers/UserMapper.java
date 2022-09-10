package com.bugsby.datalayer.controllers.mappers;

import com.bugsby.datalayer.controllers.dtos.UserDto;
import com.bugsby.datalayer.model.User;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class UserMapper implements Function<User, UserDto> {
    @Override
    public UserDto apply(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(), user.getEmail());
    }
}
