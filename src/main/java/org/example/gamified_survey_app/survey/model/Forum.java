package org.example.gamified_survey_app.survey.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded  = true)
public class Forum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String title;
    private String description;
    private LocalDateTime createdAt;
    private boolean enabled;

    @OneToOne
    @JoinColumn(name = "survey_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Survey survey;

    @OneToMany(mappedBy = "forum", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subject> subjects = new ArrayList<>();

    // Add methods to manage subjects
    public void addSubject(Subject subject) {
        subjects.add(subject);
        subject.setForum(this);
    }

    public void removeSubject(Subject subject) {
        subjects.remove(subject);
        subject.setForum(null);
    }
}