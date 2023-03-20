package com.bugsby.datalayer.controllers.security;

public class SecurityConstants {

    private SecurityConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String SECRET = "RgRPL7Auio2JlRn9J61kw09aqdqeQQK8";

    public static final String BEARER = "Bearer ";

    public static final String AUTHENTICATION_FAILED_MESSAGE = "Incorrect username or password";

    public static final long JWT_EXPIRATION_MS = 8L * 3600000;  // 8 hours
}
