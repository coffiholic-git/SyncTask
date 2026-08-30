package com.example.taskapi.exception;
import java.time.Instant; import java.util.*; import org.springframework.http.*; import org.springframework.web.bind.*; import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  record ErrorResponse(Instant timestamp,int status,String error,String message,Map<String,String> validation){
    }
 @ExceptionHandler(ApiException.class) ResponseEntity<ErrorResponse> api(ApiException e){
    return response(e.getStatus(),e.getMessage(),Map.of());
  }
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException e){
    Map<String,String> m=new HashMap<>();e.getBindingResult().getFieldErrors().forEach(x->m.put(x.getField(),x.getDefaultMessage()));return response(HttpStatus.BAD_REQUEST,"Validation failed",m);
  }
 @ExceptionHandler(Exception.class) ResponseEntity<ErrorResponse> other(Exception e){
    return response(HttpStatus.INTERNAL_SERVER_ERROR,"An unexpected error occurred",Map.of());
  }
 private ResponseEntity<ErrorResponse> response(HttpStatus s,String m,Map<String,String> v){
    return ResponseEntity.status(s).body(new ErrorResponse(Instant.now(),s.value(),s.getReasonPhrase(),m,v));
  }
}
