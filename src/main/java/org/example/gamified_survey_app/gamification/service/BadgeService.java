package org.example.gamified_survey_app.gamification.service;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.gamification.constant.ChallengeType;
import org.example.gamified_survey_app.gamification.model.Badge;
import org.example.gamified_survey_app.gamification.model.UserBadge;

import java.util.List;
import java.util.Optional;

public interface BadgeService {

    // Badge Management
    List<Badge> getAllBadges();
    Optional<Badge> getBadgeById(Long id);
    Badge createBadge(Badge badge);
    Badge updateBadge(Long id, Badge badgeDetails);
    void deleteBadge(Long id);

    // User-Badge Relationships
    List<UserBadge> getUserBadges(AppUser user);
    List<UserBadge> getCompletedBadges(AppUser user);
    List<UserBadge> getInProgressBadges(AppUser user);
    List<UserBadge> getUnclaimedBadges(AppUser user);

    UserBadge awardBadge(AppUser user, Long badgeId);
    boolean hasUserEarnedBadge(AppUser user, Long badgeId);
    UserBadge claimBadgeReward(AppUser user, Long userBadgeId);

    // Progress Tracking
    void updateBadgeProgress(AppUser user, ChallengeType type, int value, String extraData);
    int assignBadgesToUser(AppUser user);
}
