package org.example.gamified_survey_app.gamification.service;

import java.time.LocalDateTime;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.gamification.constant.LeaderboardPeriod;
import org.example.gamified_survey_app.gamification.model.LeaderboardEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LeaderboardService {
    
    /**
     * Updates a user's XP for the current leaderboard periods when they earn XP
     * 
     * @param user The user who earned XP
     * @param xpEarned The amount of XP earned
     */
    void updateUserXp(AppUser user, int xpEarned);
    
    /**
     * Gets the current leaderboard for the specified period
     * 
     * @param period The leaderboard period (WEEKLY, MONTHLY, GLOBAL)
     * @param pageable Pagination parameters
     * @return A page of leaderboard entries sorted by XP
     */
    Page<LeaderboardEntry> getLeaderboard(LeaderboardPeriod period, Pageable pageable);
    
    /**
     * Gets a user's position in the leaderboard for the specified period
     * 
     * @param user The user to check
     * @param period The leaderboard period
     * @return The user's position in the leaderboard (1-based)
     */
    Long getUserRank(AppUser user, LeaderboardPeriod period);
    
    /**
     * Gets a user's leaderboard entry for the specified period
     * 
     * @param user The user to check
     * @param period The leaderboard period
     * @return The user's leaderboard entry or null if they don't have one
     */
    LeaderboardEntry getUserLeaderboardEntry(AppUser user, LeaderboardPeriod period);
    
    /**
     * Gets the start date for the current period
     * 
     * @param period The leaderboard period
     * @return The start date for the current period
     */
    LocalDateTime getCurrentPeriodStartDate(LeaderboardPeriod period);
    
    /**
     * Gets the end date for the current period
     * 
     * @param period The leaderboard period
     * @return The end date for the current period
     */
    LocalDateTime getCurrentPeriodEndDate(LeaderboardPeriod period);
} 