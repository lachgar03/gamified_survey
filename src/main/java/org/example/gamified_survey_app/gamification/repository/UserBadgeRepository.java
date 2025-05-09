package org.example.gamified_survey_app.gamification.repository;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.gamification.model.Badge;
import org.example.gamified_survey_app.gamification.model.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    List<UserBadge> findByUser(AppUser user);
    
    Optional<UserBadge> findByUserAndBadge(AppUser user, Badge badge);
    
    boolean existsByUserAndBadge(AppUser user, Badge badge);
} 