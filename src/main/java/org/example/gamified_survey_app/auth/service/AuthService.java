package org.example.gamified_survey_app.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.gamified_survey_app.auth.dto.AuthResponse;
import org.example.gamified_survey_app.auth.dto.LoginRequest;
import org.example.gamified_survey_app.auth.dto.PasswordResetDto;
import org.example.gamified_survey_app.auth.dto.RegisterRequest;
import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.auth.model.PasswordResetToken;
import org.example.gamified_survey_app.auth.repository.PasswordResetTokenRepository;
import org.example.gamified_survey_app.auth.repository.UserRepository;
import org.example.gamified_survey_app.core.constants.Roles;
import org.example.gamified_survey_app.core.exception.CustomException;
import org.example.gamified_survey_app.core.service.EmailService;
import org.example.gamified_survey_app.core.util.JwtUtils;
import org.example.gamified_survey_app.user.model.UserProfile;
import org.example.gamified_survey_app.user.repository.UserProfileRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    
    // Token expiration time in minutes
    private static final int RESET_TOKEN_EXPIRATION_MINUTES = 30;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("An account with this email already exists");
        }
        AppUser user = new AppUser();
        UserProfile userProfile = new UserProfile();
        userProfile.setLastName(request.getLastname());
        userProfile.setFirstName(request.getFirstname());
        userProfile.setProfession(null);
        userProfile.setAge(0);
        userProfile.setPhoneNumber(null);
        userProfile.setRegion(null);
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.getRoles().add(Roles.PARTICIPANT);
        user.getRoles().add(Roles.CREATOR);
        userRepository.save(user);
        userProfile.setUser(user);
        userProfileRepository.save(userProfile);

        // Use the injected UserDetailsService to load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtils.generateToken(userDetails);
        return new AuthResponse(token, user);
    }

    public AuthResponse login(LoginRequest request) {
        // Special case for admin login
        if ("admin".equals(request.getEmail()) && "admin".equals(request.getPassword())) {
            AppUser adminUser = userRepository.findByEmail("admin")
                    .orElseGet(() -> {
                        // Create admin user if it doesn't exist
                        AppUser newAdmin = new AppUser();
                        newAdmin.setEmail("admin");
                        newAdmin.setPassword(passwordEncoder.encode("admin"));
                        // Initialize roles set if needed
                        if (newAdmin.getRoles() == null) {
                            newAdmin.setRoles(new HashSet<>());
                        }
                        newAdmin.getRoles().add(Roles.ADMIN);
                        return userRepository.save(newAdmin);
                    });
            
            // Ensure admin role is assigned
            if (!adminUser.getRoles().contains(Roles.ADMIN)) {
                adminUser.getRoles().add(Roles.ADMIN);
                userRepository.save(adminUser);
            }
            
            // Generate token for admin
            UserDetails userDetails = userDetailsService.loadUserByUsername(adminUser.getEmail());
            String token = jwtUtils.generateToken(userDetails);
            return new AuthResponse(token, adminUser);
        }
        
        // Regular user login
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
    
    @Transactional
    public void createPasswordResetTokenForUser(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("No user found with this email"));
        
        // Remove any existing tokens
        passwordResetTokenRepository.findByUser(user).ifPresent(token -> 
            passwordResetTokenRepository.deleteByUser(user)
        );
        
        // Create new token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(RESET_TOKEN_EXPIRATION_MINUTES));
        
        passwordResetTokenRepository.save(resetToken);
        
        // Send password reset email
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }
    
    @Transactional
    public boolean validatePasswordResetToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new CustomException("Invalid password reset token"));
        
        if (resetToken.isExpired()) {
            passwordResetTokenRepository.delete(resetToken);
            throw new CustomException("Token has expired");
        }
        
        return true;
    }
    
    @Transactional
    public void resetPassword(PasswordResetDto resetDto) {
        if (!resetDto.getNewPassword().equals(resetDto.getConfirmPassword())) {
            throw new CustomException("Passwords don't match");
        }
        
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(resetDto.getToken())
                .orElseThrow(() -> new CustomException("Invalid password reset token"));
        
        if (resetToken.isExpired()) {
            passwordResetTokenRepository.delete(resetToken);
            throw new CustomException("Token has expired");
        }
        
        AppUser user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(resetDto.getNewPassword()));
        userRepository.save(user);
        
        passwordResetTokenRepository.delete(resetToken);
    }
}