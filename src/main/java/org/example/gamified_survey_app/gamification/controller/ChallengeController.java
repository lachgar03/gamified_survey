package org.example.gamified_survey_app.gamification.controller;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.core.exception.ResourceNotFoundException;
import org.example.gamified_survey_app.gamification.dto.ChallengeDTO;
import org.example.gamified_survey_app.gamification.dto.UserChallengeDTO;
import org.example.gamified_survey_app.gamification.model.Challenge;
import org.example.gamified_survey_app.gamification.model.UserChallenge;
import org.example.gamified_survey_app.gamification.service.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/challenges")
public class ChallengeController {

    private final ChallengeService challengeService;

    @Autowired
    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @GetMapping
    public ResponseEntity<List<ChallengeDTO>> getAllChallenges() {
        List<ChallengeDTO> challenges = challengeService.getAllChallenges().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(challenges);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChallengeDTO> getChallengeById(@PathVariable Long id) {
        return challengeService.getChallengeById(id)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found with id: " + id));
    }

    @PostMapping
    public ResponseEntity<ChallengeDTO> createChallenge(@RequestBody ChallengeDTO challengeDTO) {
        Challenge challenge = new Challenge();
        updateChallengeFromDTO(challenge, challengeDTO);
        Challenge savedChallenge = challengeService.createChallenge(challenge);
        return ResponseEntity.ok(convertToDTO(savedChallenge));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChallengeDTO> updateChallenge(@PathVariable Long id, @RequestBody ChallengeDTO challengeDTO) {
        Challenge challenge = challengeService.getChallengeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found with id: " + id));

        updateChallengeFromDTO(challenge, challengeDTO);
        Challenge updatedChallenge = challengeService.updateChallenge(id, challenge);
        return ResponseEntity.ok(convertToDTO(updatedChallenge));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChallenge(@PathVariable Long id) {
        challengeService.deleteChallenge(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user")
    public ResponseEntity<List<UserChallengeDTO>> getUserChallenges(@AuthenticationPrincipal AppUser user) {
        List<UserChallengeDTO> challenges = challengeService.getUserChallenges(user).stream()
                .map(this::convertToUserChallengeDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(challenges);
    }

    @PostMapping("/{challengeId}/award")
    public ResponseEntity<UserChallengeDTO> awardChallenge(
            @AuthenticationPrincipal AppUser user,
            @PathVariable Long challengeId) {

        UserChallenge userChallenge = challengeService.awardChallenge(user, challengeId);
        return ResponseEntity.ok(convertToUserChallengeDTO(userChallenge));
    }

//    @PostMapping("/claim/{userChallengeId}")
//    public ResponseEntity<UserChallengeDTO> claimReward(
//            @AuthenticationPrincipal AppUser user,
//            @PathVariable Long userChallengeId) {
//
//        UserChallenge userChallenge = challengeService.claimChallengeReward(user, userChallengeId);
//        return ResponseEntity.ok(convertToUserChallengeDTO(userChallenge));
//    }

    @GetMapping("/{challengeId}/user-has")
    public ResponseEntity<Boolean> hasUserEarnedChallenge(
            @AuthenticationPrincipal AppUser user,
            @PathVariable Long challengeId) {

        boolean hasEarned = challengeService.hasUserEarnedChallenge(user, challengeId);
        return ResponseEntity.ok(hasEarned);
    }

    private ChallengeDTO convertToDTO(Challenge challenge) {
        return new ChallengeDTO(
                challenge.getId(),
                challenge.getName(),
                challenge.getDescription(),
                challenge.getXpValue(),
                challenge.getAchievementCondition(),
                challenge.getTargetValue(),
                challenge.getActionType(),
                challenge.getPeriod(), // ✅ Added
                null, false, null,
                false, null
        );
    }

    private void updateChallengeFromDTO(Challenge challenge, ChallengeDTO dto) {
        challenge.setName(dto.getName());
        challenge.setDescription(dto.getDescription());
        challenge.setXpValue(dto.getXpValue());
        challenge.setAchievementCondition(dto.getAchievementCondition());
        challenge.setActionType(dto.getActionType());
        challenge.setTargetValue(dto.getTargetValue());
        challenge.setPeriod(dto.getPeriod()); // ✅ Added
    }

    private UserChallengeDTO convertToUserChallengeDTO(UserChallenge ub) {
        ChallengeDTO challengeDTO = convertToDTO(ub.getChallenge());
        challengeDTO.setCurrentValue(ub.getCurrentValue());
        challengeDTO.setCompleted(ub.isCompleted());
        challengeDTO.setCompletedAt(ub.getCompletedAt());
        challengeDTO.setRewardClaimed(ub.isRewardClaimed());
        challengeDTO.setRewardClaimedAt(ub.getRewardClaimedAt());

        return new UserChallengeDTO(
                ub.getId(),
                ub.getUser().getId(),
                ub.getUser().getEmail(),
                challengeDTO,
                ub.getCurrentValue(),
                ub.isCompleted(),
                ub.getCompletedAt(),
                ub.isRewardClaimed(),
                ub.getRewardClaimedAt(),
                ub.getStartedAt()
        );
    }
}
