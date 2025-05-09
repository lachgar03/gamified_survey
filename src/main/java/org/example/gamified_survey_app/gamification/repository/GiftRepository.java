package org.example.gamified_survey_app.gamification.repository;

import org.example.gamified_survey_app.gamification.model.Gift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftRepository extends JpaRepository<Gift, Long> {
    List<Gift> findByActiveTrue();
    
    List<Gift> findByActiveTrueAndAvailableQuantityGreaterThan(Integer quantity);
    
    List<Gift> findByPointsCostLessThanEqual(Integer points);
} 