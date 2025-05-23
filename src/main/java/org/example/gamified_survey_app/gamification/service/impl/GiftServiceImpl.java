package org.example.gamified_survey_app.gamification.service.impl;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.core.exception.ResourceNotFoundException;
import org.example.gamified_survey_app.gamification.model.Gift;
import org.example.gamified_survey_app.gamification.model.GiftRedemption;
import org.example.gamified_survey_app.gamification.repository.GiftRedemptionRepository;
import org.example.gamified_survey_app.gamification.repository.GiftRepository;
import org.example.gamified_survey_app.gamification.service.GiftService;
import org.example.gamified_survey_app.gamification.service.UserXpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GiftServiceImpl implements GiftService {

    private final GiftRepository giftRepository;
    private final GiftRedemptionRepository redemptionRepository;
    private final UserXpService userXpService;

    @Autowired
    public GiftServiceImpl(GiftRepository giftRepository, GiftRedemptionRepository redemptionRepository, UserXpService userXpService) {
        this.giftRepository = giftRepository;
        this.redemptionRepository = redemptionRepository;
        this.userXpService = userXpService;
    }

    @Override
    public List<Gift> getAllGifts() {
        return giftRepository.findAll();
    }

    @Override
    public List<Gift> getAvailableGifts() {
        return giftRepository.findByActiveTrueAndAvailableQuantityGreaterThan(0);
    }

    @Override
    public List<Gift> getGiftsForUserPoints(AppUser user) {
        return giftRepository.findByPointsCostLessThanEqual(user.getXp());
    }

    @Override
    public Optional<Gift> getGiftById(Long id) {
        return giftRepository.findById(id);
    }

    @Override
    public Gift createGift(Gift gift) {
        return giftRepository.save(gift);
    }

    @Override
    public Gift updateGift(Long id, Gift giftDetails) {
        Gift gift = giftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gift not found with id: " + id));

        gift.setName(giftDetails.getName());
        gift.setDescription(giftDetails.getDescription());
        gift.setImageUrl(giftDetails.getImageUrl());
        gift.setPointsCost(giftDetails.getPointsCost());
        gift.setAvailableQuantity(giftDetails.getAvailableQuantity());
        gift.setActive(giftDetails.isActive());

        return giftRepository.save(gift);
    }

    @Override
    public void deleteGift(Long id) {
        giftRepository.deleteById(id);
    }

    @Override
    @Transactional
    public GiftRedemption redeemGift(AppUser user, Long giftId, String deliveryAddress, String deliveryNotes) {
        Gift gift = giftRepository.findById(giftId)
                .orElseThrow(() -> new ResourceNotFoundException("Gift not found with id: " + giftId));
        
        // Check if the gift is available and active
        if (!gift.isActive() || gift.getAvailableQuantity() <= 0) {
            throw new IllegalStateException("Gift is not available for redemption");
        }
        
        // Check if user has enough points
        if (user.getXp() < gift.getPointsCost()) {
            throw new IllegalStateException("User does not have enough points to redeem this gift");
        }
        
        // Create redemption record
        GiftRedemption redemption = new GiftRedemption();
        redemption.setUser(user);
        redemption.setGift(gift);
        redemption.setRedeemedAt(LocalDateTime.now());
        redemption.setPointsSpent(gift.getPointsCost());
        redemption.setStatus(GiftRedemption.RedemptionStatus.PENDING);
        redemption.setDeliveryAddress(deliveryAddress);
        redemption.setDeliveryNotes(deliveryNotes);
        
        // Update gift quantity
        gift.setAvailableQuantity(gift.getAvailableQuantity() - 1);
        giftRepository.save(gift);
        
        // Update user points
        userXpService.updateUserXp(user , -(gift.getPointsCost()));
        // Save redemption
        return redemptionRepository.save(redemption);
    }

    @Override
    public List<GiftRedemption> getUserRedemptions(AppUser user) {
        return redemptionRepository.findByUser(user);
    }

    @Override
    public List<GiftRedemption> getPendingRedemptions() {
        return redemptionRepository.findByStatus(GiftRedemption.RedemptionStatus.PENDING);
    }

    @Override
    @Transactional
    public GiftRedemption updateRedemptionStatus(Long redemptionId, GiftRedemption.RedemptionStatus status) {
        GiftRedemption redemption = redemptionRepository.findById(redemptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Redemption not found with id: " + redemptionId));
        
        // If cancelling a redemption, refund the points and increase the gift quantity
        if (status == GiftRedemption.RedemptionStatus.CANCELLED && 
            redemption.getStatus() != GiftRedemption.RedemptionStatus.CANCELLED) {
            
            AppUser user = redemption.getUser();
            userXpService.updateUserXp(user, redemption.getPointsSpent());
            
            Gift gift = redemption.getGift();
            gift.setAvailableQuantity(gift.getAvailableQuantity() + 1);
            giftRepository.save(gift);
        }
        
        redemption.setStatus(status);
        return redemptionRepository.save(redemption);
    }
} 