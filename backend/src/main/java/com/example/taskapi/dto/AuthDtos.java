package com.example.taskapi.dto;
import jakarta.validation.constraints.*;
public final class AuthDtos {
  private AuthDtos() {

  }
  public record RegisterRequest(@NotBlank String name, @Email @NotBlank String email, @NotBlank @Size(min = 8, max = 100) String password) {
  }
  public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {
  }
  public record PasswordResetRequest(@Email @NotBlank String email) {
  }
  public record ResetPasswordRequest(@NotBlank String token, @NotBlank @Size(min = 8, max = 100) String newPassword) {
  }
  public record PasswordResetTokenResponse(String message, String resetToken) {
  }
  public record AuthResponse(
      String token,
      String tokenType,
      Long userId,
      String name,
      String email,
      String role) {
  }
}
