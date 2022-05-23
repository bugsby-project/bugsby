package security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

public class JwtUtils {
    /**
     * Method for extracting the username from a JSON Web Token
     * @param token: String, the web token
     * @return - the username, if the token is valid
     *         - the empty string, otherwise
     */
    public static String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Method for extracting the date from a JSON Web Token
     * @param token: String, the web token
     * @return an {@code Optional}
     *         - with the date, if the token is valid
     *         - empty, otherwise
     */
    public static Optional<Date> extractExpiration(String token) {
        try {
            return Optional.of(extractClaim(token, Claims::getExpiration));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private static Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SecurityConstants.SECRET).parseClaimsJws(token).getBody();
    }

    private static boolean isTokenExpired(String token) {
        Optional<Date> date = extractExpiration(token);
        return date.map(value -> value.before(new Date())).orElse(true);
    }

    /**
     * Method for validating a JSON Web Token
     * @param token: String, the web token
     * @param userDetails: UserDetails, encapsulates the username and password of the user
     * @return true, if the token is valid, according to the userDetails, false, otherwise
     */
    public static boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Method for generating a JSON Web Token based on a user's details
     * @param userDetails: UserDetails, encapsulates the username and the password of the user
     * @return the corresponding JSON Web Token
     */
    public static String generateToken(UserDetails userDetails) {
        Date now = new Date();
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + SecurityConstants.JWT_EXPIRATION_MS))
                .signWith(SignatureAlgorithm.HS256, SecurityConstants.SECRET)
                .compact();
    }
}
