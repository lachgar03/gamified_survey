package org.example.gamified_survey_app.gamification.controller;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.core.exception.ResourceNotFoundException;
import org.example.gamified_survey_app.gamification.dto.LevelDTO;
import org.example.gamified_survey_app.gamification.model.Level;
import org.example.gamified_survey_app.gamification.service.LevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/levels")
public class LevelController {

    private final LevelService levelService;

    @Autowired
    public LevelController(LevelService levelService) {
        this.levelService = levelService;
    }

    @GetMapping
    public ResponseEntity<List<LevelDTO>> getAllLevels() {
        List<LevelDTO> levels = levelService.getAllLevels().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(levels);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LevelDTO> getLevelById(@PathVariable Long id) {
        return levelService.getLevelById(id)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Level not found with id: " + id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LevelDTO> createLevel(@RequestBody LevelDTO levelDTO) {
        Level level = new Level();
        updateLevelFromDTO(level, levelDTO);
        
        Level savedLevel = levelService.createLevel(level);
        return ResponseEntity.ok(convertToDTO(savedLevel));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LevelDTO> updateLevel(@PathVariable Long id, @RequestBody LevelDTO levelDTO) {
        Level level = levelService.getLevelById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Level not found with id: " + id));
        
        updateLevelFromDTO(level, levelDTO);
        
        Level updatedLevel = levelService.updateLevel(id, level);
        return ResponseEntity.ok(convertToDTO(updatedLevel));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLevel(@PathVariable Long id) {
        levelService.deleteLevel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user")
    public ResponseEntity<LevelDTO> getUserLevel(@AuthenticationPrincipal AppUser user) {
        Level level = levelService.getUserLevel(user);
        return ResponseEntity.ok(convertToDTO(level));
    }

    private LevelDTO convertToDTO(Level level) {
        return new LevelDTO(
                level.getId(),
                level.getName(),
                level.getNumber(),
                level.getPointsThreshold(),
                level.getDescription(),
                level.getBadgeUrl()
        );
    }

    private void updateLevelFromDTO(Level level, LevelDTO dto) {
        level.setName(dto.getName());
        level.setNumber(dto.getNumber());
        level.setPointsThreshold(dto.getPointsThreshold());
        level.setDescription(dto.getDescription());
        level.setBadgeUrl(dto.getBadgeUrl());
    }
} 