package org.example.gamified_survey_app.gamification.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.gamification.constant.LeaderboardPeriod;
import org.example.gamified_survey_app.gamification.model.LeaderboardEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaderboardRepository extends JpaRepository<LeaderboardEntry, Long> {
    
    // Find a user's entry for a specific period
    Optional<LeaderboardEntry> findByUserAndPeriodAndPeriodStartDate(
            AppUser user, 
            LeaderboardPeriod period,
            LocalDateTime periodStartDate);
    
    // Get top entries for a specific period, sorted by XP
    Page<LeaderboardEntry> findByPeriodAndPeriodStartDateOrderByXpEarnedDesc(
            LeaderboardPeriod period,
            LocalDateTime periodStartDate,
            Pageable pageable);
    
    // Get user ranking in a specific leaderboard period
    @Query("SELECT COUNT(l) FROM LeaderboardEntry l WHERE l.period = :period " +
           "AND l.periodStartDate = :startDate AND l.xpEarned > " +
           "(SELECT le.xpEarned FROM LeaderboardEntry le WHERE le.user = :user " +
           "AND le.period = :period AND le.periodStartDate = :startDate)")
    Long getUserRank(@Param("user") AppUser user, 
                    @Param("period") LeaderboardPeriod period,
                    @Param("startDate") LocalDateTime startDate);
} 