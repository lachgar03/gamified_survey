package org.example.gamified_survey_app.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class RateLimitConfig {

    // Default limits
    private static final int PUBLIC_LIMIT_PER_MIN = 100;
    private static final int AUTH_LIMIT_PER_MIN = 500;
    private static final int LOGIN_LIMIT_PER_MIN = 10;

    private static final int PUBLIC_LIMIT_PER_HOUR = 1000;
    private static final int AUTH_LIMIT_PER_HOUR = 3000;
    private static final int LOGIN_LIMIT_PER_HOUR = 20;

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
                    String path = request.getRequestURI();
                    if (isStaticResource(path)) {
                        filterChain.doFilter(request, response);
                        return;
                    }

                    // Determine key type
                    String clientKey = resolveClientKey(request);
                    ApiRateLimit limit = determineRateLimit(path);

                    String minuteKey = clientKey + "_minute_" + path;
                    String hourKey = clientKey + "_hour_" + path;

                    int requestsPerMinute = rateLimitCacheMinute.getIfPresent(minuteKey) != null
                            ? rateLimitCacheMinute.getIfPresent(minuteKey) : 0;
                    int requestsPerHour = rateLimitCacheHour.getIfPresent(hourKey) != null
                            ? rateLimitCacheHour.getIfPresent(hourKey) : 0;

                    if (requestsPerMinute >= limit.perMinute) {
                        log.warn("Minute limit exceeded for {}: {}", clientKey, path);
                        reject(response, limit.perMinute, requestsPerMinute, "minute");
                        return;
                    }

                    if (requestsPerHour >= limit.perHour) {
                        log.warn("Hour limit exceeded for {}: {}", clientKey, path);
                        reject(response, limit.perHour, requestsPerHour, "hour");
                        return;
                    }

                    // Atomically increment request counts
                    rateLimitCacheMinute.asMap().merge(minuteKey, 1, Integer::sum);
                    rateLimitCacheHour.asMap().merge(hourKey, 1, Integer::sum);

                    // Set response headers
                    response.setHeader("X-RateLimit-Limit-Minute", String.valueOf(limit.perMinute));
                    response.setHeader("X-RateLimit-Remaining-Minute", String.valueOf(limit.perMinute - requestsPerMinute));
                    response.setHeader("X-RateLimit-Limit-Hour", String.valueOf(limit.perHour));
                    response.setHeader("X-RateLimit-Remaining-Hour", String.valueOf(limit.perHour - requestsPerHour));

                    filterChain.doFilter(request, response);
                } catch (Exception e) {
                    log.error("Rate limiting failed", e);
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Internal server error in rate limiter.\"}");
                }
            }

            private void reject(HttpServletResponse response, int limit, int current, String window) throws IOException {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write(String.format(
                        "{\"error\":\"Rate limit exceeded (%s window). Limit: %d, Used: %d\"}",
                        window, limit, current
                ));
            }

            private boolean isStaticResource(String path) {
                return path.startsWith("/css") || path.startsWith("/js") ||
                        path.startsWith("/images") || path.startsWith("/favicon.ico");
            }

            private String resolveClientKey(HttpServletRequest request) {
                // Use IP address by default, or add logic to use user ID if authenticated
                String xfHeader = request.getHeader("X-Forwarded-For");
                return (xfHeader != null) ? xfHeader.split(",")[0].trim() : request.getRemoteAddr();
            }

            private ApiRateLimit determineRateLimit(String path) {
                if (path.startsWith("/api/auth/login") || path.startsWith("/api/auth/register")) {
                    return new ApiRateLimit(LOGIN_LIMIT_PER_MIN, LOGIN_LIMIT_PER_HOUR);
                } else if (path.startsWith("/api/auth/")) {
                    return new ApiRateLimit(PUBLIC_LIMIT_PER_MIN, PUBLIC_LIMIT_PER_HOUR);
                } else {
                    return new ApiRateLimit(AUTH_LIMIT_PER_MIN, AUTH_LIMIT_PER_HOUR);
                }
            }

        };
    }

    private static class ApiRateLimit {
        final int perMinute;
        final int perHour;

        ApiRateLimit(int perMinute, int perHour) {
            this.perMinute = perMinute;
            this.perHour = perHour;
        }
    }
}
