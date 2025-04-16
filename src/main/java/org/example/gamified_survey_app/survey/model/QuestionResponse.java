package org.example.gamified_survey_app.survey.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "survey_response_id")
    private SurveyResponse surveyResponse;

    // For text responses
    @Column(length = 1000)
    private String textResponse;

    // For single/multiple choice responses
    @ManyToMany
    @JoinTable(
            name = "question_response_options",
            joinColumns = @JoinColumn(name = "question_response_id"),
            inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private List<QuestionOption> selectedOptions = new ArrayList<>();

    // For rating responses
    private Integer ratingValue;
}