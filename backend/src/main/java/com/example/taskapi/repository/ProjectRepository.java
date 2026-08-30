package com.example.taskapi.repository;

import com.example.taskapi.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
  Page<Project> findDistinctByMembers_Id(Long userId, Pageable pageable);
}
