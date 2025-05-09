package org.example.gamified_survey_app.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetDto {
    private String token;
    private String newPassword;
    private String confirmPassword;
} 