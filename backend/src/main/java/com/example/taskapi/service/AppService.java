package com.example.taskapi.service;

import com.example.taskapi.dto.*;
import com.example.taskapi.exception.ApiException;
import com.example.taskapi.model.*;
import com.example.taskapi.repository.*;
import com.example.taskapi.security.JwtService;
import com.example.taskapi.security.LoginRateLimitService;
import com.example.taskapi.security.PasswordResetTokenService;
import java.time.LocalDate;
import java.util.List;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AppService {
  private static final Logger log = LoggerFactory.getLogger(AppService.class);
  private final UserRepository users;
  private final ProjectRepository projects;
  private final TaskRepository tasks;
  private final TaskActivityRepository activities;
  private final PasswordEncoder encoder;
  private final JwtService jwt;
  private final LoginRateLimitService loginRateLimit;
  private final PasswordResetTokenService passwordResetTokens;

  public AppService(
      UserRepository users,
      ProjectRepository projects,
      TaskRepository tasks,
      TaskActivityRepository activities,
      PasswordEncoder encoder,
      JwtService jwt,
      LoginRateLimitService loginRateLimit,
      PasswordResetTokenService passwordResetTokens) {
    this.users = users;
    this.projects = projects;
    this.tasks = tasks;
    this.activities = activities;
    this.encoder = encoder;
    this.jwt = jwt;
    this.loginRateLimit = loginRateLimit;
    this.passwordResetTokens = passwordResetTokens;
  }

  public User current(Authentication authentication) {
    return users.findByEmail(authentication.getName())
        .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found"));
  }

  public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
    if (users.existsByEmail(request.email().toLowerCase())) {
      throw new ApiException(HttpStatus.CONFLICT, "Email is already registered");
    }
    User user = new User();
    user.setName(request.name());
    user.setEmail(request.email().toLowerCase());
    user.setPasswordHash(encoder.encode(request.password()));
    user.setRole(Role.MEMBER);
    users.save(user);
    return auth(user);
  }

  public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
    loginRateLimit.check(request.email());
    User user = users.findByEmail(request.email().toLowerCase())
        .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
    if (!encoder.matches(request.password(), user.getPasswordHash())) {
      throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }
    loginRateLimit.clear(request.email());
    return auth(user);
  }

  public AuthDtos.PasswordResetTokenResponse requestPasswordReset(
      AuthDtos.PasswordResetRequest request) {
    User user = users.findByEmail(request.email().toLowerCase()).orElse(null);

    // Never return the actual reset token in the API response: anyone who knows a
    // user's email could otherwise grab it here and immediately call reset-password
    // to take over the account, without ever touching the user's inbox.
    // TODO: wire up a real email service and send the token there instead of logging it.
    if (user != null) {
      String token = passwordResetTokens.createToken(user.getEmail());
      log.info("Password reset requested for {}. Reset token (send via email): {}", user.getEmail(), token);
    }

    return new AuthDtos.PasswordResetTokenResponse(
        "If that email exists, a password-reset link has been sent.",
        null);
  }

  public void resetPassword(AuthDtos.ResetPasswordRequest request) {
    String email = passwordResetTokens.consumeToken(request.token());

    if (email == null) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Reset token is invalid or has expired");
    }

    User user = users.findByEmail(email)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

    user.setPasswordHash(encoder.encode(request.newPassword()));
  }

  private AuthDtos.AuthResponse auth(User user) {
    return new AuthDtos.AuthResponse(jwt.generate(user.getEmail(), user.getRole().name()), "Bearer", user.getId(), user.getName(), user.getEmail(), user.getRole().name());
  }

  private boolean elevated(User user) {
    return user.getRole() == Role.ADMIN || user.getRole() == Role.MANAGER; }
  private Project project(Long id) {
    return projects.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Project not found")); }
  private Task task(Long projectId, Long id) {
    return tasks.findByIdAndProjectId(id, projectId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Task not found")); }

  private void member(Project project, User user) {
    if (project.getMembers().stream().noneMatch(member -> member.getId().equals(user.getId()))) {
      throw new ApiException(HttpStatus.FORBIDDEN, "You are not a project member");
    }
  }

  private void manager(Project project, User user) {
    member(project, user);
    if (!elevated(user)) throw new ApiException(HttpStatus.FORBIDDEN, "Manager or admin role is required");
  }

  public Project createProject(ProjectDtos.ProjectRequest request, User user) {
    if (!elevated(user)) throw new ApiException(HttpStatus.FORBIDDEN, "Manager or admin role is required");
    Project project = new Project();
    project.setName(request.name());
    project.setDescription(request.description());
    project.setCreatedBy(user);
    project.getMembers().add(user);
    return projects.save(project);
  }

  public Page<Project> projects(User user, Pageable pageable) {
    Page<Project> page = projects.findDistinctByMembers_Id(user.getId(), pageable);
    // Force the lazy `members` collection to load now, while the transaction/session
    // is still open. open-in-view is disabled, so if we don't do this here the
    // collection is still an uninitialized proxy when Jackson serializes the
    // response later, causing a LazyInitializationException.
    page.getContent().forEach(project -> project.getMembers().size());
    return page; }
  public Project getProject(Long id, User user) {
    Project project = project(id); member(project, user); return project; }
  public Project updateProject(Long id, ProjectDtos.ProjectRequest request, User user) {
    Project project = project(id); manager(project, user); project.setName(request.name()); project.setDescription(request.description()); project.setUpdatedAt(Instant.now()); project.setUpdatedBy(user); return project; }
  public void deleteProject(Long id, User user) {
    if (user.getRole() != Role.ADMIN) throw new ApiException(HttpStatus.FORBIDDEN, "Admin role is required"); projects.delete(project(id)); }

  public Project addMember(Long id, Long userId, User user) {
    Project project = project(id); manager(project, user);
    User newMember = users.findById(userId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    project.getMembers().add(newMember);
    return project;
  }

  public void removeMember(Long id, Long userId, User user) {
    Project project = project(id); manager(project, user);
    if (project.getCreatedBy().getId().equals(userId)) throw new ApiException(HttpStatus.BAD_REQUEST, "Project creator cannot be removed");
    project.getMembers().removeIf(member -> member.getId().equals(userId));
  }

  public Task createTask(Long id, TaskDtos.TaskRequest request, User user) {
    Project project = project(id); manager(project, user);
    Task task = new Task(); task.setProject(project); task.setTitle(request.title()); task.setDescription(request.description());
    task.setStatus(request.status() == null ? TaskStatus.TODO : request.status()); task.setDueDate(request.dueDate()); task.setCreatedBy(user);
    if (request.assignedToId() != null) task.setAssignedTo(assignee(project, request.assignedToId()));
    Task saved = tasks.save(task);
    activity(saved, user, "TASK_CREATED", "Created task: " + saved.getTitle());
    if (saved.getAssignedTo() != null) activity(saved, user, "TASK_ASSIGNED", "Assigned to " + saved.getAssignedTo().getEmail());
    return saved;
  }

  private User assignee(Project project, Long id) {
    User user = users.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Assignee not found")); member(project, user); return user; }
  public Page<Task> listTasks(Long id, TaskStatus status, Long assigneeId, LocalDate dueDate, Pageable pageable, User user) {
    Project project = project(id); member(project, user); return tasks.search(id, status, assigneeId, dueDate, pageable); }
  public Task getTask(Long projectId, Long id, User user) {
    Project project = project(projectId); member(project, user); return task(projectId, id); }

  public Task updateTask(Long projectId, Long id, TaskDtos.TaskRequest request, User user) {
    Project project = project(projectId); Task task = task(projectId, id); member(project, user);
    if (!elevated(user)) throw new ApiException(HttpStatus.FORBIDDEN, "Members may update only their assigned task status via the status endpoint");
    task.setTitle(request.title()); task.setDescription(request.description()); task.setDueDate(request.dueDate());
    if (request.status() != null) task.setStatus(request.status());
    if (request.assignedToId() != null) {
      User assignee = assignee(project, request.assignedToId());
      boolean changed = task.getAssignedTo() == null || !task.getAssignedTo().getId().equals(assignee.getId());
      task.setAssignedTo(assignee);
      if (changed) activity(task, user, "TASK_ASSIGNED", "Assigned to " + assignee.getEmail());
    }
    task.setUpdatedAt(Instant.now()); task.setUpdatedBy(user);
    activity(task, user, "TASK_UPDATED", "Updated task details");
    return task;
  }

  public void deleteTask(Long projectId, Long id, User user) {
    Project project = project(projectId); manager(project, user); tasks.delete(task(projectId, id)); }
  public Task status(Long projectId, Long id, TaskStatus status, User user) {
    if (status == null) throw new ApiException(HttpStatus.BAD_REQUEST, "Status is required");
    Task task = task(projectId, id);
    // Same lazy-loading issue as projects(): task.getProject().getMembers() is never
    // touched on this path, so force it to initialize before the transaction closes.
    task.getProject().getMembers().size();
    if (task.getAssignedTo() == null || !task.getAssignedTo().getId().equals(user.getId())) throw new ApiException(HttpStatus.FORBIDDEN, "Only the assignee may update task status");
    task.setStatus(status); task.setUpdatedAt(Instant.now()); task.setUpdatedBy(user);
    activity(task, user, "STATUS_CHANGED", "Changed status to " + status);
    return task;
  }

  public Page<TaskActivity> history(Long projectId, Long taskId, Pageable pageable, User user) {
    Project project = project(projectId);
    member(project, user);
    task(projectId, taskId);
    return activities.findByTaskIdOrderByCreatedAtDesc(taskId, pageable);
  }

  private void activity(Task task, User actor, String action, String details) {
    TaskActivity activity = new TaskActivity();
    activity.setTask(task);
    activity.setActor(actor);
    activity.setAction(action);
    activity.setDetails(details);
    activities.save(activity);
  }
}
