package org.example.gamified_survey_app.gamification.dto;

import java.util.List;

import org.example.gamified_survey_app.gamification.constant.LeaderboardPeriod;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardResponseDTO {
    private List<LeaderboardEntryDTO> leaderboard;
    private UserRankDTO currentUserRank;
    private long totalUsers;
    private int totalPages;
    private int currentPage;
    private LeaderboardPeriod period;
} 