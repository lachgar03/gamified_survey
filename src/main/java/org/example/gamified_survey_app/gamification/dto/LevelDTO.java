package org.example.gamified_survey_app.gamification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LevelDTO {
    private Long id;
    private String name;
    private Integer number;
    private Integer pointsThreshold;
    private String description;
    private String badgeUrl;
} 