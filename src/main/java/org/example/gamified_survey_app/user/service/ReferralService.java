package org.example.gamified_survey_app.user.service;

import java.util.List;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.user.model.Referral;

public interface ReferralService {
    /**
     * Creates a new referral
     * 
     * @param referrer The user making the referral
     * @param refereeEmail The email of the user being referred
     * @return The created referral code
     */
    String createReferralCode(AppUser referrer, String refereeEmail);
    
    /**
     * Processes a referral when a new user registers
     * 
     * @param newUser The newly registered user
     * @param referralCode The referral code used
     * @return The created referral
     */
    Referral processReferral(AppUser newUser, String referralCode);
    
    /**
     * Gets all referrals made by a user
     * 
     * @param referrer The user who made the referrals
     * @return A list of referrals
     */
    List<Referral> getReferralsByUser(AppUser referrer);
    
    /**
     * Gets the referral for a user if they were referred
     * 
     * @param referee The user who was referred
     * @return The referral or null if they weren't referred
     */
    Referral getReferralForUser(AppUser referee);
    
    /**
     * Gets the number of referrals made by a user
     * 
     * @param referrer The user who made the referrals
     * @return The number of referrals
     */
    Long getReferralCount(AppUser referrer);

    List<Referral> getReferralByCode(String referralCode);
    
    /**
     * Awards XP bonus for referrals when a referred user completes certain actions
     * 
     * @param referee The user who was referred
     * @param xpAmount The amount of XP to award to the referrer
     */
    void awardReferralBonus(AppUser referee, int xpAmount);
} 