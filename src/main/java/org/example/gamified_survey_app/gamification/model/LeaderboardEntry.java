package org.example.gamified_survey_app.gamification.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.gamification.constant.LeaderboardPeriod;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;
    
    private Integer xpEarned;
    
    @Enumerated(EnumType.STRING)
    private LeaderboardPeriod period;
    
    // The start time of this leaderboard period
    private LocalDateTime periodStartDate;
    
    // The end time of this leaderboard period
    private LocalDateTime periodEndDate;
    
    // Timestamp when this leaderboard entry was created or updated
    private LocalDateTime updatedAt;
} 