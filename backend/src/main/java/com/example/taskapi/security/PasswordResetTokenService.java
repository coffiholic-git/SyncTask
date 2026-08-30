package com.example.taskapi.security;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetTokenService {
  private static final String KEY_PREFIX = "password-reset:";

  private final StringRedisTemplate redis;
  private final long expiryMinutes;
  private final SecureRandom secureRandom = new SecureRandom();

  public PasswordResetTokenService(
      StringRedisTemplate redis,
      @Value("${app.password-reset.expiry-minutes}") long expiryMinutes) {
    this.redis = redis;
    this.expiryMinutes = expiryMinutes;
  }

  public String createToken(String email) {
    byte[] randomBytes = new byte[32];
    secureRandom.nextBytes(randomBytes);

    String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    String key = KEY_PREFIX + token;

    redis.opsForValue().set(key, email, Duration.ofMinutes(expiryMinutes));
    return token;
  }

  public String consumeToken(String token) {
    String key = KEY_PREFIX + token;
    String email = redis.opsForValue().get(key);

    if (email != null) {
      redis.delete(key);
    }

    return email;
  }
}
