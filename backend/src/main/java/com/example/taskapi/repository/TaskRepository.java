package com.example.taskapi.repository;

import com.example.taskapi.model.*;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {
  @Query("select t from Task t where t.project.id = :projectId and (:status is null or t.status = :status) and (:assigneeId is null or t.assignedTo.id = :assigneeId) and (:dueDate is null or t.dueDate = :dueDate)")
  Page<Task> search(@Param("projectId") Long projectId, @Param("status") TaskStatus status, @Param("assigneeId") Long assigneeId, @Param("dueDate") LocalDate dueDate, Pageable pageable);
  Optional<Task> findByIdAndProjectId(Long id, Long projectId);
}
