package com.example.taskapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "projects")
public class Project {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @Column(nullable = false) private String name;
  @Column(length = 2000) private String description;
  @ManyToOne(optional = false) @JoinColumn(name = "created_by") private User createdBy;
  @Column(nullable = false, updatable = false) private Instant createdAt = Instant.now();
  private Instant updatedAt;
  @ManyToOne @JoinColumn(name = "updated_by") private User updatedBy;
  @ManyToMany
  @JoinTable(name = "project_members", joinColumns = @JoinColumn(name = "project_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
  private Set<User> members = new HashSet<>();
  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true) @JsonIgnore private List<Task> tasks = new ArrayList<>();
  public Long getId() {
    return id; }
  public String getName() {
    return name; }
  public String getDescription() {
    return description; }
  public User getCreatedBy() {
    return createdBy; }
  public Instant getCreatedAt() {
    return createdAt; }
  public Instant getUpdatedAt() {
    return updatedAt; }
  public User getUpdatedBy() {
    return updatedBy; }
  public Set<User> getMembers() {
    return members; }
  public void setName(String value) {
    name = value; }
  public void setDescription(String value) {
    description = value; }
  public void setCreatedBy(User value) {
    createdBy = value; }
  public void setUpdatedAt(Instant value) {
    updatedAt = value; }
  public void setUpdatedBy(User value) {
    updatedBy = value; }
}
