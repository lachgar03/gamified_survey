package org.example.gamified_survey_app.gamification.model;

import java.time.LocalDateTime;

import org.example.gamified_survey_app.auth.model.AppUser;

import jakarta.persistence.Entity;
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
public class UserChallenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;
    
    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;
    
    // Current progress towards the target
    private Integer currentValue = 0;
    
    // Whether the challenge has been completed
    private boolean completed = false;
    
    // When the challenge was completed
    private LocalDateTime completedAt;
    
    // When the user started this challenge
    private LocalDateTime startedAt;
    
    // Whether the reward has been claimed
    private boolean rewardClaimed = false;
    
    // When the reward was claimed
    private LocalDateTime rewardClaimedAt;
} 