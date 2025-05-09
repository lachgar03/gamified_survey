package org.example.gamified_survey_app.gamification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedeemGiftRequest {
    private Long giftId;
    private String deliveryAddress;
    private String deliveryNotes;
} 