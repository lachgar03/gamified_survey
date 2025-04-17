package org.example.gamified_survey_app.user.dto;


import lombok.Data;

@Data
public class UserProfileDto {
    private String lastName;
    private String firstName;
    private String profession;
    private String region;
    private Integer age;
    private String phoneNumber;
}
