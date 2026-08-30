package com.example.taskapi.dto;
import jakarta.validation.constraints.NotBlank;
public final class ProjectDtos {
  private ProjectDtos(){

  }
  public record ProjectRequest(@NotBlank String name, String description){

  }
  public record MemberRequest(Long userId){

  }
}
