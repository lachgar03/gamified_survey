package org.example.gamified_survey_app.gamification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.gamified_survey_app.gamification.constant.ChallengePeriod;
import org.example.gamified_survey_app.gamification.constant.ChallengeType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeDTO {
    private Long id;
    private String name;
    private String description;
    private Integer XpValue;
    private String achievementCondition;

    private Integer targetValue;
    private ChallengeType actionType;
    private ChallengePeriod period; // ✅ Add this line

    private Integer currentValue;
    private boolean completed;
    private LocalDateTime completedAt;

    private boolean rewardClaimed;
    private LocalDateTime rewardClaimedAt;
}
