package org.example.gamified_survey_app.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.gamified_survey_app.core.constants.Roles;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
    public class AppUser {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column(unique = true)
        private String email;
        private String password;
        private int xp;
    public int calculateLevel() {
        return xp / 1000; // or any custom level formula
    }

    @Enumerated(EnumType.STRING)
    private Roles role;
    
    // Ban fields
    private boolean banned = false;
    private String banReason;
    private LocalDateTime bannedAt;
    private LocalDateTime banExpiresAt;
    
    public boolean isBanActive() {
        if (!banned) {
            return false;
        }
        
        // Check if ban is permanent or still active
        return banExpiresAt == null || LocalDateTime.now().isBefore(banExpiresAt);
    }
}

