package org.example.gamified_survey_app.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gamified_survey_app.auth.security.CustomUserDetailsService;
import org.example.gamified_survey_app.core.util.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");
            
            // Log the authentication state clearly
            if (!StringUtils.hasText(authHeader)) {
                log.info("Authentication header is missing for request to: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }
            
            // Check for "Bearer null" case explicitly
            if ("Bearer null".equals(authHeader)) {
                log.warn("Authentication header contains 'Bearer null' which is invalid for request to: {}", request.getRequestURI());
                sendUnauthorizedError(response, "Invalid authentication token: received 'Bearer null'");
                return;
            }
            
            if (!authHeader.startsWith("Bearer ")) {
                log.warn("Malformed Authorization header - does not start with 'Bearer ': {}", authHeader);
                sendUnauthorizedError(response, "Malformed Authorization header - must start with 'Bearer '");
                return;
            }

            String token = jwtUtils.resolveToken(request);
            
            if (token == null) {
                log.warn("Token could not be extracted from Authorization header for request to: {}", request.getRequestURI());
                sendUnauthorizedError(response, "Token could not be extracted from Authorization header");
                return;
            }

            if (jwtUtils.validateToken(token)) {
                String username = jwtUtils.getUsernameFromToken(token);
                log.debug("Username extracted from token: {}", username);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(auth);
                log.debug("Authentication successful for user: {}", username);
            } else {
                log.warn("Invalid token detected for request to: {}", request.getRequestURI());
                sendUnauthorizedError(response, "Invalid or expired token");
                return;
            }
        } catch (Exception e) {
            log.error("Authentication failed: {}", e.getMessage(), e);
            sendUnauthorizedError(response, "Authentication error: " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }
    
    private void sendUnauthorizedError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}

