package com.example.taskapi.controller;

import com.example.taskapi.dto.AuthDtos.AuthResponse;
import com.example.taskapi.dto.AuthDtos.LoginRequest;
import com.example.taskapi.dto.AuthDtos.PasswordResetRequest;
import com.example.taskapi.dto.AuthDtos.PasswordResetTokenResponse;
import com.example.taskapi.dto.AuthDtos.RegisterRequest;
import com.example.taskapi.dto.AuthDtos.ResetPasswordRequest;
import com.example.taskapi.service.AppService;
import com.example.taskapi.security.TokenBlacklistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AppService service;
  private final TokenBlacklistService blacklist;
  public AuthController(AppService service, TokenBlacklistService blacklist) {
    this.service = service; this.blacklist = blacklist;
  }
  @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED)
  public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
    return service.register(request);
  }
  @PostMapping("/login")
  public AuthResponse login(@Valid @RequestBody LoginRequest request) {
    return service.login(request);
  }

  @PostMapping("/forgot-password")
  public PasswordResetTokenResponse forgotPassword(
      @Valid @RequestBody PasswordResetRequest request) {
    return service.requestPasswordReset(request);
  }

  @PostMapping("/reset-password")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
    service.resetPassword(request);
  }
  @PostMapping("/logout") @ResponseStatus(HttpStatus.NO_CONTENT)
  public void logout(@RequestHeader("Authorization") String authorization) {
    if (authorization == null || !authorization.startsWith("Bearer ")) throw new IllegalArgumentException("Bearer token is required");
    blacklist.revoke(authorization.substring(7));
  }
}
