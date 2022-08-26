package com.bugsby.datalayer.security;

public class SecurityConstants {
    public static final String SECRET = "RgRPL7Auio2JlRn9J61kw09aqdqeQQK8";

    public static final String BEARER = "Bearer ";

    public static final String AUTHENTICATION_FAILED_MESSAGE = "Incorrect username or password";

    public static final long JWT_EXPIRATION_MS = 8 * 3600000;  // 8 hours
}
