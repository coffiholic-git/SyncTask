package com.example.taskapi.controller;

import com.example.taskapi.dto.TaskDtos.*;
import com.example.taskapi.model.*;
import com.example.taskapi.service.AppService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
public class TaskController {
  private final AppService service;
  public TaskController(AppService service) {
    this.service = service;
  }
  @PostMapping @ResponseStatus(HttpStatus.CREATED)
  public Task create(@PathVariable Long projectId, @Valid @RequestBody TaskRequest request, Authentication auth) {
    return service.createTask(projectId, request, service.current(auth));
  }
  @GetMapping
  public Page<Task> list(@PathVariable Long projectId, @RequestParam(required = false) TaskStatus status, @RequestParam(required = false) Long assigneeId, @RequestParam(required = false) LocalDate dueDate, @PageableDefault(size = 20, sort = "dueDate") Pageable pageable, Authentication auth) {
    return service.listTasks(projectId, status, assigneeId, dueDate, pageable, service.current(auth));
  }
  @GetMapping("/{taskId}") public Task get(@PathVariable Long projectId, @PathVariable Long taskId, Authentication auth) {
    return service.getTask(projectId, taskId, service.current(auth));
  }
  @PutMapping("/{taskId}") public Task update(@PathVariable Long projectId, @PathVariable Long taskId, @Valid @RequestBody TaskRequest request, Authentication auth) {
    return service.updateTask(projectId, taskId, request, service.current(auth));
  }
  @DeleteMapping("/{taskId}") @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long projectId, @PathVariable Long taskId, Authentication auth) {
    service.deleteTask(projectId, taskId, service.current(auth));
  }
  @PatchMapping("/{taskId}/status") public Task status(@PathVariable Long projectId, @PathVariable Long taskId, @RequestBody StatusRequest request, Authentication auth) {
    return service.status(projectId, taskId, request.status(), service.current(auth));
  }
  @GetMapping("/{taskId}/history")
  public Page<TaskActivity> history(@PathVariable Long projectId, @PathVariable Long taskId, @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable, Authentication auth) {
    return service.history(projectId, taskId, pageable, service.current(auth));
  }
}
