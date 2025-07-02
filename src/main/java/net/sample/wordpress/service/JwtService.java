package net.sample.wordpress.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);


    @Value("${jwt.secret}")
    private String secretKey;

    public String generateToken(String username,Long userId) {
        logger.info("Generating JWT for user: {} with ID: {}", username, userId);
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("userId", userId);
        String token= Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .and()
                .signWith(getSecretKey())
                .compact();
        logger.debug("JWT token generated successfully for user: {}", username);
        return token;
    }
    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String jwtToken) {
        logger.debug("Extracting username from token");
        return extractClaim(jwtToken, Claims::getSubject);
    }
    public Long extractUserId(String token) {
        logger.debug("Extracting userId from token");
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Long.class);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver)
    {
        final Claims claims=extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        logger.debug("Parsing all claims from token");
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String jwtToken, UserDetails userDetails) {
        final String username = extractUserName(jwtToken);
        boolean isValid= (username.equals(userDetails.getUsername()) && !isTokenExpired(jwtToken));
        if (isValid) {
            logger.info("JWT token is valid for user: {}", username);
        } else {
            logger.warn("JWT token is invalid or expired for user: {}", username);
        }
        return isValid;

    }

    private boolean isTokenExpired(String jwtToken)
    {
        Date expiration = extractExpiration(jwtToken);
        boolean expired = expiration.before(new Date());
        logger.debug("Token expiration check: expired={}", expired);
        return expired;
    }

    private Date extractExpiration(String token) {

        return extractClaim(token, Claims::getExpiration);
    }





}
