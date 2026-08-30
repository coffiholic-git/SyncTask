package com.example.taskapi.security;

import com.example.taskapi.exception.ApiException;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class LoginRateLimitService {
  private final StringRedisTemplate redis;
  private final int maxAttempts;
  private final long windowSeconds;

  public LoginRateLimitService(StringRedisTemplate redis, @Value("${app.rate-limit.login-attempts}") int maxAttempts, @Value("${app.rate-limit.login-window-seconds}") long windowSeconds) {
    this.redis = redis; this.maxAttempts = maxAttempts; this.windowSeconds = windowSeconds;
  }

  public void check(String email) {
    String key = "login-attempts:" + email.toLowerCase();
    Long attempts = redis.opsForValue().increment(key);
    if (attempts != null && attempts == 1) redis.expire(key, Duration.ofSeconds(windowSeconds));
    if (attempts != null && attempts > maxAttempts) throw new ApiException(HttpStatus.TOO_MANY_REQUESTS, "Too many login attempts. Please try again later.");
  }

  public void clear(String email) {
    redis.delete("login-attempts:" + email.toLowerCase()); }
}
