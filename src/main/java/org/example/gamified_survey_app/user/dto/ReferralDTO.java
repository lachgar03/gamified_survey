package org.example.gamified_survey_app.user.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReferralDTO {
    private Long id;
    
    // Referrer information
    private Long referrerId;
    private String referrerEmail;
    private String referrerName;
    
    // Referee information
    private Long refereeId;
    private String refereeEmail;
    private String refereeName;
    private LocalDateTime joinedAt;
    
    // Referral details
    private String referralCode;
    private LocalDateTime createdAt;
    private boolean bonusAwarded;
    private LocalDateTime bonusAwardedAt;
    private Integer xpAwarded;
} 