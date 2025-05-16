package org.example.gamified_survey_app.gamification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.gamified_survey_app.auth.model.AppUser;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUserDto {
    private Long id;
    private String email;
    private int xp;

    public AppUserDto(AppUser user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.xp = user.getXp();
    }
}
