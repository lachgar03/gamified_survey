package org.example.gamified_survey_app.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
    
    @Bean
    public Filter securityHeadersFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                    FilterChain filterChain) throws ServletException, IOException {
                // Content Security Policy
                response.setHeader("Content-Security-Policy", 
                        "default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                        "style-src 'self' 'unsafe-inline'; img-src 'self' data:; " +
                        "font-src 'self'; connect-src 'self'");
                
                // Prevent clickjacking
                response.setHeader("X-Frame-Options", "DENY");
                
                // XSS Protection (modern browsers ignore this, but it's still good practice)
                response.setHeader("X-XSS-Protection", "1; mode=block");
                
                // Disable MIME type sniffing
                response.setHeader("X-Content-Type-Options", "nosniff");
                
                // Referrer Policy
                response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
                
                // Permissions Policy
                response.setHeader("Permissions-Policy", "camera=(), microphone=(), geolocation=()");
                
                // HSTS - Strict Transport Security
                response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
                
                filterChain.doFilter(request, response);
            }
        };
    }
}
