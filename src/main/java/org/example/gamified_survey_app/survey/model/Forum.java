package org.example.gamified_survey_app.survey.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Forum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private LocalDateTime createdAt;
    private boolean enabled;

    @OneToOne
    @JoinColumn(name = "survey_id")
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