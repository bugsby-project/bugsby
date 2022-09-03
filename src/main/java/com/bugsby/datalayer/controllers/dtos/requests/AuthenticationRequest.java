package com.bugsby.datalayer.controllers.dtos.requests;

/**
 * Abstraction of a request to authenticate.
 */
public record AuthenticationRequest(String username, String password) {
}
