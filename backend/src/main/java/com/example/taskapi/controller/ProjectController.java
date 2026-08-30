package com.example.taskapi.controller;

import com.example.taskapi.dto.ProjectDtos.MemberRequest;
import com.example.taskapi.dto.ProjectDtos.ProjectRequest;
import com.example.taskapi.model.Project;
import com.example.taskapi.service.AppService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
  private final AppService service;
  public ProjectController(AppService service) {
    this.service = service;
  }
  @PostMapping @ResponseStatus(HttpStatus.CREATED)
  public Project create(@Valid @RequestBody ProjectRequest request, Authentication auth) {
    return service.createProject(request, service.current(auth));
  }
  @GetMapping
  public Page<Project> list(@PageableDefault(size = 20, sort = "createdAt") Pageable pageable, Authentication auth) {
    return service.projects(service.current(auth), pageable);
  }
  @GetMapping("/{id}") public Project get(@PathVariable Long id, Authentication auth) {
    return service.getProject(id, service.current(auth));
  }
  @PutMapping("/{id}") public Project update(@PathVariable Long id, @Valid @RequestBody ProjectRequest request, Authentication auth) {
    return service.updateProject(id, request, service.current(auth));
  }
  @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id, Authentication auth) {
    service.deleteProject(id, service.current(auth));
  }
  @PostMapping("/{id}/members")
  public Project add(@PathVariable Long id, @RequestBody MemberRequest request, Authentication auth) {
    return service.addMember(id, request.userId(), service.current(auth));
  }
  @DeleteMapping("/{id}/members/{userId}") @ResponseStatus(HttpStatus.NO_CONTENT)
  public void remove(@PathVariable Long id, @PathVariable Long userId, Authentication auth) {
    service.removeMember(id, userId, service.current(auth));
  }
}
