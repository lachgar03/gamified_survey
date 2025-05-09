package org.example.gamified_survey_app.gamification.service;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.gamification.model.Badge;
import org.example.gamified_survey_app.gamification.model.UserBadge;

import java.util.List;
import java.util.Optional;

public interface BadgeService {
    List<Badge> getAllBadges();
    
    Optional<Badge> getBadgeById(Long id);
    
    Badge createBadge(Badge badge);
    
    Badge updateBadge(Long id, Badge badgeDetails);
    
    void deleteBadge(Long id);
    
    List<UserBadge> getUserBadges(AppUser user);
    
    UserBadge awardBadge(AppUser user, Long badgeId);
    
    boolean hasUserEarnedBadge(AppUser user, Long badgeId);
} 