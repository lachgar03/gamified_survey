package org.example.gamified_survey_app.gamification.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.gamification.constant.ChallengeType;
import org.example.gamified_survey_app.gamification.model.Challenge;
import org.example.gamified_survey_app.gamification.model.UserChallenge;
import org.example.gamified_survey_app.gamification.repository.ChallengeRepository;
import org.example.gamified_survey_app.gamification.repository.UserChallengeRepository;
import org.example.gamified_survey_app.gamification.service.ChallengeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final UserChallengeRepository userChallengeRepository;

    @Override
    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAll();
    }

    @Override
    public Optional<Challenge> getChallengeById(Long id) {
        return challengeRepository.findById(id);
    }

    @Override
    public Challenge createChallenge(Challenge challenge) {
        return challengeRepository.save(challenge);
    }

    @Override
    public Challenge updateChallenge(Long id, Challenge challengeDetails) {
        Challenge challenge = challengeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));
        challenge.setName(challengeDetails.getName());
        challenge.setDescription(challengeDetails.getDescription());
        challenge.setAchievementCondition(challengeDetails.getAchievementCondition());
        return challengeRepository.save(challenge);
    }

    @Override
    public void deleteChallenge(Long id) {
        challengeRepository.deleteById(id);
    }

    @Override
    public List<UserChallenge> getUserChallenges(AppUser user) {
        return userChallengeRepository.findByUser(user);
    }

    @Override
    public List<UserChallenge> getCompletedChallenges(AppUser user) {
        return userChallengeRepository.findByUserAndCompletedTrue(user);
    }

    @Override
    public List<UserChallenge> getInProgressChallenges(AppUser user) {
        return userChallengeRepository.findByUserAndCompletedFalse(user);
    }

    @Override
    public List<UserChallenge> getUnclaimedChallenges(AppUser user) {
        return userChallengeRepository.findByUserAndCompletedTrueAndRewardClaimedFalse(user);
    }

    @Override
    public UserChallenge awardChallenge(AppUser user, Long challengeId) {
        Optional<UserChallenge> existing = userChallengeRepository.findByUserAndChallengeId(user, challengeId);
        if (existing.isPresent()) {
            return existing.get();
        }

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        UserChallenge userChallenge = new UserChallenge();
        userChallenge.setUser(user);
        userChallenge.setChallenge(challenge);
        userChallenge.setStartedAt(LocalDateTime.now());
        userChallenge.setCurrentValue(0);
        userChallenge.setCompleted(false);
        userChallenge.setRewardClaimed(false);

        return userChallengeRepository.save(userChallenge);
    }

    @Override
    public boolean hasUserEarnedChallenge(AppUser user, Long challengeId) {
        return userChallengeRepository.findByUserAndChallengeId(user, challengeId)
                .map(UserChallenge::isCompleted)
                .orElse(false);
    }

    @Override
    public UserChallenge claimChallengeReward(AppUser user, Long userChallengeId) {
        UserChallenge userChallenge = userChallengeRepository.findById(userChallengeId)
                .orElseThrow(() -> new RuntimeException("UserChallenge not found"));

        if (!userChallenge.getUser().getId().equals(user.getId()) || !userChallenge.isCompleted()) {
            throw new RuntimeException("Cannot claim reward");
        }

        userChallenge.setRewardClaimed(true);
        userChallenge.setRewardClaimedAt(LocalDateTime.now());

        return userChallengeRepository.save(userChallenge);
    }

    @Override
    public void updateChallengeProgress(AppUser user, ChallengeType type, int value, String extraData) {
        List<UserChallenge> userChallenges = userChallengeRepository.findByUser(user);

        for (UserChallenge userChallenge : userChallenges) {
            if (userChallenge.isCompleted()) continue;

            Challenge challenge = userChallenge.getChallenge();
            if (!challenge.getAchievementCondition().contains(type.name())) continue;

            int newValue = userChallenge.getCurrentValue() + value;
            userChallenge.setCurrentValue(newValue);

            // For now assume each challenge completion threshold is 100
            if (newValue >= 100) {
                userChallenge.setCompleted(true);
                userChallenge.setCompletedAt(LocalDateTime.now());
            }

            userChallengeRepository.save(userChallenge);
        }
    }

    @Override
    public int assignChallengesToUser(AppUser user) {
        List<Challenge> all = challengeRepository.findAll();
        int count = 0;

        for (Challenge challenge : all) {
            if (userChallengeRepository.findByUserAndChallengeId(user, challenge.getId()).isEmpty()) {
                UserChallenge userChallenge = new UserChallenge();
                userChallenge.setUser(user);
                userChallenge.setChallenge(challenge);
                userChallenge.setStartedAt(LocalDateTime.now());
                userChallenge.setCurrentValue(0);
                userChallenge.setCompleted(false);
                userChallenge.setRewardClaimed(false);
                userChallengeRepository.save(userChallenge);
                count++;
            }
        }
        return count;
    }
}
