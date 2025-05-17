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
public class UserBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;

    @ManyToOne
    @JoinColumn(name = "badge_id")
    private Badge badge;

    // Track progress toward badge (optional)
    private Integer currentValue = 0;

    private boolean completed = false;
    private LocalDateTime completedAt;

    private boolean rewardClaimed = false;
    private LocalDateTime rewardClaimedAt;

    private LocalDateTime startedAt;
}
