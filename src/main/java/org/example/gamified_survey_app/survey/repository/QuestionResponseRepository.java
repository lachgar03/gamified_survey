package org.example.gamified_survey_app.survey.repository;

import org.example.gamified_survey_app.survey.model.QuestionResponse;
import org.example.gamified_survey_app.survey.model.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionResponseRepository extends JpaRepository<QuestionResponse, Long> {
    List<QuestionResponse> findBySurveyResponse(org.example.gamified_survey_app.survey.model.SurveyResponse surveyResponse);
    List<QuestionResponse> findAllByQuestion_Survey(Survey survey);
    @Query("SELECT qo.id, COUNT(qr) " +
            "FROM QuestionResponse qr " +
            "JOIN qr.selectedOptions qo " +
            "WHERE qr.question.id = :questionId AND qo.id IN :optionIds " +
            "GROUP BY qo.id")
    List<Object[]> countByQuestionIdAndSelectedOptionIdIn(@Param("questionId") Long questionId,
                                                          @Param("optionIds") List<Long> optionIds);

    List<QuestionResponse> findByQuestionIdAndTextResponseIsNotNull(Long questionId);

}
