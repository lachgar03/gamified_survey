package org.example.gamified_survey_app.gamification.model;

import java.time.LocalDateTime;

import org.example.gamified_survey_app.gamification.constant.ChallengePeriod;
import org.example.gamified_survey_app.gamification.constant.ChallengeType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String description;
    
    @Enumerated(EnumType.STRING)
    private ChallengeType type;
    
    @Enumerated(EnumType.STRING)
    private ChallengePeriod period;
    
    // The specific goal value (e.g., number of surveys to complete)
    private Integer targetValue;
    
    // Extra data needed for the challenge (e.g., category ID for category-specific challenges)
    private String extraData;
    
    // XP reward when completed
    private Integer xpReward;
    
    // Badge reward when completed (optional)
    @ManyToOne
    @JoinColumn(name = "badge_id")
    private Badge badgeReward;
    
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    // Is this challenge currently active
    private boolean active = true;
} 