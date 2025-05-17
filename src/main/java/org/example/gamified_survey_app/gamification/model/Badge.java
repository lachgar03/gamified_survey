package org.example.gamified_survey_app.gamification.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.gamified_survey_app.gamification.constant.ChallengePeriod;
import org.example.gamified_survey_app.gamification.constant.ChallengeType;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String imageUrl;

    // Example: 5 for "Complete 5 surveys"
    private Integer targetValue;

    // Type of action, e.g., "surveys_completed", "logins", "referrals"
    @Enumerated(EnumType.STRING)
    private ChallengeType actionType;

    // e.g., "Complete 5 surveys", "Log in 7 days in a row"
    private String achievementCondition;
    @Enumerated(EnumType.STRING)
    private ChallengePeriod period;

}
