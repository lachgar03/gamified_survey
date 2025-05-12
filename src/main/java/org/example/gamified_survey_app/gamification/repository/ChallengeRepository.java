package org.example.gamified_survey_app.gamification.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.example.gamified_survey_app.gamification.constant.ChallengePeriod;
import org.example.gamified_survey_app.gamification.model.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    List<Challenge> findByActiveTrue();
    
    List<Challenge> findByActiveTrueAndPeriod(ChallengePeriod period);
    
    @Query("SELECT c FROM Challenge c WHERE c.active = true AND " +
            "((c.startDate IS NULL OR c.startDate <= :now) AND " +
            "(c.endDate IS NULL OR c.endDate >= :now))")
    List<Challenge> findAvailableChallenges(@Param("now") LocalDateTime now);
    
    @Query("SELECT c FROM Challenge c WHERE c.active = true AND c.period = :period AND " +
            "((c.startDate IS NULL OR c.startDate <= :now) AND " +
            "(c.endDate IS NULL OR c.endDate >= :now))")
    List<Challenge> findAvailableChallengesByPeriod(@Param("period") ChallengePeriod period, 
                                                   @Param("now") LocalDateTime now);
} 