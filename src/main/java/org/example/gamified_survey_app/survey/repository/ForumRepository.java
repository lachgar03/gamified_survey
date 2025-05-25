package org.example.gamified_survey_app.survey.repository;

import org.example.gamified_survey_app.survey.model.Forum;
import org.example.gamified_survey_app.survey.model.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForumRepository extends JpaRepository<Forum, Long> {
    Optional<Forum> findBySurvey(Survey survey);
    boolean existsBySurvey(Survey survey);
}
