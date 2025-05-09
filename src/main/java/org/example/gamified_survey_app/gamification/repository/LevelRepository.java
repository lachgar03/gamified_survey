package org.example.gamified_survey_app.gamification.repository;

import org.example.gamified_survey_app.gamification.model.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LevelRepository extends JpaRepository<Level, Long> {
    Optional<Level> findByNumber(Integer number);
    
    Optional<Level> findFirstByPointsThresholdLessThanEqualOrderByPointsThresholdDesc(Integer points);
} 