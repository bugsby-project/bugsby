package com.bugsby.datalayer.controllers.dtos;

public record UserDto(Long id, String username, String password, String firstName, String lastName, String email) {
}
