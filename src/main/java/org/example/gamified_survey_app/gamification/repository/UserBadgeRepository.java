package org.example.gamified_survey_app.gamification.repository;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.gamification.model.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    List<UserBadge> findByUser(AppUser user);

    List<UserBadge> findByUserAndCompletedTrue(AppUser user);

    List<UserBadge> findByUserAndCompletedFalse(AppUser user);

    List<UserBadge> findByUserAndCompletedTrueAndRewardClaimedFalse(AppUser user);

    Optional<UserBadge> findByUserAndBadgeId(AppUser user, Long badgeId);
}
