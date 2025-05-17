package org.example.gamified_survey_app.gamification.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.gamification.constant.LeaderboardPeriod;
import org.example.gamified_survey_app.gamification.model.LeaderboardEntry;
import org.example.gamified_survey_app.gamification.repository.LeaderboardRepository;
import org.example.gamified_survey_app.gamification.service.LeaderboardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {

    private final LeaderboardRepository leaderboardRepository;

    @Override
    @Transactional
    public void updateUserXp(AppUser user, int xpEarned) {
        // Update each type of leaderboard
        updateLeaderboardEntry(user, xpEarned, LeaderboardPeriod.WEEKLY);
        updateLeaderboardEntry(user, xpEarned, LeaderboardPeriod.MONTHLY);
        updateLeaderboardEntry(user, xpEarned, LeaderboardPeriod.GLOBAL);
    }

    @Override
    public Page<LeaderboardEntry> getLeaderboard(LeaderboardPeriod period, Pageable pageable) {
        LocalDateTime currentPeriodStart = getCurrentPeriodStartDate(period);
        return leaderboardRepository.findByPeriodAndPeriodStartDateOrderByXpEarnedDesc(
                period, currentPeriodStart, pageable);
    }

    @Override
    public Long getUserRank(AppUser user, LeaderboardPeriod period) {
        LocalDateTime currentPeriodStart = getCurrentPeriodStartDate(period);
        Long rank = leaderboardRepository.getUserRank(user, period, currentPeriodStart);
        // Add 1 to make it 1-based ranking (SQL COUNT is 0-based)
        return rank + 1;
    }

    @Override
    public LeaderboardEntry getUserLeaderboardEntry(AppUser user, LeaderboardPeriod period) {
        LocalDateTime currentPeriodStart = getCurrentPeriodStartDate(period);
        Optional<LeaderboardEntry> entry = leaderboardRepository.findByUserAndPeriodAndPeriodStartDate(
                user, period, currentPeriodStart);
        return entry.orElse(null);
    }

    @Override
    public LocalDateTime getCurrentPeriodStartDate(LeaderboardPeriod period) {
        LocalDateTime now = LocalDateTime.now();
        
        switch (period) {
            case WEEKLY:
                // Start of current week (Monday)
                return now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                        .with(LocalTime.MIN);
                
            case MONTHLY:
                // Start of current month
                return now.withDayOfMonth(1).with(LocalTime.MIN);
                
            case GLOBAL:
                // For global, we use a fixed date (application start date)
                return LocalDateTime.of(2023, 1, 1, 0, 0);
                
            default:
                throw new IllegalArgumentException("Unsupported leaderboard period: " + period);
        }
    }

    @Override
    public LocalDateTime getCurrentPeriodEndDate(LeaderboardPeriod period) {
        LocalDateTime now = LocalDateTime.now();
        
        switch (period) {
            case WEEKLY:
                // End of current week (Sunday)
                return now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                        .with(LocalTime.MAX);
                
            case MONTHLY:
                // End of current month
                return now.with(TemporalAdjusters.lastDayOfMonth())
                        .with(LocalTime.MAX);
                
            case GLOBAL:
                // Global leaderboard has no end date
                return LocalDateTime.of(5000, 1, 1, 0, 0);
                
            default:
                throw new IllegalArgumentException("Unsupported leaderboard period: " + period);
        }
    }
    
    /**
     * Helper method to update a single leaderboard entry for a user
     */
    private void updateLeaderboardEntry(AppUser user, int xpEarned, LeaderboardPeriod period) {
        LocalDateTime periodStart = getCurrentPeriodStartDate(period);
        LocalDateTime periodEnd = getCurrentPeriodEndDate(period);
        
        // Find existing entry or create a new one
        LeaderboardEntry entry = leaderboardRepository
                .findByUserAndPeriodAndPeriodStartDate(user, period, periodStart)
                .orElseGet(() -> {
                    LeaderboardEntry newEntry = new LeaderboardEntry();
                    newEntry.setUser(user);
                    newEntry.setPeriod(period);
                    newEntry.setPeriodStartDate(periodStart);
                    newEntry.setPeriodEndDate(periodEnd);
                    newEntry.setXpEarned(0);
                    return newEntry;
                });
        
        // Update the entry
        entry.setXpEarned(entry.getXpEarned() + xpEarned);
        entry.setUpdatedAt(LocalDateTime.now());
        
        // Save the entry
        leaderboardRepository.save(entry);
    }
} 