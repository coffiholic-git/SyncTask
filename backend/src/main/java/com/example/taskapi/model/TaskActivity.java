package com.example.taskapi.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "task_activities")
public class TaskActivity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @ManyToOne(optional = false) @JoinColumn(name = "task_id") private Task task;
  @ManyToOne(optional = false) @JoinColumn(name = "actor_id") private User actor;
  @Column(nullable = false) private String action;
  @Column(length = 2000) private String details;
  @Column(nullable = false, updatable = false) private Instant createdAt = Instant.now();
  public Long getId() {
    return id; }
  public Task getTask() {
    return task; }
  public User getActor() {
    return actor; }
  public String getAction() {
    return action; }
  public String getDetails() {
    return details; }
  public Instant getCreatedAt() {
    return createdAt; }
  public void setTask(Task value) {
    task = value; }
  public void setActor(User value) {
    actor = value; }
  public void setAction(String value) {
    action = value; }
  public void setDetails(String value) {
    details = value; }
}
