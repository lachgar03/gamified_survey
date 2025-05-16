package org.example.gamified_survey_app.gamification.service;

import lombok.RequiredArgsConstructor;
import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.auth.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Userxpservice {

    private final UserRepository userRepository;

    public AppUser getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
    }
}
