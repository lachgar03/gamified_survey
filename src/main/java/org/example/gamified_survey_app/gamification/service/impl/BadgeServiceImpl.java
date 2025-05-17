package org.example.gamified_survey_app.gamification.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.gamification.constant.ChallengeType;
import org.example.gamified_survey_app.gamification.model.Badge;
import org.example.gamified_survey_app.gamification.model.UserBadge;
import org.example.gamified_survey_app.gamification.repository.BadgeRepository;
import org.example.gamified_survey_app.gamification.repository.UserBadgeRepository;
import org.example.gamified_survey_app.gamification.service.BadgeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BadgeServiceImpl implements BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;

    @Override
    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    @Override
    public Optional<Badge> getBadgeById(Long id) {
        return badgeRepository.findById(id);
    }

    @Override
    public Badge createBadge(Badge badge) {
        return badgeRepository.save(badge);
    }

    @Override
    public Badge updateBadge(Long id, Badge badgeDetails) {
        Badge badge = badgeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Badge not found"));
        badge.setName(badgeDetails.getName());
        badge.setDescription(badgeDetails.getDescription());
        badge.setImageUrl(badgeDetails.getImageUrl());
        badge.setAchievementCondition(badgeDetails.getAchievementCondition());
        return badgeRepository.save(badge);
    }

    @Override
    public void deleteBadge(Long id) {
        badgeRepository.deleteById(id);
    }

    @Override
    public List<UserBadge> getUserBadges(AppUser user) {
        return userBadgeRepository.findByUser(user);
    }

    @Override
    public List<UserBadge> getCompletedBadges(AppUser user) {
        return userBadgeRepository.findByUserAndCompletedTrue(user);
    }

    @Override
    public List<UserBadge> getInProgressBadges(AppUser user) {
        return userBadgeRepository.findByUserAndCompletedFalse(user);
    }

    @Override
    public List<UserBadge> getUnclaimedBadges(AppUser user) {
        return userBadgeRepository.findByUserAndCompletedTrueAndRewardClaimedFalse(user);
    }

    @Override
    public UserBadge awardBadge(AppUser user, Long badgeId) {
        Optional<UserBadge> existing = userBadgeRepository.findByUserAndBadgeId(user, badgeId);
        if (existing.isPresent()) {
            return existing.get();
        }

        Badge badge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new RuntimeException("Badge not found"));

        UserBadge userBadge = new UserBadge();
        userBadge.setUser(user);
        userBadge.setBadge(badge);
        userBadge.setStartedAt(LocalDateTime.now());
        userBadge.setCurrentValue(0);
        userBadge.setCompleted(false);
        userBadge.setRewardClaimed(false);

        return userBadgeRepository.save(userBadge);
    }

    @Override
    public boolean hasUserEarnedBadge(AppUser user, Long badgeId) {
        return userBadgeRepository.findByUserAndBadgeId(user, badgeId)
                .map(UserBadge::isCompleted)
                .orElse(false);
    }

    @Override
    public UserBadge claimBadgeReward(AppUser user, Long userBadgeId) {
        UserBadge userBadge = userBadgeRepository.findById(userBadgeId)
                .orElseThrow(() -> new RuntimeException("UserBadge not found"));

        if (!userBadge.getUser().getId().equals(user.getId()) || !userBadge.isCompleted()) {
            throw new RuntimeException("Cannot claim reward");
        }

        userBadge.setRewardClaimed(true);
        userBadge.setRewardClaimedAt(LocalDateTime.now());

        return userBadgeRepository.save(userBadge);
    }

    @Override
    public void updateBadgeProgress(AppUser user, ChallengeType type, int value, String extraData) {
        List<UserBadge> userBadges = userBadgeRepository.findByUser(user);

        for (UserBadge userBadge : userBadges) {
            if (userBadge.isCompleted()) continue;

            Badge badge = userBadge.getBadge();
            if (!badge.getAchievementCondition().contains(type.name())) continue;

            int newValue = userBadge.getCurrentValue() + value;
            userBadge.setCurrentValue(newValue);

            // For now assume each badge completion threshold is 100
            if (newValue >= 100) {
                userBadge.setCompleted(true);
                userBadge.setCompletedAt(LocalDateTime.now());
            }

            userBadgeRepository.save(userBadge);
        }
    }

    @Override
    public int assignBadgesToUser(AppUser user) {
        List<Badge> all = badgeRepository.findAll();
        int count = 0;

        for (Badge badge : all) {
            if (userBadgeRepository.findByUserAndBadgeId(user, badge.getId()).isEmpty()) {
                UserBadge userBadge = new UserBadge();
                userBadge.setUser(user);
                userBadge.setBadge(badge);
                userBadge.setStartedAt(LocalDateTime.now());
                userBadge.setCurrentValue(0);
                userBadge.setCompleted(false);
                userBadge.setRewardClaimed(false);
                userBadgeRepository.save(userBadge);
                count++;
            }
        }
        return count;
    }
}
