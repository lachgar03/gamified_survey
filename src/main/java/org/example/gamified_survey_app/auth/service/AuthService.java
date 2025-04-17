package org.example.gamified_survey_app.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.gamified_survey_app.auth.dto.AuthResponse;
import org.example.gamified_survey_app.auth.dto.LoginRequest;
import org.example.gamified_survey_app.auth.dto.RegisterRequest;
import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.auth.repository.UserRepository;
import org.example.gamified_survey_app.core.util.JwtUtils;
import org.example.gamified_survey_app.user.model.UserProfile;
import org.example.gamified_survey_app.user.repository.UserProfileRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public AuthResponse register(RegisterRequest request) {
        AppUser user = new AppUser();
        UserProfile userProfile = new UserProfile();
        userProfile.setLastName(request.getNom());
        userProfile.setFirstName(request.getPrenom());
        userProfile.setProfession(null);
        userProfile.setAge(0);
        userProfile.setPhoneNumber(null);
        userProfile.setRegion(null);
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_PARTICIPANT");
        userRepository.save(user);
        userProfile.setUser(user);
        userProfileRepository.save(userProfile);

        // Use the injected UserDetailsService to load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtils.generateToken(userDetails);
        return new AuthResponse(token, user);
    }

    public AuthResponse login(LoginRequest request) {
        AppUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        // Load UserDetails from the UserDetailsService
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtils.generateToken(userDetails);
        return new AuthResponse(token, user);
    }
}