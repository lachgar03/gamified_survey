package org.example.gamified_survey_app.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.gamified_survey_app.core.constants.Roles;
import org.example.gamified_survey_app.gamification.model.Level;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;
    private int xp;
    @ManyToOne
    @JoinColumn(name = "level_id")
    private Level level;
    private LocalDateTime levelUpAt;







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
        return banExpiresAt == null || LocalDateTime.now().isBefore(banExpiresAt);
    }

    // === UserDetails implementation ===

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isBanActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    public LocalDateTime setLevelUpAt() {
        return LocalDateTime.now();
    }
}
