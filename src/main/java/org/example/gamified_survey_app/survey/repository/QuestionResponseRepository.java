package org.example.gamified_survey_app.survey.repository;

import org.example.gamified_survey_app.survey.model.QuestionResponse;
import org.example.gamified_survey_app.survey.model.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionResponseRepository extends JpaRepository<QuestionResponse, Long> {
    List<QuestionResponse> findBySurveyResponse(org.example.gamified_survey_app.survey.model.SurveyResponse surveyResponse);
    List<QuestionResponse> findAllByQuestion_Survey(Survey survey);
}
