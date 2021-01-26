package com.github.mmodzel3.lostfinderserver.security.authentication.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
public class TokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    public String generateToken(TokenDetails token) {
        Long now = System.currentTimeMillis();

        Claims claims = Jwts.claims()
                .setId(UUID.randomUUID().toString())
                .setSubject(token.getUserEmail())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expiration));

        return Jwts.builder()
                .setClaims(claims)
                .signWith(getSigningKey())
                .compact();
    }

    public TokenDetails parseToken(String token) throws InvalidTokenException {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String userEmail = claims.getSubject();
            return new TokenDetails(userEmail);
        } catch (ExpiredJwtException ex) {
            throw new InvalidTokenException("Token expired: " + ex.getMessage());
        } catch (JwtException ex) {
            throw new InvalidTokenException("Token parse error: " + ex.getMessage());
        }
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
