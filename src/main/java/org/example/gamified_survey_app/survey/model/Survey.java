package org.example.gamified_survey_app.survey.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.gamified_survey_app.auth.model.AppUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Survey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean active = true;
    private boolean hasForum;
    private boolean verified = false;
    private boolean deleted = false;
    private LocalDateTime deletedAt;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private AppUser creator;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();
    @OneToOne(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Forum forum;
    private Integer xpReward = 10;

    private Integer minimumTimeSeconds = 60;
    

    
    // Method to enable forum
    public void enableForum(String title, String description) {
        if (this.forum == null) {
            Forum newForum = new Forum();
            newForum.setTitle(title);
            newForum.setDescription(description);
            newForum.setCreatedAt(LocalDateTime.now());
            newForum.setSurvey(this);
            this.forum = newForum;
            this.hasForum = true;
        }
    }

    // Method to disable forum
    public void disableForum() {
        this.forum = null;
        this.hasForum = false;
    }
}