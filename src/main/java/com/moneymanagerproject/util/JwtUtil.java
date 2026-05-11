package com.moneymanagerproject.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // 🔐 Secret Key (Base64 encoded string - change in production)
    private static final String SECRET_KEY ="mysupersecuresecretmysupersecuresecretmysupersecuresecret123456";

    // ⏳ 1 Month expiration (30 days)
    private static final long EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 30;

    // 🔑 Get signing key
    private Key getSignKey() {
        byte[] keyBytes = SECRET_KEY.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 🔥 Generate Token
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 🔍 Extract Username
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 🔍 Extract Expiration
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 🔄 Extract Claim
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    // 🔐 Extract All Claims
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ✅ Check if token expired
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // ✅ Validate Token
    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
