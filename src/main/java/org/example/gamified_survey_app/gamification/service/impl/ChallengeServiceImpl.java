package org.example.gamified_survey_app.gamification.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.gamification.constant.ChallengeType;
import org.example.gamified_survey_app.gamification.model.Challenge;
import org.example.gamified_survey_app.gamification.model.UserChallenge;
import org.example.gamified_survey_app.gamification.repository.ChallengeRepository;
import org.example.gamified_survey_app.gamification.repository.UserChallengeRepository;
import org.example.gamified_survey_app.gamification.service.ChallengeService;
import org.example.gamified_survey_app.gamification.service.UserXpService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final UserXpService userXpService;

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
        List<UserChallenge> challenges = userChallengeRepository.findByUserWithChallenge(user);
        return challenges.stream()
                .filter(uc -> uc.getChallenge() != null) // Filter out any null challenges
                .collect(Collectors.toList());
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
    @Transactional
    public UserChallenge awardChallenge(AppUser user, Long challengeId) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        return userChallengeRepository.findByUserAndChallengeId(user, challengeId)
                .orElseGet(() -> {
                    Challenge challenge = challengeRepository.findById(challengeId)
                            .orElseThrow(() -> new EntityNotFoundException("Challenge not found with id: " + challengeId));

                    UserChallenge userChallenge = new UserChallenge();
                    userChallenge.setUser(user);
                    userChallenge.setChallenge(challenge);
                    userChallenge.setStartedAt(LocalDateTime.now());
                    userChallenge.setCurrentValue(0);
                    userChallenge.setCompleted(false);
                    userChallenge.setRewardClaimed(false);

                    return userChallengeRepository.save(userChallenge);
                });
    }

    @Override
    @Transactional
    public boolean hasUserEarnedChallenge(AppUser user, Long challengeId) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        return userChallengeRepository.isChallengeCompleted(user, challengeId)
                .orElse(false);
    }

    @Override
    public UserChallenge claimChallengeReward(AppUser user, Challenge userChallengeId) {
        UserChallenge userChallenge = userChallengeRepository.findById(userChallengeId.getId())
                .orElseThrow(() -> new RuntimeException("UserChallenge not found"));

        if (!userChallenge.getUser().getId().equals(user.getId()) || !userChallenge.isCompleted()) {
            throw new RuntimeException("Cannot claim reward");
        }

        userChallenge.setRewardClaimed(true);
        userChallenge.setRewardClaimedAt(LocalDateTime.now());
        userXpService.updateUserXp(user, userChallengeId.getXpValue());
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

            if (newValue >= challenge.getTargetValue()) {
                userChallenge.setCompleted(true);
                userChallenge.setCompletedAt(LocalDateTime.now());
                claimChallengeReward(userChallenge.getUser(), challenge);
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
