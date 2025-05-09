package org.example.gamified_survey_app.admin.controller;

import lombok.RequiredArgsConstructor;
import org.example.gamified_survey_app.admin.dto.UserBanRequest;
import org.example.gamified_survey_app.admin.service.AdminService;
import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.core.exception.CustomException;
import org.example.gamified_survey_app.survey.model.Survey;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    
    @PostMapping("/users/ban")
    public ResponseEntity<?> banUser(@RequestBody UserBanRequest request) {
        try {
            AppUser bannedUser = adminService.banUser(request);
            return ResponseEntity.ok().body("User has been banned successfully");
        } catch (CustomException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/users/{userId}/unban")
    public ResponseEntity<?> unbanUser(@PathVariable Long userId) {
        try {
            AppUser unbannedUser = adminService.unbanUser(userId);
            return ResponseEntity.ok().body("User has been unbanned successfully");
        } catch (CustomException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/users/banned")
    public ResponseEntity<?> getBannedUsers() {
        try {
            List<AppUser> bannedUsers = adminService.getBannedUsers();
            return ResponseEntity.ok().body(bannedUsers);
        } catch (CustomException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/surveys/{surveyId}/verify")
    public ResponseEntity<?> verifySurvey(@PathVariable Long surveyId) {
        try {
            Survey verifiedSurvey = adminService.verifySurvey(surveyId);
            return ResponseEntity.ok().body("Survey has been verified successfully");
        } catch (CustomException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 