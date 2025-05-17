package org.example.gamified_survey_app.survey.repository;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.survey.model.Survey;
import org.example.gamified_survey_app.survey.model.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {
    List<SurveyResponse> findByUser(AppUser user);
    List<SurveyResponse> findBySurvey(Survey survey);
    List<SurveyResponse> findByUserId(Long userId);
    SurveyResponse findBySurveyAndUser(Survey survey, AppUser user);



    @Query("SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.survey = ?1 AND sr.completedAt IS NOT NULL")
    Long countResponsesBySurvey(Survey survey);

    @Query("SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.survey = :survey AND sr.flaggedAsSuspicious = true")
    Long countFlaggedResponsesBySurvey(@Param("survey") Survey survey);

    @Query("SELECT AVG(sr.timeSpentSeconds) FROM SurveyResponse sr WHERE sr.survey = :survey")
    Double averageTimeSpentBySurvey(@Param("survey") Survey survey);

    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.user = :user AND FUNCTION('DATE', sr.completedAt) = :date")
    List<SurveyResponse> findByUserAndCompletedAtDate(@Param("user") AppUser user, @Param("date") LocalDate date);

    @Query("SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.user = :user AND FUNCTION('DATE', sr.completedAt) = :date")
    int countByUserAndDate(@Param("user") AppUser user, @Param("date") LocalDate date);


}
