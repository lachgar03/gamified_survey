package org.example.gamified_survey_app.auth.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String Lastname;
    private String Firstname;

}

