package org.example.gamified_survey_app.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor

public class AvatarConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String top;
    private String accessories;
    private String hairColor;
    private String eyes;
    private String skin;
    private String accessoriesColor;
    private String facialHair;
    private String facialHairColor;
    private String clothing;
    private String clothesColor;
    private String eyebrows;
    private String mouth;

    @OneToOne(mappedBy = "avatarConfig")
    private UserProfile userProfile;
}
