package org.example.gamified_survey_app.auth.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.Level;
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
import org.example.gamified_survey_app.gamification.repository.LevelRepository;
import org.example.gamified_survey_app.gamification.service.LeaderboardService;
import org.example.gamified_survey_app.user.model.Referral;
import org.example.gamified_survey_app.user.model.UserProfile;
import org.example.gamified_survey_app.user.repository.ReferralRepository;
import org.example.gamified_survey_app.user.repository.UserProfileRepository;
import org.example.gamified_survey_app.user.service.ReferralService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

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
    private final LevelRepository levelRepository;

    // Token expiration time in minutes
    private static final int RESET_TOKEN_EXPIRATION_MINUTES = 30;
    private final ReferralRepository referralRepository;
    private final ReferralService referralService;
    private final LeaderboardService leaderboardService;


    @Value("${admin.username:admin@example.com}")
    private String adminUsername;
    
    @Value("${admin.password:changeThisInProduction!}")
    private String adminPassword;

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
        String roleStr = request.getRole();

        try {
            Roles role = Roles.valueOf(roleStr.toUpperCase());
            user.setRole(role);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + roleStr);
        }
        user.setXp(0);
        user.setLevel(levelRepository.findByNumber(1).orElseThrow(() -> new CustomException("Level not found")));

        userRepository.save(user);
        userProfile.setUser(user);
        userProfileRepository.save(userProfile);

        // Use the injected UserDetailsService to load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtils.generateToken(userDetails);

        if (request.getReferralCode() != null) {
            List<Referral> referal = referralRepository.findByReferralCode(request.getReferralCode());
            if (referal.isEmpty()) {
                throw new CustomException("Invalid referral code");
            }else {
                Referral referral = referal.get(0);
                referral.setReferee(user);
                referralRepository.save(referral);
                referralService.awardReferralBonus(referral.getReferee(),15);
            }

        }
        leaderboardService.updateUserXp(user,0);
        return new AuthResponse(token, user);
    }

    public AuthResponse login(LoginRequest request) {
        // Special case for admin login
        if (adminUsername.equals(request.getEmail()) && adminPassword.equals(request.getPassword())) {
            AppUser adminUser = userRepository.findByEmail("admin@yvyr.com")
                    .orElseGet(() -> {
                        AppUser newAdmin = new AppUser();
                        newAdmin.setEmail("admin@yvyr.com");
                        newAdmin.setPassword(passwordEncoder.encode("admin123"));
                        newAdmin.setRole(Roles.ADMIN);  // ✅ Set the role directly
                        // set level
                        newAdmin.setLevel(levelRepository.findByNumber(1).orElseThrow(() -> new CustomException("Level not found")));


                        return userRepository.save(newAdmin);
                    });
            
            // Ensure admin role is assigned
            if (adminUser.getRole() != Roles.ADMIN) {
                adminUser.setRole(Roles.ADMIN);
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
    public boolean isEmailAvailable(String email) {
        return !userRepository.findByEmail(email).isPresent();
    }
    public boolean validateToken(String token) {
        return jwtUtils.validateToken(token);
    }


}