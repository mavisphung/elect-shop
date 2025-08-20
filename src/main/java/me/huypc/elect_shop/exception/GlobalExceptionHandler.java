package me.huypc.elect_shop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import me.huypc.elect_shop.generated.dto.ErrorResponse;
import me.huypc.elect_shop.generated.dto.ValidationErrorResponse;
import me.huypc.elect_shop.generated.dto.ValidationErrorResponseDetailsInner;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        log.error("NotFoundException occurred: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        log.error("Validation error occurred: {}", e.getMessage());
        
        List<ValidationErrorResponseDetailsInner> details = new ArrayList<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            ValidationErrorResponseDetailsInner detail = new ValidationErrorResponseDetailsInner();
            detail.setField(error.getField());
            detail.setMessage(error.getDefaultMessage());
            details.add(detail);
        });
        
        ValidationErrorResponse response = new ValidationErrorResponse("Validation failed", details);
        response.setTimestamp(OffsetDateTime.now());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Exception occurred: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(new ErrorResponse("Internal Server Error"));
    }
}
