package org.example.gamified_survey_app.survey.repository;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.survey.model.Category;
import org.example.gamified_survey_app.survey.model.Survey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {
  List<Survey> findByDeletedFalseAndCreator(AppUser creator);
  Page<Survey> findByVerifiedIsFalse(Pageable pageable);
  Page<Survey> findByVerifiedIsTrue(Pageable pageable);


  Page<Survey> findByActiveTrue(Pageable pageable);
  List<Survey> findByDeletedFalse();
  Optional<Survey> findByIdAndDeletedFalse(Long id);

  List<Survey> findByCategory(Category category);

  @Query("SELECT s FROM Survey s WHERE s.active = true AND s.expiresAt > ?1 AND s.deleted = false AND s.verified = true ORDER BY s.createdAt DESC")
  Page<Survey> findActiveSurveys(LocalDateTime now, Pageable pageable);

  @Query("SELECT s FROM Survey s WHERE s.active = true AND s.deleted = false AND s.verified = true AND s.expiresAt > ?1  AND s.id NOT IN " +
          "(SELECT sr.survey.id FROM SurveyResponse sr WHERE sr.user = ?2) " +
          "ORDER BY s.createdAt DESC")
  Page<Survey> findAvailableSurveysForUser(LocalDateTime now, AppUser user, Pageable pageable);
}