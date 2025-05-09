package org.example.gamified_survey_app.gamification.service;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.gamification.model.Gift;
import org.example.gamified_survey_app.gamification.model.GiftRedemption;

import java.util.List;
import java.util.Optional;

public interface GiftService {
    List<Gift> getAllGifts();
    
    List<Gift> getAvailableGifts();
    
    List<Gift> getGiftsForUserPoints(AppUser user);
    
    Optional<Gift> getGiftById(Long id);
    
    Gift createGift(Gift gift);
    
    Gift updateGift(Long id, Gift giftDetails);
    
    void deleteGift(Long id);
    
    GiftRedemption redeemGift(AppUser user, Long giftId, String deliveryAddress, String deliveryNotes);
    
    List<GiftRedemption> getUserRedemptions(AppUser user);
    
    List<GiftRedemption> getPendingRedemptions();
    
    GiftRedemption updateRedemptionStatus(Long redemptionId, GiftRedemption.RedemptionStatus status);
} 