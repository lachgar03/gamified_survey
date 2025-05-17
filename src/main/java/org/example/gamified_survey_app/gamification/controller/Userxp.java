package org.example.gamified_survey_app.gamification.controller;

import lombok.RequiredArgsConstructor;
import org.example.gamified_survey_app.gamification.dto.AppUserDto;
import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.gamification.service.UserXpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class Userxp {

    private final UserXpService userService;

    @GetMapping("/me")
    public ResponseEntity<AppUserDto> getCurrentUser() {
        AppUser currentUser = userService.getCurrentUser(); // ✅ Correct usage
        return ResponseEntity.ok(new AppUserDto(currentUser));
    }
}
