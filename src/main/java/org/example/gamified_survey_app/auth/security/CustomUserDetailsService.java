package org.example.gamified_survey_app.auth.security;

import lombok.RequiredArgsConstructor;
import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.auth.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser utilisateur = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        // Convertir Set<Roles> en authorities
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole().name())
        );

        return User.builder()
                .username(utilisateur.getEmail())
                .password(utilisateur.getPassword())
                .authorities(authorities)
                .build();
    }
}
