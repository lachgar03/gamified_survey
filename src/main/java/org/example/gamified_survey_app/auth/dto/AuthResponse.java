package org.example.gamified_survey_app.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.gamified_survey_app.auth.model.AppUser;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private AppUser utilisateur;
}

