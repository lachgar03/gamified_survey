package org.example.gamified_survey_app.core.exception;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        log.error("Erreur métier: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse("BUSINESS_ERROR", ex.getMessage()), 
                HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Ressource non trouvée: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse("NOT_FOUND", ex.getMessage()), 
                HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("Accès refusé: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse("ACCESS_DENIED", "Vous n'avez pas les droits nécessaires"), 
                HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        
        log.error("Erreur de validation: {}", errors);
        return new ResponseEntity<>(new ErrorResponse("VALIDATION_ERROR", "Erreur de validation", errors), 
                HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        log.error("Erreur non gérée", ex);
        return new ResponseEntity<>(new ErrorResponse("INTERNAL_SERVER_ERROR", "Une erreur est survenue"), 
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class ErrorResponse {
    private String code;
    private String message;
    private List<String> details;
    
    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.details = Collections.emptyList();
    }
}
