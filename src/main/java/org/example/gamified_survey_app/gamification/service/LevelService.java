package org.example.gamified_survey_app.gamification.service;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.gamification.model.Level;

import java.util.List;
import java.util.Optional;

public interface LevelService {
    List<Level> getAllLevels();
    
    Optional<Level> getLevelById(Long id);
    
    Level createLevel(Level level);
    
    Level updateLevel(Long id, Level levelDetails);
    
    void deleteLevel(Long id);
    
    Level getUserLevel(AppUser user);
    
    Level getUserLevel(Integer points);
    
    boolean hasUserLeveledUp(AppUser user, Integer oldPoints);
} 