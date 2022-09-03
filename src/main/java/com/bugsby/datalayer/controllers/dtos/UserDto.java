package com.bugsby.datalayer.controllers.dtos;

import com.bugsby.datalayer.model.User;

public record UserDto(Long id, String username, String password, String firstName, String lastName, String email) {
    public static UserDto from(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(), user.getEmail());
    }
}
