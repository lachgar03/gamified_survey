package org.example.gamified_survey_app.core.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.auth.repository.UserRepository;
import org.example.gamified_survey_app.core.service.FraudDetectionService;
import org.example.gamified_survey_app.survey.model.QuestionResponse;
import org.example.gamified_survey_app.survey.model.Survey;
import org.example.gamified_survey_app.survey.model.SurveyResponse;
import org.example.gamified_survey_app.survey.repository.SurveyResponseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FraudDetectionServiceImpl implements FraudDetectionService {

    private static final Logger log = LoggerFactory.getLogger(FraudDetectionServiceImpl.class);
    private final SurveyResponseRepository surveyResponseRepository;
    private final UserRepository userRepository;
    
    // Cache to store user's last IP address, max 1000 entries, expire after 1 hour
    private final Cache<Long, String> userLastIpCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();
    
    // Cache to store user's recent activity timestamps, max 1000 entries, expire after 10 minutes
    private final Cache<Long, List<LocalDateTime>> userActivityCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    @Override
    public boolean isSuspiciousResponse(Survey survey, int timeSpentSeconds, AppUser user) {
        // Check if the time spent is below the minimum threshold
        if (timeSpentSeconds < survey.getMinimumTimeSeconds()) {
            log.warn("Suspicious response: Time spent below minimum threshold for user: {}, survey: {}", 
                    user.getEmail(), survey.getId());
            return true;
        }
        
        // Check if the user has suspicious activity rate
        if (isSuspiciousActivityRate(user)) {
            log.warn("Suspicious response: Suspicious activity rate for user: {}", user.getEmail());
            return true;
        }
        
        // Check average time spent on surveys of this type (if data is available)
        Double averageTimeSpent = surveyResponseRepository.averageTimeSpentBySurvey(survey);
        if (averageTimeSpent != null) {
            // If time spent is less than 30% of average, flag as suspicious
            if (timeSpentSeconds < (averageTimeSpent * 0.3)) {
                log.warn("Suspicious response: Time spent significantly below average for user: {}, survey: {}", 
                        user.getEmail(), survey.getId());
                return true;
            }
        }
        
        return false;
    }

    @Override
    public boolean analyzeResponsePatterns(SurveyResponse response) {
        List<QuestionResponse> questionResponses = response.getQuestionResponses();
        
        // Not enough responses to analyze
        if (questionResponses == null || questionResponses.size() < 3) {
            return false;
        }
        
        // Check for identical answers to all multiple choice questions
        long singleChoiceQuestions = questionResponses.stream()
                .filter(qr -> qr.getQuestion().getType().name().contains("CHOICE"))
                .count();
        
        if (singleChoiceQuestions >= 3) {
            // Check if user selected the same option number for all questions
            Map<Integer, Long> optionPositionCounts = questionResponses.stream()
                    .filter(qr -> qr.getQuestion().getType().name().contains("CHOICE") && 
                            qr.getSelectedOptions() != null && 
                            !qr.getSelectedOptions().isEmpty())
                    .collect(Collectors.groupingBy(
                            qr -> qr.getSelectedOptions().get(0).getOrderIndex(),
                            Collectors.counting()
                    ));
            
            // If the same position was selected for 80%+ of questions, flag as suspicious
            for (Long count : optionPositionCounts.values()) {
                if (count >= singleChoiceQuestions * 0.8) {
                    log.warn("Suspicious pattern: Same option position selected for majority of questions");
                    return true;
                }
            }
        }
        
        // Check for suspiciously fast text responses
        List<QuestionResponse> textResponses = questionResponses.stream()
                .filter(qr -> qr.getQuestion().getType().name().equals("TEXT") && 
                        qr.getTextResponse() != null && 
                        qr.getTextResponse().length() > 20)
                .collect(Collectors.toList());
        
        if (!textResponses.isEmpty()) {
            // Calculate average characters per second based on total time spent
            int totalChars = textResponses.stream()
                    .mapToInt(qr -> qr.getTextResponse().length())
                    .sum();
            
            double charsPerSecond = (double) totalChars / response.getTimeSpentSeconds();
            
            // If typing speed is unrealistically fast (more than 10 chars per second on average)
            if (charsPerSecond > 10) {
                log.warn("Suspicious pattern: Unrealistically fast typing detected ({} chars/sec)", charsPerSecond);
                return true;
            }
        }
        
        return false;
    }

    @Override
    public boolean isSuspiciousLocation(AppUser user, String ipAddress) {
        if (user == null || ipAddress == null) {
            return false;
        }
        
        String lastIp = userLastIpCache.getIfPresent(user.getId());
        
        // First time seeing this user, not suspicious
        if (lastIp == null) {
            userLastIpCache.put(user.getId(), ipAddress);
            return false;
        }
        
        // Check if IP changed since last activity
        if (!lastIp.equals(ipAddress)) {
            // Update the last IP
            userLastIpCache.put(user.getId(), ipAddress);
            
            // In a real implementation, we would use a geolocation service
            // to check if the IP change represents an impossible travel time
            // For now, we'll just consider any IP change as non-suspicious
            return false;
        }
        
        return false;
    }

    @Override
    public boolean isSuspiciousActivityRate(AppUser user) {
        if (user == null) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        List<LocalDateTime> activities = userActivityCache.get(user.getId(), k -> 
                java.util.Collections.synchronizedList(new java.util.ArrayList<>()));
        
        // Add current activity
        activities.add(now);
        
        // Remove activities older than 5 minutes
        activities.removeIf(time -> Duration.between(time, now).toMinutes() > 5);
        
        // Update cache with cleaned list
        userActivityCache.put(user.getId(), activities);
        
        // If more than 20 activities in the last 5 minutes, consider suspicious
        if (activities.size() > 20) {
            log.warn("Suspicious activity rate detected for user: {}", user.getEmail());
            return true;
        }
        
        return false;
    }

    @Override
    public void flagUser(AppUser user, String reason) {
        log.warn("User {} flagged for suspicious activity: {}", user.getEmail(), reason);
        // In a real implementation, we would log this to a database for admin review
        
        // We could also trigger an email alert to administrators
    }

    @Override
    public void banUser(AppUser user, String reason, Integer durationDays) {
        user.setBanned(true);
        user.setBanReason(reason);
        user.setBannedAt(LocalDateTime.now());
        
        if (durationDays != null) {
            user.setBanExpiresAt(LocalDateTime.now().plusDays(durationDays));
            log.info("User {} banned for {} days. Reason: {}", user.getEmail(), durationDays, reason);
        } else {
            // Permanent ban
            user.setBanExpiresAt(null);
            log.info("User {} banned permanently. Reason: {}", user.getEmail(), reason);
        }
        
        userRepository.save(user);
    }
} 