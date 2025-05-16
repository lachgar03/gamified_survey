package org.example.gamified_survey_app.user.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProgressionRequestDto {
    private LocalDate startDate;
    private LocalDate endDate;
}
