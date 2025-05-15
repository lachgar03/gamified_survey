package org.example.gamified_survey_app.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gamified_survey_app.auth.dto.AuthResponse;
import org.example.gamified_survey_app.auth.dto.LoginRequest;
import org.example.gamified_survey_app.auth.dto.RegisterRequest;
import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.auth.security.CustomUserDetailsService;
import org.example.gamified_survey_app.auth.service.AuthService;
import org.example.gamified_survey_app.core.util.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller responsible for authentication operations including
 * user registration, login and other identity-related endpoints.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint for user registration
     *
     * @param registerRequest contains user registration details
     * @return JWT token and user information
     */


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Registration request received for email: {}", registerRequest.getEmail());
        try {
            AuthResponse response = authService.register(registerRequest);
            log.info("User successfully registered with email: {}", registerRequest.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("Registration failed: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } catch (Exception e) {
            log.error("Unexpected error during registration: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "An unexpected error occurred during registration");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Endpoint for user login
     *
     * @param loginRequest contains login credentials
     * @return JWT token and user information
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for email: {}", loginRequest.getEmail());
        try {
            AuthResponse response = authService.login(loginRequest);

            // Check if a user is banned
            AppUser user = response.getUtilisateur();
            if (user.isBanActive()) {
                log.warn("Login attempt by banned user: {}", loginRequest.getEmail());
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Account is currently banned");
                error.put("reason", user.getBanReason());
                error.put("expiresAt", user.getBanExpiresAt());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            log.info("User successfully logged in: {}", loginRequest.getEmail());
            return ResponseEntity.ok(response);
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            log.warn("Authentication failed: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            log.error("Unexpected error during authentication: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "An unexpected error occurred during authentication");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Endpoint to validate JWT token
     *
     * @param token JWT token to validate
     * @return status of token validation
     */
    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        try {
            boolean isValid = authService.validateToken(token);
            Map<String, Boolean> response = new HashMap<>();
            response.put("valid", isValid);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Endpoint to check if an email is already registered
     *
     * @param email email to check
     * @return status indicating if email is available
     */
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmailAvailability(@RequestParam String email) {
        boolean isAvailable = authService.isEmailAvailable(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", isAvailable);
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint for authentication service
     *
     * @return status of auth service
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "Auth service is running");
        return ResponseEntity.ok(status);
    }


}