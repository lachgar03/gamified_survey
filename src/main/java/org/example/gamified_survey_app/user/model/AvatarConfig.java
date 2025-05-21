package org.example.gamified_survey_app.user.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class AvatarConfig {
    private String hair;
    private String hairColor;
    private String accessory;
    private String eyeType;
    private String skinColor;

    // Getters and setters
}