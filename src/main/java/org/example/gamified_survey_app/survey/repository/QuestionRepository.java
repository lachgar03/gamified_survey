package org.example.gamified_survey_app.survey.repository;

import org.example.gamified_survey_app.survey.model.Question;
import org.example.gamified_survey_app.survey.model.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findBySurveyOrderByOrderIndexAsc(Survey survey);
    List<Question> findBySurveyId(Long surveyId);
    List<Question> findBySurveyIdOrderByOrderIndex(Long surveyId);
}
