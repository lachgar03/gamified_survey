package org.example.gamified_survey_app.core.service;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.survey.model.Survey;
import org.example.gamified_survey_app.survey.model.SurveyResponse;

public interface FraudDetectionService {
    /**
     * Checks if a survey response is suspicious
     * 
     * @param survey The survey
     * @param timeSpentSeconds The time spent on the survey in seconds
     * @param user The user who submitted the response
     * @return True if the response is suspicious, false otherwise
     */
    boolean isSuspiciousResponse(Survey survey, int timeSpentSeconds, AppUser user);
    
    /**
     * Analyzes a completed response for suspicious patterns
     * 
     * @param response The survey response to analyze
     * @return True if the response has suspicious patterns, false otherwise
     */
    boolean analyzeResponsePatterns(SurveyResponse response);
    
    /**
     * Checks for suspicious IP address or geolocation changes for a user
     * 
     * @param user The user
     * @param ipAddress The current IP address
     * @return True if the IP address or geolocation is suspicious, false otherwise
     */
    boolean isSuspiciousLocation(AppUser user, String ipAddress);
    
    /**
     * Checks for suspicious activity rate (too many actions in short time)
     * 
     * @param user The user
     * @return True if the activity rate is suspicious, false otherwise
     */
    boolean isSuspiciousActivityRate(AppUser user);
    
    /**
     * Flags a user for suspicious activity
     * 
     * @param user The user to flag
     * @param reason The reason for flagging
     */
    void flagUser(AppUser user, String reason);
    
    /**
     * Bans a user for confirmed fraud
     * 
     * @param user The user to ban
     * @param reason The reason for the ban
     * @param durationDays The duration of the ban in days (null for permanent)
     */
    void banUser(AppUser user, String reason, Integer durationDays);
} 