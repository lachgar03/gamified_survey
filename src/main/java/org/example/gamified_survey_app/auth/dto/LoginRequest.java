package org.example.gamified_survey_app.auth.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}

