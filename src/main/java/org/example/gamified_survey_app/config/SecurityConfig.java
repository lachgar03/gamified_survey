package org.example.gamified_survey_app.config;

import lombok.RequiredArgsConstructor;
import org.example.gamified_survey_app.auth.security.CustomUserDetailsService;
import org.example.gamified_survey_app.core.util.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtils jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;
    private final CorsFilter corsFilter; // Inject the CorsFilter from WebConfig

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()

                        // Admin endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Survey-related endpoints with specific roles
                        .requestMatchers(
                                "/api/surveys/create",
                                "/api/surveys/*/deactivate",
                                "/api/surveys/*/update",
                                "/api/surveys/*/delete",
                                "/api/surveys/created",
                                "/api/surveys/*/results").hasAnyRole("CREATOR", "ADMIN")

                        // Participant-specific endpoints
                        .requestMatchers("/api/surveys/available", "/api/surveys/respond").hasAnyRole("PARTICIPANT", "CREATOR", "ADMIN")

                        // Gift management - admin only
                        .requestMatchers(
                                "/api/gifts/create",
                                "/api/gifts/*/update",
                                "/api/gifts/*/delete",
                                "/api/gifts/redemptions/pending",
                                "/api/gifts/redemptions/*/status").hasRole("ADMIN")

                        // Level management - admin only
                        .requestMatchers(
                                "/api/levels/create",
                                "/api/levels/*/update",
                                "/api/levels/*/delete").hasRole("ADMIN")

                        // Forum management
                        .requestMatchers("/api/forums/*/sujet/create").hasAnyRole("CREATOR", "ADMIN")

                        // Profile management
                        .requestMatchers("/api/profile/**").authenticated()

                        // Default - require authentication for all other endpoints
                        .anyRequest().authenticated()
                )
                // Add the corsFilter before the JwtAuthenticationFilter
                .addFilterBefore(corsFilter, ChannelProcessingFilter.class)
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenUtil, userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}