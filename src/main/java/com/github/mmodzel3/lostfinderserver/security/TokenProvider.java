package com.github.mmodzel3.lostfinderserver.security;

import com.github.mmodzel3.lostfinderserver.user.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
public class TokenProvider {

    @Value("${jwt.secret:A94kZt+2iJKbFw8atLuC3wYxN4ZIh5XwJg62pVFjNfg=A}")
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

    TokenDetails parseToken(String token) throws InvalidTokenException {
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
