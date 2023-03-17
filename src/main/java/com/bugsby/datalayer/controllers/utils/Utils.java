package com.bugsby.datalayer.controllers.utils;

import com.bugsby.datalayer.controllers.security.JwtUtils;
import com.bugsby.datalayer.controllers.security.SecurityConstants;

public class Utils {

    private Utils() {
        throw new IllegalStateException("Utility class");
    }

    public static String extractUsername(String token) {
        if (!token.startsWith(SecurityConstants.BEARER)) {
            return "";
        }

        token = token.substring(SecurityConstants.BEARER.length());
        return JwtUtils.extractUsername(token);
    }
}
