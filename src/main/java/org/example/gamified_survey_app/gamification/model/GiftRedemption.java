package org.example.gamified_survey_app.gamification.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.gamified_survey_app.auth.model.AppUser;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GiftRedemption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;
    
    @ManyToOne
    @JoinColumn(name = "gift_id")
    private Gift gift;
    
    private LocalDateTime redeemedAt;
    
    // Points spent on this redemption
    private Integer pointsSpent;
    
    // Status of redemption: PENDING, PROCESSED, DELIVERED, CANCELLED
    @Enumerated(EnumType.STRING)
    private RedemptionStatus status = RedemptionStatus.PENDING;
    
    // Optional delivery information
    private String deliveryAddress;
    private String deliveryNotes;
    
    public enum RedemptionStatus {
        PENDING, PROCESSED, DELIVERED, CANCELLED
    }
} 