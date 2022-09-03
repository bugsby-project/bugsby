package com.bugsby.datalayer.controllers.security;

import com.bugsby.datalayer.controllers.dtos.UserDto;

/**
 * Abstraction of a response obtained after a successful authentication.
 */
public class AuthenticationResponse {
    private final String jwt;
    private final UserDto user;

    public AuthenticationResponse(String jwt, UserDto user) {
        this.jwt = jwt;
        this.user = user;
    }

    public String getJwt() {
        return jwt;
    }

    public UserDto getUser() {
        return user;
    }
}
