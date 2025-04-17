package org.example.gamified_survey_app.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.gamified_survey_app.user.dto.PasswordChangeDto;
import org.example.gamified_survey_app.user.dto.UserProfileDto;
import org.example.gamified_survey_app.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profileManage")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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
}
