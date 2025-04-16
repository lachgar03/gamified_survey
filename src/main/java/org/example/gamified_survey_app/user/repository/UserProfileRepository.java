package org.example.gamified_survey_app.user.repository;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.user.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUser(AppUser user);
}
