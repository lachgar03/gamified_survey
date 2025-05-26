package org.example.gamified_survey_app.gamification.repository;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.gamification.model.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long> {
    List<UserChallenge> findByUser(AppUser user);

    List<UserChallenge> findByUserAndCompletedTrue(AppUser user);

    List<UserChallenge> findByUserAndCompletedFalse(AppUser user);

    List<UserChallenge> findByUserAndCompletedTrueAndRewardClaimedFalse(AppUser user);
    Optional<UserChallenge> findByUserAndChallengeId(AppUser user, Long challengeId);

    @Query("SELECT uc FROM UserChallenge uc WHERE uc.user = :user AND uc.challenge.id = :challengeId")
    Optional<Boolean> isChallengeCompleted(@Param("user") AppUser user, @Param("challengeId") Long challengeId);

    @Query("SELECT uc FROM UserChallenge uc JOIN FETCH uc.challenge WHERE uc.user = :user")
    List<UserChallenge> findByUserWithChallenge(@Param("user") AppUser user);


}
