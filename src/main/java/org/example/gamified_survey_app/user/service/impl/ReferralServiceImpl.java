package org.example.gamified_survey_app.user.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.core.exception.CustomException;
import org.example.gamified_survey_app.gamification.service.UserXpService;
import org.example.gamified_survey_app.user.model.Referral;
import org.example.gamified_survey_app.user.repository.ReferralRepository;
import org.example.gamified_survey_app.user.service.ReferralService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReferralServiceImpl implements ReferralService {
    
    private static final Logger log = LoggerFactory.getLogger(ReferralServiceImpl.class);
    private final ReferralRepository referralRepository;

    private static final int REFERRAL_BONUS_XP = 50;
    private final UserXpService userXpService;

    @Override
    @Transactional
    public String createReferralCode(AppUser referrer, String refereeEmail) {
        log.info("Creating referral code for referrer: {} to referee: {}", 
                referrer.getEmail(), refereeEmail);
        
        // Generate a unique referral code
        String referralCode = UUID.randomUUID().toString().substring(0, 8);
        
        // Create a pending referral
        Referral referral = new Referral();
        referral.setReferrer(referrer);
        referral.setReferralCode(referralCode);
        referral.setCreatedAt(LocalDateTime.now());
        
        referralRepository.save(referral);
        
        return referralCode;
    }

    @Override
    @Transactional
    public Referral processReferral(AppUser newUser, String referralCode) {
        log.info("Processing referral code: {} for new user: {}", 
                referralCode, newUser.getEmail());
        
        if (referralCode == null || referralCode.isEmpty()) {
            log.warn("Empty referral code provided");
            return null;
        }
        
        List<Referral> referrals = referralRepository.findByReferralCode(referralCode);
        
        if (referrals.isEmpty()) {
            log.warn("Invalid referral code: {}", referralCode);
            return null;
        }
        
        Referral referral = referrals.getFirst();
        
        // Ensure the referrer is not the same as the referee
        if (referral.getReferrer().getEmail().equals(newUser.getEmail())) {
            log.warn("User tried to refer themselves: {}", newUser.getEmail());
            throw new CustomException("You cannot refer yourself");
        }
        
        // Update the referral with the new user's information
        referral.setReferee(newUser);
        
        return referralRepository.save(referral);
    }

    @Override
    public List<Referral> getReferralsByUser(AppUser referrer) {
        log.debug("Getting all referrals for user: {}", referrer.getEmail());
        return referralRepository.findByReferrer(referrer);
    }

    @Override
    public Referral getReferralForUser(AppUser referee) {
        log.debug("Getting referral for referee: {}", referee.getEmail());
        return referralRepository.findByReferee(referee).orElse(null);
    }

    @Override
    public Long getReferralCount(AppUser referrer) {
        log.debug("Getting referral count for user: {}", referrer.getEmail());
        return referralRepository.countReferralsByReferrer(referrer);
    }

    @Override
    @Transactional
    public void awardReferralBonus(AppUser referee, int xpAmount) {
        log.info("Awarding referral bonus for action by user: {}", referee.getEmail());
        
        // Find the referral for this user
        Referral referral = referralRepository.findByReferee(referee).orElse(null);

        if (referral == null || referral.isBonusAwarded()) {
            log.debug("No applicable referral found or bonus already awarded");
            return;
        }

        AppUser user = referral.getReferrer();

        // Award XP to the referrer
        int bonusXp = (int)(xpAmount * 0.1); // 10% of the XP earned by referee
        if (bonusXp < REFERRAL_BONUS_XP) {
            bonusXp = REFERRAL_BONUS_XP; // Minimum bonus
        }
        

        
        // Mark the bonus as awarded
        referral.setBonusAwarded(true);
        referral.setBonusAwardedAt(LocalDateTime.now());
        referral.setXpAwarded(bonusXp);
        
        referralRepository.save(referral);
         userXpService.updateUserXp(user , bonusXp);

        log.info("Awarded {} bonus XP to referrer: {}", bonusXp, user.getEmail());
    }
    @Override
    public List<Referral> getReferralByCode(String referralCode) {
        return referralRepository.findByReferralCode(referralCode);
    }
} 