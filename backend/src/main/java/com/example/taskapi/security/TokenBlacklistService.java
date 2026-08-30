package com.example.taskapi.security;

import java.time.Duration;
import java.util.Date;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {
  private static final String PREFIX = "revoked-token:";
  private final StringRedisTemplate redis;
  private final JwtService jwt;

  public TokenBlacklistService(StringRedisTemplate redis, JwtService jwt) {
    this.redis = redis; this.jwt = jwt; }

  public void revoke(String token) {
    var claims = jwt.claims(token);
    long remainingMillis = claims.getExpiration().getTime() - System.currentTimeMillis();
    if (remainingMillis > 0) redis.opsForValue().set(PREFIX + claims.getId(), "revoked", Duration.ofMillis(remainingMillis));
  }

  public boolean isRevoked(String token) {
    String id = jwt.claims(token).getId();
    return Boolean.TRUE.equals(redis.hasKey(PREFIX + id));
  }
}
