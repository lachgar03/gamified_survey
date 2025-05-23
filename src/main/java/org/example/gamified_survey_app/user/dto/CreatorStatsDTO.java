package org.example.gamified_survey_app.user.dto;
import lombok.Data;
import java.util.List;
@Data
public class CreatorStatsDTO {
    private int totalSurveysCreated;
    private int totalResponses;
    private double averageResponsesPerSurvey;

    private int totalXpAwarded;
    private double averageTimeSpentSeconds;
    private int suspiciousResponsesCount;

    private List<SurveySummaryDTO> surveySummaries;
}




