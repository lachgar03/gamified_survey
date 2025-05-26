package org.example.gamified_survey_app.gamification.model;

import java.time.LocalDateTime;
import org.example.gamified_survey_app.auth.model.AppUser;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    private Integer currentValue = 0;

    private boolean completed = false;
    private LocalDateTime completedAt;

    private boolean rewardClaimed = false;
    private LocalDateTime rewardClaimedAt;

    private LocalDateTime startedAt;
}
