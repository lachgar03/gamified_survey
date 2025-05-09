package org.example.gamified_survey_app.gamification.service.impl;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.gamification.model.Level;
import org.example.gamified_survey_app.gamification.repository.LevelRepository;
import org.example.gamified_survey_app.gamification.service.LevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LevelServiceImpl implements LevelService {
    
    private final LevelRepository levelRepository;
    
    @Autowired
    public LevelServiceImpl(LevelRepository levelRepository) {
        this.levelRepository = levelRepository;
    }
    
    @Override
    public List<Level> getAllLevels() {
        return levelRepository.findAll();
    }
    
    @Override
    public Optional<Level> getLevelById(Long id) {
        return levelRepository.findById(id);
    }
    
    @Override
    public Level createLevel(Level level) {
        return levelRepository.save(level);
    }
    
    @Override
    public Level updateLevel(Long id, Level levelDetails) {
        Level level = levelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Level not found with id: " + id));
        
        level.setName(levelDetails.getName());
        level.setNumber(levelDetails.getNumber());
        level.setPointsThreshold(levelDetails.getPointsThreshold());
        level.setDescription(levelDetails.getDescription());
        level.setBadgeUrl(levelDetails.getBadgeUrl());
        
        return levelRepository.save(level);
    }
    
    @Override
    public void deleteLevel(Long id) {
        levelRepository.deleteById(id);
    }
    
    @Override
    public Level getUserLevel(AppUser user) {
        return getUserLevel(user.getXp());
    }
    
    @Override
    public Level getUserLevel(Integer points) {
        return levelRepository.findFirstByPointsThresholdLessThanEqualOrderByPointsThresholdDesc(points)
                .orElseGet(() -> {
                    // If no matching level found, return the lowest level
                    return levelRepository.findAll().stream()
                            .min((l1, l2) -> l1.getPointsThreshold().compareTo(l2.getPointsThreshold()))
                            .orElseThrow(() -> new RuntimeException("No levels defined in the system"));
                });
    }
    
    @Override
    public boolean hasUserLeveledUp(AppUser user, Integer oldPoints) {
        Level oldLevel = getUserLevel(oldPoints);
        Level newLevel = getUserLevel(user);
        
        return !oldLevel.getId().equals(newLevel.getId()) && 
               newLevel.getNumber() > oldLevel.getNumber();
    }
} 