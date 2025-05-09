package org.example.gamified_survey_app.auth.controller;

import lombok.RequiredArgsConstructor;
import org.example.gamified_survey_app.auth.dto.PasswordResetDto;
import org.example.gamified_survey_app.auth.dto.PasswordResetRequestDto;
import org.example.gamified_survey_app.auth.service.AuthService;
import org.example.gamified_survey_app.core.exception.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/password")
@RequiredArgsConstructor
public class PasswordResetController {
    
    private final AuthService authService;
    
    @PostMapping("/reset-request")
    public ResponseEntity<?> requestPasswordReset(@RequestBody PasswordResetRequestDto requestDto) {
        try {
            authService.createPasswordResetTokenForUser(requestDto.getEmail());
            return ResponseEntity.ok().body("Password reset email sent");
        } catch (CustomException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestParam("token") String token) {
        try {
            boolean valid = authService.validatePasswordResetToken(token);
            return ResponseEntity.ok().body("Token is valid");
        } catch (CustomException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetDto resetDto) {
        try {
            authService.resetPassword(resetDto);
            return ResponseEntity.ok().body("Password has been reset successfully");
        } catch (CustomException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 