package org.example.gamified_survey_app.config;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

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
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {
                try {
                    // Skip rate limiting for static resources
                    String path = request.getRequestURI();
                    if (path.startsWith("/css") || path.startsWith("/js") ||
                            path.startsWith("/images") || path.startsWith("/favicon.ico")) {
                        filterChain.doFilter(request, response);
                        return;
                    }

                    String clientIp = getClientIP(request);
                    String minuteKey = clientIp + "_minute";
                    String hourKey = clientIp + "_hour";

                    int requestsPerMinute = rateLimitCacheMinute.getIfPresent(minuteKey) != null
                            ? rateLimitCacheMinute.getIfPresent(minuteKey) : 0;
                    int requestsPerHour = rateLimitCacheHour.getIfPresent(hourKey) != null
                            ? rateLimitCacheHour.getIfPresent(hourKey) : 0;

                    if (requestsPerMinute >= MAX_REQUESTS_PER_MINUTE) {
                        log.warn("Rate limit exceeded for IP {}: {} requests per minute", clientIp, requestsPerMinute);
                        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\":\"Rate limit exceeded. Please try again later.\"}");
                        return;
                    }

                    if (requestsPerHour >= MAX_REQUESTS_PER_HOUR) {
                        log.warn("Rate limit exceeded for IP {}: {} requests per hour", clientIp, requestsPerHour);
                        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\":\"Hourly rate limit exceeded. Please try again later.\"}");
                        return;
                    }

                    // Increment counters
                    rateLimitCacheMinute.put(minuteKey, requestsPerMinute + 1);
                    rateLimitCacheHour.put(hourKey, requestsPerHour + 1);

                    filterChain.doFilter(request, response);
                } catch (Exception e) {
                    log.error("Error in rate limit filter", e);
                    // Respond with 500 if rate limiter itself fails
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Internal server error in rate limiter.\"}");
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
