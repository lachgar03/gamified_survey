package org.example.gamified_survey_app.gamification.repository;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.gamification.model.GiftRedemption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftRedemptionRepository extends JpaRepository<GiftRedemption, Long> {
    List<GiftRedemption> findByUser(AppUser user);
    
    List<GiftRedemption> findByStatus(GiftRedemption.RedemptionStatus status);
    
    List<GiftRedemption> findByUserAndStatus(AppUser user, GiftRedemption.RedemptionStatus status);
} 