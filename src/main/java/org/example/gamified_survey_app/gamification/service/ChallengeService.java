package org.example.gamified_survey_app.gamification.service;

import java.util.List;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.gamification.constant.ChallengePeriod;
import org.example.gamified_survey_app.gamification.constant.ChallengeType;
import org.example.gamified_survey_app.gamification.model.Challenge;
import org.example.gamified_survey_app.gamification.model.UserChallenge;

public interface ChallengeService {
    
    /**
     * Gets all active challenges
     * 
     * @return A list of active challenges
     */
    List<Challenge> getAllActiveChallenges();
    
    /**
     * Gets all active challenges for a specific period
     * 
     * @param period The period (DAILY, WEEKLY, MONTHLY, PERMANENT)
     * @return A list of active challenges for the period
     */
    List<Challenge> getActiveChallengesByPeriod(ChallengePeriod period);
    
    /**
     * Gets all available challenges for a user (not yet completed)
     * 
     * @param user The user
     * @return A list of available challenges
     */
    List<UserChallenge> getAvailableChallengesForUser(AppUser user);
    
    /**
     * Gets all completed challenges for a user
     * 
     * @param user The user
     * @return A list of completed challenges
     */
    List<UserChallenge> getCompletedChallengesForUser(AppUser user);
    
    /**
     * Gets completed challenges with unclaimed rewards for a user
     *
     * @param user The user
     * @return A list of completed challenges with unclaimed rewards
     */
    List<UserChallenge> getUnclaimedRewardsForUser(AppUser user);
    
    /**
     * Creates a new challenge
     * 
     * @param challenge The challenge to create
     * @return The created challenge
     */
    Challenge createChallenge(Challenge challenge);
    
    /**
     * Updates a challenge's progress for a user based on an action
     * 
     * @param user The user
     * @param type The challenge type
     * @param value The progress value to add
     * @param extraData Optional extra data for specific challenges
     */
    void updateChallengeProgress(AppUser user, ChallengeType type, int value, String extraData);
    
    /**
     * Claims a reward for a completed challenge
     * 
     * @param user The user
     * @param userChallengeId The id of the user challenge
     * @return The updated user challenge
     */
    UserChallenge claimChallengeReward(AppUser user, Long userChallengeId);
    
    /**
     * Assigns a challenge to a user
     * 
     * @param user The user
     * @param challenge The challenge
     * @return The created user challenge
     */
    UserChallenge assignChallengeToUser(AppUser user, Challenge challenge);
    
    /**
     * Assigns available challenges to a user based on the period
     * 
     * @param user The user
     * @param period The period (DAILY, WEEKLY, MONTHLY)
     * @return The number of challenges assigned
     */
    int assignPeriodChallenges(AppUser user, ChallengePeriod period);
} 