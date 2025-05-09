package org.example.gamified_survey_app.gamification.controller;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.core.exception.ResourceNotFoundException;
import org.example.gamified_survey_app.gamification.dto.GiftDTO;
import org.example.gamified_survey_app.gamification.dto.GiftRedemptionDTO;
import org.example.gamified_survey_app.gamification.dto.RedeemGiftRequest;
import org.example.gamified_survey_app.gamification.model.Gift;
import org.example.gamified_survey_app.gamification.model.GiftRedemption;
import org.example.gamified_survey_app.gamification.service.GiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/gifts")
public class GiftController {

    private final GiftService giftService;

    @Autowired
    public GiftController(GiftService giftService) {
        this.giftService = giftService;
    }

    @GetMapping
    public ResponseEntity<List<GiftDTO>> getAllGifts() {
        List<GiftDTO> gifts = giftService.getAllGifts().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(gifts);
    }

    @GetMapping("/available")
    public ResponseEntity<List<GiftDTO>> getAvailableGifts() {
        List<GiftDTO> gifts = giftService.getAvailableGifts().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(gifts);
    }

    @GetMapping("/for-user")
    public ResponseEntity<List<GiftDTO>> getGiftsForUser(@AuthenticationPrincipal AppUser user) {
        List<GiftDTO> gifts = giftService.getGiftsForUserPoints(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(gifts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GiftDTO> getGiftById(@PathVariable Long id) {
        return giftService.getGiftById(id)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Gift not found with id: " + id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GiftDTO> createGift(@RequestBody GiftDTO giftDTO) {
        Gift gift = new Gift();
        updateGiftFromDTO(gift, giftDTO);
        
        Gift savedGift = giftService.createGift(gift);
        return ResponseEntity.ok(convertToDTO(savedGift));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GiftDTO> updateGift(@PathVariable Long id, @RequestBody GiftDTO giftDTO) {
        Gift gift = giftService.getGiftById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gift not found with id: " + id));
        
        updateGiftFromDTO(gift, giftDTO);
        
        Gift updatedGift = giftService.updateGift(id, gift);
        return ResponseEntity.ok(convertToDTO(updatedGift));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGift(@PathVariable Long id) {
        giftService.deleteGift(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/redeem")
    @PreAuthorize("hasAnyRole('PARTICIPANT', 'CREATOR', 'ADMIN')")
    public ResponseEntity<GiftRedemptionDTO> redeemGift(
            @AuthenticationPrincipal AppUser user,
            @RequestBody RedeemGiftRequest request) {
        
        GiftRedemption redemption = giftService.redeemGift(
                user, 
                request.getGiftId(),
                request.getDeliveryAddress(),
                request.getDeliveryNotes()
        );
        
        return ResponseEntity.ok(convertToRedemptionDTO(redemption));
    }

    @GetMapping("/redemptions")
    public ResponseEntity<List<GiftRedemptionDTO>> getUserRedemptions(@AuthenticationPrincipal AppUser user) {
        List<GiftRedemptionDTO> redemptions = giftService.getUserRedemptions(user).stream()
                .map(this::convertToRedemptionDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(redemptions);
    }

    @GetMapping("/redemptions/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GiftRedemptionDTO>> getPendingRedemptions() {
        List<GiftRedemptionDTO> redemptions = giftService.getPendingRedemptions().stream()
                .map(this::convertToRedemptionDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(redemptions);
    }

    @PutMapping("/redemptions/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GiftRedemptionDTO> updateRedemptionStatus(
            @PathVariable Long id,
            @RequestParam GiftRedemption.RedemptionStatus status) {
        
        GiftRedemption updatedRedemption = giftService.updateRedemptionStatus(id, status);
        return ResponseEntity.ok(convertToRedemptionDTO(updatedRedemption));
    }

    private GiftDTO convertToDTO(Gift gift) {
        return new GiftDTO(
                gift.getId(),
                gift.getName(),
                gift.getDescription(),
                gift.getImageUrl(),
                gift.getPointsCost(),
                gift.getAvailableQuantity(),
                gift.isActive()
        );
    }

    private void updateGiftFromDTO(Gift gift, GiftDTO dto) {
        gift.setName(dto.getName());
        gift.setDescription(dto.getDescription());
        gift.setImageUrl(dto.getImageUrl());
        gift.setPointsCost(dto.getPointsCost());
        gift.setAvailableQuantity(dto.getAvailableQuantity());
        gift.setActive(dto.isActive());
    }

    private GiftRedemptionDTO convertToRedemptionDTO(GiftRedemption redemption) {
        return new GiftRedemptionDTO(
                redemption.getId(),
                redemption.getUser().getId(),
                redemption.getUser().getEmail(),
                convertToDTO(redemption.getGift()),
                redemption.getRedeemedAt(),
                redemption.getPointsSpent(),
                redemption.getStatus(),
                redemption.getDeliveryAddress(),
                redemption.getDeliveryNotes()
        );
    }
} 