package org.example.gamified_survey_app.gamification.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private Integer number;
    private Integer pointsThreshold;
    private String description;
    
    // Optional: Badge or icon URL for this level
    private String badgeUrl;
} 