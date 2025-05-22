package org.example.gamified_survey_app.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.gamified_survey_app.user.dto.AvatarConfigDto;
import org.example.gamified_survey_app.user.dto.PasswordChangeDto;
import org.example.gamified_survey_app.user.dto.UserProfileDto;
import org.example.gamified_survey_app.user.model.AvatarConfig;
import org.example.gamified_survey_app.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static java.sql.DriverManager.println;

@RestController
@RequestMapping("/api/profileManage")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @GetMapping("/role")
    public ResponseEntity<?> getUserRole(org.springframework.security.core.Authentication authentication){
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
            String role = authentication.getAuthorities().iterator().next().getAuthority();

        return ResponseEntity.ok(Map.of("role", role));
    }
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getUserProfile() {
        return ResponseEntity.ok(userService.getUserProfile());
    }

    @PutMapping("/update")
    public ResponseEntity<UserProfileDto> updateUserProfile(@RequestBody UserProfileDto profileDto) {
        return ResponseEntity.ok(userService.updateUserProfile(profileDto));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeDto passwordChangeDto) {
        if (!passwordChangeDto.getNewPassword().equals(passwordChangeDto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("New password and confirmation do not match");
        }

        boolean success = userService.changePassword(
                passwordChangeDto.getOldPassword(),
                passwordChangeDto.getNewPassword()
        );

        if (success) {
            return ResponseEntity.ok().body("Password changed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect old password");
        }
    }
    @PutMapping("/avatar")
    public ResponseEntity<AvatarConfigDto> updateAvatar(@RequestBody AvatarConfigDto config) {
        return ResponseEntity.ok(userService.updateAvatar(config));
    }
    @GetMapping("/avatar")
    public ResponseEntity<AvatarConfigDto> getAvatar() {
        return ResponseEntity.ok(userService.getAvatarConfig());
    }
}
