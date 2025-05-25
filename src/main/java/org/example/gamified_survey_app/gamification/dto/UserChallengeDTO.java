package org.example.gamified_survey_app.gamification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserChallengeDTO {
    private Long id;
    private Long userId;
    private String userName;
    private ChallengeDTO badge;
    private Integer currentValue;
    private boolean completed;
    private LocalDateTime completedAt;
    private boolean rewardClaimed;
    private LocalDateTime rewardClaimedAt;
    private LocalDateTime startedAt;
}
