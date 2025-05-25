package org.example.gamified_survey_app.gamification.repository;

import org.example.gamified_survey_app.gamification.model.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    Optional<Challenge> findByName(String name);
} 