package com.bugsby.datalayer.controllers.dtos.requests;

import com.bugsby.datalayer.controllers.dtos.UserDto;

/**
 * Abstraction of a response obtained after a successful authentication.
 */
public record AuthenticationResponse(String jwt, UserDto user) {
}
