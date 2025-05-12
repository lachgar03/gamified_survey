package org.example.gamified_survey_app.user.service;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.user.model.UserProfile;

public interface UserProfileService {
    
    /**
     * Gets a user's display name based on their profile information
     * If no profile exists, returns the user's email
     * 
     * @param user The user
     * @return The user's display name
     */
    String getUserDisplayName(AppUser user);
    
    /**
     * Gets a user's profile
     * 
     * @param user The user
     * @return The user's profile or null if none exists
     */
    UserProfile getUserProfile(AppUser user);
    
    /**
     * Creates or updates a user's profile
     * 
     * @param user The user
     * @param profile The profile data
     * @return The updated profile
     */
    UserProfile updateUserProfile(AppUser user, UserProfile profile);
} 