package org.example.gamified_survey_app.survey.repository;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.survey.model.Survey;
import org.example.gamified_survey_app.survey.model.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {
    List<SurveyResponse> findBySurvey(Survey survey);

    List<org.example.gamified_survey_app.survey.model.SurveyResponse> findByUser(AppUser user);

    boolean existsBySurveyAndUser(Survey survey, AppUser user);

    @Query("SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.survey = ?1")
    Long countResponsesBySurvey(Survey survey);

    @Query("SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.survey = :survey AND sr.flaggedAsSuspicious = true")
    Long countFlaggedResponsesBySurvey(@Param("survey") Survey survey);

    @Query("SELECT AVG(sr.timeSpentSeconds) FROM SurveyResponse sr WHERE sr.survey = :survey")
    Double averageTimeSpentBySurvey(@Param("survey") Survey survey);

}
