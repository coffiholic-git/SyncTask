package com.example.taskapi.repository;

import com.example.taskapi.model.TaskActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskActivityRepository extends JpaRepository<TaskActivity, Long> {
  Page<TaskActivity> findByTaskIdOrderByCreatedAtDesc(Long taskId, Pageable pageable);
}
