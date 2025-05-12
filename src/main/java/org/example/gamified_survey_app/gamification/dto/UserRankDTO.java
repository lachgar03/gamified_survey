package org.example.gamified_survey_app.gamification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRankDTO {
    private long rank;
    private int xpEarned;
    private String userName;
} 