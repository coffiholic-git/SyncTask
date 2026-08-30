package com.example.taskapi.controller;

import com.example.taskapi.exception.ApiException;
import com.example.taskapi.model.User;
import com.example.taskapi.repository.UserRepository;
import com.example.taskapi.service.AppService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final AppService service;
  private final UserRepository users;
  public UserController(AppService service, UserRepository users) {
    this.service = service; this.users = users;
  }
  @GetMapping("/me") public User me(Authentication authentication) {
    return service.current(authentication);
  }
  @GetMapping("/{id}") public User get(@PathVariable Long id) {
    return users.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
  }
}
