package org.example.gamified_survey_app.gamification.controller;

import lombok.RequiredArgsConstructor;
import org.example.gamified_survey_app.gamification.dto.BadgeDTO;
import org.example.gamified_survey_app.gamification.model.Badge;
import org.example.gamified_survey_app.gamification.service.BadgeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final BadgeService badgeService;

    @GetMapping
    public ResponseEntity<List<BadgeDTO>> getAllChallenges() {
        List<BadgeDTO> challenges = badgeService.getAllBadges().stream()
                .filter(b -> b.getActionType() != null && b.getTargetValue() != null)
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(challenges);
    }

    private BadgeDTO convertToDTO(Badge badge) {
        return new BadgeDTO(
                badge.getId(),
                badge.getName(),
                badge.getDescription(),
                badge.getImageUrl(),
                badge.getAchievementCondition(),
                badge.getTargetValue(),
                badge.getActionType(),
                badge.getPeriod(), // ✅ Added
                null, false, null,
                false, null
        );
    }
}
