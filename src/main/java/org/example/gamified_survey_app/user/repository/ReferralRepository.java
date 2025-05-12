package org.example.gamified_survey_app.user.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.user.model.Referral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, Long> {
    List<Referral> findByReferrer(AppUser referrer);
    
    Optional<Referral> findByReferee(AppUser referee);
    
    List<Referral> findByReferralCode(String referralCode);
    
    @Query("SELECT COUNT(r) FROM Referral r WHERE r.referrer = :referrer")
    Long countReferralsByReferrer(@Param("referrer") AppUser referrer);
    
    @Query("SELECT COUNT(r) FROM Referral r WHERE r.referrer = :referrer AND r.createdAt >= :since")
    Long countReferralsByReferrerSince(@Param("referrer") AppUser referrer, @Param("since") LocalDateTime since);
    
    List<Referral> findByReferrerAndBonusAwarded(AppUser referrer, boolean bonusAwarded);
} 