package org.example.gamified_survey_app.user.dto;

import lombok.Data;

@Data

public class SurveySummaryDTO {
    private Long surveyId;
    private String title;
    private int responseCount;
    private int totalXpAwarded;
    private int suspiciousResponses;
    private double averageTimeSpentSeconds;
}

