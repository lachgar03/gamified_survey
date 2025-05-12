package org.example.gamified_survey_app.gamification.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.gamification.model.Challenge;
import org.example.gamified_survey_app.gamification.model.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long> {
    List<UserChallenge> findByUser(AppUser user);
    
    List<UserChallenge> findByUserAndCompletedTrue(AppUser user);
    
    List<UserChallenge> findByUserAndCompletedFalse(AppUser user);
    
    Optional<UserChallenge> findByUserAndChallenge(AppUser user, Challenge challenge);
    
    @Query("SELECT uc FROM UserChallenge uc WHERE uc.user = :user AND uc.completed = true " +
            "AND uc.rewardClaimed = false")
    List<UserChallenge> findCompletedWithUnclaimedRewards(@Param("user") AppUser user);
    
    @Query("SELECT COUNT(uc) FROM UserChallenge uc WHERE uc.user = :user AND uc.completed = true " +
            "AND uc.completedAt >= :since")
    Long countCompletedChallengesSince(@Param("user") AppUser user, @Param("since") LocalDateTime since);
} 