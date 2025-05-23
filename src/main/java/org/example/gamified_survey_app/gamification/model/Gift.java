package org.example.gamified_survey_app.gamification.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Gift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    private String imageUrl;
    private Integer pointsCost;
    
    // Number of this gift available in inventory
    private Integer availableQuantity;
    
    // Boolean to determine if this gift is active
    private boolean active = true;
} 