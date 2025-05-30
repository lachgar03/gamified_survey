package org.example.gamified_survey_app.auth.repository;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.core.constants.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);
    
    List<AppUser> findByBannedTrue();
    Optional<Integer> findXpByEmail(String email);


    List<AppUser> findByRole(Roles roles);
}
