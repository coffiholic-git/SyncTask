package com.example.taskapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String email;

  @JsonIgnore
  @Column(nullable = false)
  private String passwordHash;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role = Role.MEMBER;

  @ManyToMany(mappedBy = "members")
  @JsonIgnore
  private Set<Project> projects = new HashSet<>();

  public Long getId() {
    return id; }
  public String getName() {
    return name; }
  public String getEmail() {
    return email; }
  public String getPasswordHash() {
    return passwordHash; }
  public Role getRole() {
    return role; }

  public void setName(String value) {
    name = value; }
  public void setEmail(String value) {
    email = value; }
  public void setPasswordHash(String value) {
    passwordHash = value; }
  public void setRole(Role value) {
    role = value; }
}
