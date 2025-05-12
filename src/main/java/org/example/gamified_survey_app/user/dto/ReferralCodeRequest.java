package org.example.gamified_survey_app.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReferralCodeRequest {
    
    @NotBlank(message = "Referee email is required")
    @Email(message = "Invalid email format")
    private String refereeEmail;
} 