package org.example.gamified_survey_app.gamification.controller;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.core.exception.ResourceNotFoundException;
import org.example.gamified_survey_app.gamification.dto.BadgeDTO;
import org.example.gamified_survey_app.gamification.dto.UserBadgeDTO;
import org.example.gamified_survey_app.gamification.model.Badge;
import org.example.gamified_survey_app.gamification.model.UserBadge;
import org.example.gamified_survey_app.gamification.service.BadgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/badges")
public class BadgeController {

    private final BadgeService badgeService;

    @Autowired
    public BadgeController(BadgeService badgeService) {
        this.badgeService = badgeService;
    }

    @GetMapping
    public ResponseEntity<List<BadgeDTO>> getAllBadges() {
        List<BadgeDTO> badges = badgeService.getAllBadges().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(badges);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BadgeDTO> getBadgeById(@PathVariable Long id) {
        return badgeService.getBadgeById(id)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Badge not found with id: " + id));
    }

    @PostMapping
    public ResponseEntity<BadgeDTO> createBadge(@RequestBody BadgeDTO badgeDTO) {
        Badge badge = new Badge();
        updateBadgeFromDTO(badge, badgeDTO);
        Badge savedBadge = badgeService.createBadge(badge);
        return ResponseEntity.ok(convertToDTO(savedBadge));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BadgeDTO> updateBadge(@PathVariable Long id, @RequestBody BadgeDTO badgeDTO) {
        Badge badge = badgeService.getBadgeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Badge not found with id: " + id));

        updateBadgeFromDTO(badge, badgeDTO);
        Badge updatedBadge = badgeService.updateBadge(id, badge);
        return ResponseEntity.ok(convertToDTO(updatedBadge));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBadge(@PathVariable Long id) {
        badgeService.deleteBadge(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user")
    public ResponseEntity<List<UserBadgeDTO>> getUserBadges(@AuthenticationPrincipal AppUser user) {
        List<UserBadgeDTO> badges = badgeService.getUserBadges(user).stream()
                .map(this::convertToUserBadgeDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(badges);
    }

    @PostMapping("/{badgeId}/award")
    public ResponseEntity<UserBadgeDTO> awardBadge(
            @AuthenticationPrincipal AppUser user,
            @PathVariable Long badgeId) {

        UserBadge userBadge = badgeService.awardBadge(user, badgeId);
        return ResponseEntity.ok(convertToUserBadgeDTO(userBadge));
    }

    @PostMapping("/claim/{userBadgeId}")
    public ResponseEntity<UserBadgeDTO> claimReward(
            @AuthenticationPrincipal AppUser user,
            @PathVariable Long userBadgeId) {

        UserBadge userBadge = badgeService.claimBadgeReward(user, userBadgeId);
        return ResponseEntity.ok(convertToUserBadgeDTO(userBadge));
    }

    @GetMapping("/{badgeId}/user-has")
    public ResponseEntity<Boolean> hasUserEarnedBadge(
            @AuthenticationPrincipal AppUser user,
            @PathVariable Long badgeId) {

        boolean hasEarned = badgeService.hasUserEarnedBadge(user, badgeId);
        return ResponseEntity.ok(hasEarned);
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

    private void updateBadgeFromDTO(Badge badge, BadgeDTO dto) {
        badge.setName(dto.getName());
        badge.setDescription(dto.getDescription());
        badge.setImageUrl(dto.getImageUrl());
        badge.setAchievementCondition(dto.getAchievementCondition());
        badge.setActionType(dto.getActionType());
        badge.setTargetValue(dto.getTargetValue());
        badge.setPeriod(dto.getPeriod()); // ✅ Added
    }

    private UserBadgeDTO convertToUserBadgeDTO(UserBadge ub) {
        BadgeDTO badgeDTO = convertToDTO(ub.getBadge());
        badgeDTO.setCurrentValue(ub.getCurrentValue());
        badgeDTO.setCompleted(ub.isCompleted());
        badgeDTO.setCompletedAt(ub.getCompletedAt());
        badgeDTO.setRewardClaimed(ub.isRewardClaimed());
        badgeDTO.setRewardClaimedAt(ub.getRewardClaimedAt());

        return new UserBadgeDTO(
                ub.getId(),
                ub.getUser().getId(),
                ub.getUser().getEmail(),
                badgeDTO,
                ub.getCurrentValue(),
                ub.isCompleted(),
                ub.getCompletedAt(),
                ub.isRewardClaimed(),
                ub.getRewardClaimedAt(),
                ub.getStartedAt()
        );
    }
}
