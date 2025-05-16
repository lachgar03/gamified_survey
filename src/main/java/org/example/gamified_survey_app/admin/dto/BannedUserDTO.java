package org.example.gamified_survey_app.admin.dto;

import lombok.Getter;
import org.example.gamified_survey_app.auth.model.AppUser;

import java.time.LocalDateTime;
@Getter
public class BannedUserDTO {
    private Long id;
    private String email;
    private String banReason;
    private LocalDateTime bannedAt;
    private LocalDateTime banExpiresAt;

    public BannedUserDTO(AppUser user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.banReason = user.getBanReason();
        this.bannedAt = user.getBannedAt();
        this.banExpiresAt = user.getBanExpiresAt();
    }

    // Getters and setters
}
