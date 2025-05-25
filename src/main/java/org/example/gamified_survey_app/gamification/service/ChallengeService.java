package org.example.gamified_survey_app.gamification.service;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.gamification.constant.ChallengeType;
import org.example.gamified_survey_app.gamification.model.Challenge;
import org.example.gamified_survey_app.gamification.model.UserChallenge;

import java.util.List;
import java.util.Optional;

public interface ChallengeService {

    // Challenge Management
    List<Challenge> getAllChallenges();
    Optional<Challenge> getChallengeById(Long id);
    Challenge createChallenge(Challenge challenge);
    Challenge updateChallenge(Long id, Challenge challengeDetails);
    void deleteChallenge(Long id);

    // User-Challenge Relationships
    List<UserChallenge> getUserChallenges(AppUser user);
    List<UserChallenge> getCompletedChallenges(AppUser user);
    List<UserChallenge> getInProgressChallenges(AppUser user);
    List<UserChallenge> getUnclaimedChallenges(AppUser user);

    UserChallenge awardChallenge(AppUser user, Long badgeId);
    boolean hasUserEarnedChallenge(AppUser user, Long badgeId);
    UserChallenge claimChallengeReward(AppUser user, Long userChallengeId);

    // Progress Tracking
    void updateChallengeProgress(AppUser user, ChallengeType type, int value, String extraData);
    int assignChallengesToUser(AppUser user);
}
