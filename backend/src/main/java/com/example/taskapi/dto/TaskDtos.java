package com.example.taskapi.dto;
import com.example.taskapi.model.TaskStatus; import jakarta.validation.constraints.NotBlank; import java.time.LocalDate;
public final class TaskDtos {
  private TaskDtos(){

    }
    public record TaskRequest(@NotBlank String title,String description,Long assignedToId,LocalDate dueDate,TaskStatus status){

    }
    public record StatusRequest(TaskStatus status){
    }
}
