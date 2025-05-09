package org.example.gamified_survey_app.gamification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.gamified_survey_app.gamification.model.GiftRedemption;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GiftRedemptionDTO {
    private Long id;
    private Long userId;
    private String userName;
    private GiftDTO gift;
    private LocalDateTime redeemedAt;
    private Integer pointsSpent;
    private GiftRedemption.RedemptionStatus status;
    private String deliveryAddress;
    private String deliveryNotes;
} 