package org.example.gamified_survey_app.auth.repository;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.auth.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUser(AppUser user);
    void deleteByUser(AppUser user);
} 