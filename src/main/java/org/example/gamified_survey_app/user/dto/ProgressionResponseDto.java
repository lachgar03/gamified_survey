package org.example.gamified_survey_app.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProgressionResponseDto {

    private UserStats user;
    private List<ProgressionPoint> progressionData;
    private List<MilestoneDto> milestones;

    @Data
    @AllArgsConstructor
    public static class UserStats {
        private int currentXp;
        private int currentLevel;
    }

    @Data
    @AllArgsConstructor
    public static class ProgressionPoint {
        private String date;
        private int xpEarned;
        private int surveysCompleted;
    }

    @Data
    @AllArgsConstructor
    public static class MilestoneDto {
        private String type;
        private String description;
        private String date;
    }
}
