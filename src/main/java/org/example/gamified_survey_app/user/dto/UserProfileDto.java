package org.example.gamified_survey_app.user.dto;


import lombok.Data;

@Data
public class UserProfileDto {
    private String FirstName;
    private String LastName;
    private Integer age;
    private String phoneNumber;
    private String profession;
    private String region;
}
