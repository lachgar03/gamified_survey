package org.example.gamified_survey_app.gamification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GiftDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Integer pointsCost;
    private Integer availableQuantity;
    private boolean active;
} 