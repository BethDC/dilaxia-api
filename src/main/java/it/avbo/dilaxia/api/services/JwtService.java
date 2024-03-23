package it.avbo.dilaxia.api.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import it.avbo.dilaxia.api.entities.User;
import org.apache.commons.lang3.RandomStringUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

public class JwtService {
    private final static String SECRET_KEY = RandomStringUtils.random(64, true, true);

    public static String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 24)) // 1 giorno
                .signWith(getSigningKey())
                .compact();
    }

    public static boolean isValid(String token, User user) {
        if(extractClaim(token, Claims::getExpiration).before(new Date(System.currentTimeMillis())))
            return false;

        String username = extractUsername(token);
        return username.equals(user.getUsername());
    }

    public static String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public static <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private static Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    private static SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
