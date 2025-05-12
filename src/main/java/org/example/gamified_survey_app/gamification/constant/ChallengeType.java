package org.example.gamified_survey_app.gamification.constant;

public enum ChallengeType {
    // Survey-related challenges
    COMPLETE_SURVEYS,          // Complete a certain number of surveys
    COMPLETE_CATEGORY_SURVEYS, // Complete surveys in a specific category
    QUICK_RESPONSE,            // Complete surveys within a time limit
    
    // Forum-related challenges
    CREATE_COMMENTS,           // Create a certain number of comments
    GET_COMMENT_LIKES,         // Get likes on your comments
    
    // Referral-related challenges
    REFER_USERS,               // Refer a certain number of users
    
    // Streak-related challenges
    DAILY_LOGIN,               // Log in for consecutive days
    WEEKLY_PARTICIPATION       // Participate in surveys every week for X weeks
} 