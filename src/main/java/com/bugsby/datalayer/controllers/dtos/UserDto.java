package com.bugsby.datalayer.controllers.dtos;

import com.bugsby.datalayer.model.User;

public class UserDto {
    private final Long id;
    private final String username;
    private final String password;
    private final String firstName;
    private final String lastName;
    private final String email;

    public UserDto(Long id, String username, String password, String firstName, String lastName, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public static UserDto from(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(), user.getEmail());
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }
}
