package org.example.gamified_survey_app.gamification.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.gamified_survey_app.auth.model.AppUser;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer number;
    private Integer pointsThreshold;
    private String description;
    private String avatarUrl;

} 