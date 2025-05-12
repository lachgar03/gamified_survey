package org.example.gamified_survey_app.user.model;

import java.time.LocalDateTime;

import org.example.gamified_survey_app.auth.model.AppUser;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Referral {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // The user who made the referral (referrer)
    @ManyToOne
    @JoinColumn(name = "referrer_id")
    private AppUser referrer;
    
    // The user who was referred (referee)
    @ManyToOne
    @JoinColumn(name = "referee_id")
    private AppUser referee;
    
    // When the referral was created
    private LocalDateTime createdAt;
    
    // Referral code used (if any)
    private String referralCode;
    
    // Whether the XP bonus has been awarded to the referrer
    private boolean bonusAwarded = false;
    
    // When the bonus was awarded
    private LocalDateTime bonusAwardedAt;
    
    // The amount of XP awarded
    private Integer xpAwarded;
} 