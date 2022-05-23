package utils;

import security.JwtUtils;
import security.SecurityConstants;

public class Utils {
    public static String extractUsername(String token) {
        if (!token.startsWith(SecurityConstants.BEARER)) {
            return "";
        }

        token = token.substring(SecurityConstants.BEARER.length());
        return JwtUtils.extractUsername(token);
    }
}
