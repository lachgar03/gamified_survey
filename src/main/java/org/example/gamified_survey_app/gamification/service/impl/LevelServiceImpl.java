package org.example.gamified_survey_app.gamification.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.gamification.constant.LeaderboardPeriod;
import org.example.gamified_survey_app.gamification.model.LeaderboardEntry;
import org.example.gamified_survey_app.gamification.model.Level;
import org.example.gamified_survey_app.gamification.repository.LeaderboardRepository;
import org.example.gamified_survey_app.gamification.repository.LevelRepository;
import org.example.gamified_survey_app.gamification.service.LevelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LevelServiceImpl implements LevelService {

    private static final Logger log = LoggerFactory.getLogger(LevelServiceImpl.class);
    private final LevelRepository levelRepository;

    @Override
    @Cacheable(value = "levels", key = "'all'")
    public List<Level> getAllLevels() {
        log.debug("Récupération de tous les niveaux");
        return levelRepository.findAll();
    }
    
    @Override
    @Cacheable(value = "levels", key = "#id")
    public Optional<Level> getLevelById(Long id) {
        log.debug("Récupération du niveau avec id: {}", id);
        return levelRepository.findById(id);
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "levels", allEntries = true)
    public Level createLevel(Level level) {
        log.info("Création d'un nouveau niveau: {}", level.getName());
        return levelRepository.save(level);
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "levels", allEntries = true)
    public Level updateLevel(Long id, Level levelDetails) {
        log.info("Mise à jour du niveau avec id: {}", id);
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
    @Transactional
    @CacheEvict(value = "levels", allEntries = true)
    public void deleteLevel(Long id) {
        log.info("Suppression du niveau avec id: {}", id);
        levelRepository.deleteById(id);
    }
    
    @Override
    @Cacheable(value = "levels", key = "'user_' + #user.id")
    public Level getUserLevel(AppUser user) {
        log.debug("Récupération du niveau pour l'utilisateur: {}", user.getEmail());
        return getUserLevel(user.getXp());
    }
    
    @Override
    @Cacheable(value = "levels", key = "'xp_' + #points")
    public Level getUserLevel(Integer points) {
        log.debug("Récupération du niveau pour {} points XP", points);
        return levelRepository.findFirstByPointsThresholdLessThanEqualOrderByPointsThresholdDesc(points)
                .orElseGet(() -> {
                    // If no matching level found, return the lowest level
                    log.warn("Aucun niveau trouvé pour {} points, retour au niveau minimal", points);
                    return levelRepository.findAll().stream()
                            .min((l1, l2) -> l1.getPointsThreshold().compareTo(l2.getPointsThreshold()))
                            .orElseThrow(() -> new RuntimeException("No levels defined in the system"));
                });
    }
    
    @Override
    public boolean hasUserLeveledUp(AppUser user, Integer oldPoints) {
        Level oldLevel = getUserLevel(oldPoints);
        Level newLevel = getUserLevel(user);
        
        boolean leveledUp = !oldLevel.getId().equals(newLevel.getId()) && 
               newLevel.getNumber() > oldLevel.getNumber();
        
        if (leveledUp) {
            log.info("L'utilisateur {} est passé du niveau {} au niveau {}", 
                     user.getEmail(), oldLevel.getNumber(), newLevel.getNumber());
        }
        
        return leveledUp;
    }
} 