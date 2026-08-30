package com.example.taskapi.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name = "tasks")
public class Task {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @ManyToOne(optional = false) @JoinColumn(name = "project_id") private Project project;
  @Column(nullable = false) private String title;
  @Column(length = 3000) private String description;
  @Enumerated(EnumType.STRING) @Column(nullable = false) private TaskStatus status = TaskStatus.TODO;
  @ManyToOne @JoinColumn(name = "assigned_to") private User assignedTo;
  private LocalDate dueDate;
  @ManyToOne(optional = false) @JoinColumn(name = "created_by") private User createdBy;
  private Instant updatedAt;
  @ManyToOne @JoinColumn(name = "updated_by") private User updatedBy;
  public Long getId() {
    return id; }
  public Project getProject() {
    return project; }
  public String getTitle() {
    return title; }
  public String getDescription() {
    return description; }
  public TaskStatus getStatus() {
    return status; }
  public User getAssignedTo() {
    return assignedTo; }
  public LocalDate getDueDate() {
    return dueDate; }
  public User getCreatedBy() {
    return createdBy; }
  public Instant getUpdatedAt() {
    return updatedAt; }
  public User getUpdatedBy() {
    return updatedBy; }
  public void setProject(Project value) {
    project = value; }
  public void setTitle(String value) {
    title = value; }
  public void setDescription(String value) {
    description = value; }
  public void setStatus(TaskStatus value) {
    status = value; }
  public void setAssignedTo(User value) {
    assignedTo = value; }
  public void setDueDate(LocalDate value) {
    dueDate = value; }
  public void setCreatedBy(User value) {
    createdBy = value; }
  public void setUpdatedAt(Instant value) {
    updatedAt = value; }
  public void setUpdatedBy(User value) {
    updatedBy = value; }
}
