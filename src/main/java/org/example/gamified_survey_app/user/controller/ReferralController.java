package org.example.gamified_survey_app.user.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.auth.repository.UserRepository;
import org.example.gamified_survey_app.core.exception.ResourceNotFoundException;
import org.example.gamified_survey_app.user.dto.ReferralCodeRequest;
import org.example.gamified_survey_app.user.dto.ReferralDTO;
import org.example.gamified_survey_app.user.model.Referral;
import org.example.gamified_survey_app.user.service.ReferralService;
import org.example.gamified_survey_app.user.service.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/referrals")
@RequiredArgsConstructor
public class ReferralController {
    
    private static final Logger log = LoggerFactory.getLogger(ReferralController.class);
    private final ReferralService referralService;
    private final UserProfileService userProfileService;
    private final UserRepository userRepository;
    
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('PARTICIPANT', 'CREATOR', 'ADMIN')")
    public ResponseEntity<String> createReferralCode(
            @AuthenticationPrincipal AppUser user,
            @Valid @RequestBody ReferralCodeRequest request) {
        
        log.info("Creating referral code for email: {}", request.getRefereeEmail());
        String referralCode = referralService.createReferralCode(user, request.getRefereeEmail());
        
        return ResponseEntity.ok(referralCode);
    }
    
    @GetMapping("/my-referrals")
    @PreAuthorize("hasAnyRole('PARTICIPANT', 'CREATOR', 'ADMIN')")
    public ResponseEntity<List<ReferralDTO>> getUserReferrals(@AuthenticationPrincipal AppUser user) {
        log.info("Getting referrals for user: {}", user.getEmail());
        
        List<Referral> referrals = referralService.getReferralsByUser(user);
        List<ReferralDTO> referralDTOs = referrals.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(referralDTOs);
    }
    
    @GetMapping("/my-referrer")
    @PreAuthorize("hasAnyRole('PARTICIPANT', 'CREATOR', 'ADMIN')")
    public ResponseEntity<ReferralDTO> getUserReferrer(@AuthenticationPrincipal AppUser user) {
        log.info("Getting referrer for user: {}", user.getEmail());
        
        Referral referral = referralService.getReferralForUser(user);
        
        if (referral == null) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(convertToDTO(referral));
    }
    
    @GetMapping("/count")
    @PreAuthorize("hasAnyRole('PARTICIPANT', 'CREATOR', 'ADMIN')")
    public ResponseEntity<Long> getUserReferralCount(@AuthenticationPrincipal AppUser user) {
        log.info("Getting referral count for user: {}", user.getEmail());
        
        Long count = referralService.getReferralCount(user);
        
        return ResponseEntity.ok(count);
    }
    
    private ReferralDTO convertToDTO(Referral referral) {
        ReferralDTO dto = new ReferralDTO();
        dto.setId(referral.getId());
        
        // Referrer information
        dto.setReferrerId(referral.getReferrer().getId());
        dto.setReferrerEmail(referral.getReferrer().getEmail());
        dto.setReferrerName(userProfileService.getUserDisplayName(referral.getReferrer()));
        
        // Referee information, if available
        if (referral.getReferee() != null) {
            dto.setRefereeId(referral.getReferee().getId());
            dto.setRefereeEmail(referral.getReferee().getEmail());
            dto.setRefereeName(userProfileService.getUserDisplayName(referral.getReferee()));
            dto.setJoinedAt(referral.getReferee() != null ? referral.getCreatedAt() : null);
        }
        
        dto.setReferralCode(referral.getReferralCode());
        dto.setCreatedAt(referral.getCreatedAt());
        dto.setBonusAwarded(referral.isBonusAwarded());
        dto.setBonusAwardedAt(referral.getBonusAwardedAt());
        dto.setXpAwarded(referral.getXpAwarded());
        
        return dto;
    }
} 