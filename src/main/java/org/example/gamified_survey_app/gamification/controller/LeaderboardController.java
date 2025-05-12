package org.example.gamified_survey_app.gamification.controller;

import lombok.RequiredArgsConstructor;
import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.gamification.constant.LeaderboardPeriod;
import org.example.gamified_survey_app.gamification.dto.LeaderboardEntryDTO;
import org.example.gamified_survey_app.gamification.dto.LeaderboardResponseDTO;
import org.example.gamified_survey_app.gamification.dto.UserRankDTO;
import org.example.gamified_survey_app.gamification.model.LeaderboardEntry;
import org.example.gamified_survey_app.gamification.service.LeaderboardService;
import org.example.gamified_survey_app.user.service.UserProfileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;
    private final UserProfileService userProfileService;

    @GetMapping("/{period}")
    public ResponseEntity<LeaderboardResponseDTO> getLeaderboard(
            @PathVariable String period,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal AppUser currentUser) {

        LeaderboardPeriod leaderboardPeriod = LeaderboardPeriod.valueOf(period.toUpperCase());
        
        // Get the current user's rank
        Long userRank = leaderboardService.getUserRank(currentUser, leaderboardPeriod);
        LeaderboardEntry userEntry = leaderboardService.getUserLeaderboardEntry(currentUser, leaderboardPeriod);
        
        // Create the user rank DTO
        UserRankDTO userRankDTO = new UserRankDTO(
                userRank,
                userEntry != null ? userEntry.getXpEarned() : 0,
                userProfileService.getUserDisplayName(currentUser)
        );
        
        // Get the paginated leaderboard
        Page<LeaderboardEntry> leaderboardPage = leaderboardService.getLeaderboard(
                leaderboardPeriod, 
                PageRequest.of(page, size)
        );
        
        // Convert to DTOs
        List<LeaderboardEntryDTO> leaderboardEntries = leaderboardPage.getContent().stream()
                .map(entry -> new LeaderboardEntryDTO(
                        leaderboardPage.getNumber() * leaderboardPage.getSize() + leaderboardPage.getContent().indexOf(entry) + 1,
                        entry.getXpEarned(),
                        userProfileService.getUserDisplayName(entry.getUser()),
                        entry.getUser().getId().equals(currentUser.getId())
                ))
                .collect(Collectors.toList());
        
        // Create the response
        LeaderboardResponseDTO response = new LeaderboardResponseDTO(
                leaderboardEntries,
                userRankDTO,
                leaderboardPage.getTotalElements(),
                leaderboardPage.getTotalPages(),
                leaderboardPage.getNumber(),
                leaderboardPeriod
        );
        
        return ResponseEntity.ok(response);
    }
} 