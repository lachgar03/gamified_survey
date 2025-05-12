package org.example.gamified_survey_app.config;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RateLimitConfig {

    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private static final int MAX_REQUESTS_PER_HOUR = 1000;

    @Bean
    public Cache<String, Integer> rateLimitCacheMinute() {
        return Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .maximumSize(10000)
                .build();
    }
    
    @Bean
    public Cache<String, Integer> rateLimitCacheHour() {
        return Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .maximumSize(10000)
                .build();
    }
    
    @Bean
    public OncePerRequestFilter rateLimitFilter(Cache<String, Integer> rateLimitCacheMinute,
                                               Cache<String, Integer> rateLimitCacheHour) {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                    FilterChain filterChain) {
                try {
                    // Skip rate limiting for static resources
                    String path = request.getRequestURI();
                    if (path.startsWith("/css") || path.startsWith("/js") || path.startsWith("/images") 
                            || path.startsWith("/favicon.ico")) {
                        filterChain.doFilter(request, response);
                        return;
                    }
                    
                    // Get client IP or use X-Forwarded-For if available
                    String clientIp = getClientIP(request);
                    
                    // Create cache keys
                    String minuteKey = clientIp + "_minute";
                    String hourKey = clientIp + "_hour";
                    
                    // Get and increment counters
                    Integer requestsPerMinute = rateLimitCacheMinute.get(minuteKey, k -> 0);
                    Integer requestsPerHour = rateLimitCacheHour.get(hourKey, k -> 0);
                    
                    if (requestsPerMinute >= MAX_REQUESTS_PER_MINUTE) {
                        log.warn("Rate limit exceeded for IP {}: {} requests per minute", clientIp, requestsPerMinute);
                        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                        response.getWriter().write("{\"error\":\"Rate limit exceeded. Please try again later.\"}");
                        response.setContentType("application/json");
                        return;
                    }
                    
                    if (requestsPerHour >= MAX_REQUESTS_PER_HOUR) {
                        log.warn("Rate limit exceeded for IP {}: {} requests per hour", clientIp, requestsPerHour);
                        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                        response.getWriter().write("{\"error\":\"Hourly rate limit exceeded. Please try again later.\"}");
                        response.setContentType("application/json");
                        return;
                    }
                    
                    // Increment counters
                    rateLimitCacheMinute.put(minuteKey, requestsPerMinute + 1);
                    rateLimitCacheHour.put(hourKey, requestsPerHour + 1);
                    
                    // Continue with request
                    filterChain.doFilter(request, response);
                } catch (Exception e) {
                    log.error("Error in rate limit filter", e);
                    try {
                        filterChain.doFilter(request, response);
                    } catch (Exception ex) {
                        log.error("Error continuing filter chain", ex);
                    }
                }
            }
            
            private String getClientIP(HttpServletRequest request) {
                String xfHeader = request.getHeader("X-Forwarded-For");
                if (xfHeader == null) {
                    return request.getRemoteAddr();
                }
                return xfHeader.split(",")[0].trim();
            }
        };
    }
} 