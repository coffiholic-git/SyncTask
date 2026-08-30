package com.example.taskapi.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  private final Key key;
  private final long expiry;

  public JwtService(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.expiration-ms}") long expiry) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expiry = expiry;
  }

  public String generate(String email, String role) {
    return Jwts.builder().id(UUID.randomUUID().toString()).subject(email).claim("role", role)
        .issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + expiry)).signWith(key).compact();
  }

  public Claims claims(String token) {
    return Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token).getPayload();
  }

  public String email(String token) {
    return claims(token).getSubject(); }
  public boolean valid(String token) {
    try {
      claims(token); return true; } catch (JwtException | IllegalArgumentException exception) {
      return false; } }
}
