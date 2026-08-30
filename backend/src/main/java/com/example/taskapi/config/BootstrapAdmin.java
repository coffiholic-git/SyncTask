package com.example.taskapi.config;

import com.example.taskapi.model.*;
import com.example.taskapi.model.Role;
import com.example.taskapi.repository.UserRepository;

import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BootstrapAdmin {
  @Bean
 CommandLineRunner seedAdmin(
         UserRepository r,
         PasswordEncoder e,
         @Value("${app.bootstrap-admin.email}") String email,
         @Value("${app.bootstrap-admin.password}") String password,
         @Value("${app.bootstrap-admin.name}") String name) {
    return args -> {
      if (!r.existsByEmail(email.toLowerCase())) {
        User u = new User();
    u.setName(name);
    u.setEmail(email.toLowerCase());
    u.setPasswordHash(e.encode(password));
    u.setRole(Role.ADMIN);
    r.save(u);
   }
  };
 }
}
