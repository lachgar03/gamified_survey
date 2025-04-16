package org.example.gamified_survey_app.user.dto;


import lombok.Data;

@Data
public class PasswordChangeDto {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
