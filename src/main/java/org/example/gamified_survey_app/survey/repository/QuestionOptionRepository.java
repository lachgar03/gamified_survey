package org.example.gamified_survey_app.survey.repository;

import org.example.gamified_survey_app.survey.model.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {
    List<QuestionOption> findByQuestionOrderByOrderIndexAsc(org.example.gamified_survey_app.survey.model.Question question);
}
