package org.example.gamified_survey_app.gamification.repository;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.gamification.model.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long> {
    List<UserChallenge> findByUser(AppUser user);

    List<UserChallenge> findByUserAndCompletedTrue(AppUser user);

    List<UserChallenge> findByUserAndCompletedFalse(AppUser user);

    List<UserChallenge> findByUserAndCompletedTrueAndRewardClaimedFalse(AppUser user);

    Optional<UserChallenge> findByUserAndChallengeId(AppUser user, Long badgeId);
}
