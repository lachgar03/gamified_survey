package org.example.gamified_survey_app.gamification.service.impl;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.core.exception.ResourceNotFoundException;
import org.example.gamified_survey_app.gamification.model.Badge;
import org.example.gamified_survey_app.gamification.model.UserBadge;
import org.example.gamified_survey_app.gamification.repository.BadgeRepository;
import org.example.gamified_survey_app.gamification.repository.UserBadgeRepository;
import org.example.gamified_survey_app.gamification.service.BadgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BadgeServiceImpl implements BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;

    @Autowired
    public BadgeServiceImpl(BadgeRepository badgeRepository, UserBadgeRepository userBadgeRepository) {
        this.badgeRepository = badgeRepository;
        this.userBadgeRepository = userBadgeRepository;
    }

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
                .orElseThrow(() -> new ResourceNotFoundException("Badge not found with id: " + id));
        
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
    @Transactional
    public UserBadge awardBadge(AppUser user, Long badgeId) {
        // Check if user already has this badge
        if (hasUserEarnedBadge(user, badgeId)) {
            throw new IllegalStateException("User already has this badge");
        }
        
        Badge badge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new ResourceNotFoundException("Badge not found with id: " + badgeId));
        
        UserBadge userBadge = new UserBadge();
        userBadge.setUser(user);
        userBadge.setBadge(badge);
        userBadge.setEarnedAt(LocalDateTime.now());
        
        return userBadgeRepository.save(userBadge);
    }

    @Override
    public boolean hasUserEarnedBadge(AppUser user, Long badgeId) {
        Badge badge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new ResourceNotFoundException("Badge not found with id: " + badgeId));
        
        return userBadgeRepository.existsByUserAndBadge(user, badge);
    }
} 