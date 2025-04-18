package org.example.gamified_survey_app.survey.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.gamified_survey_app.survey.model.Question;

import java.time.LocalDateTime;
import java.util.List;

public class SurveyDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SurveyRequest {
        private String title;
        private String description;
        private LocalDateTime expiresAt;
        private Long categoryId;
        private Integer xpReward;
        private Integer minimumTimeSeconds;
        private List<QuestionRequest> questions;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionRequest {
        private String text;
        private Integer orderIndex;
        private Question.QuestionType type;
        private boolean required;
        private List<QuestionOptionRequest> options;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionOptionRequest {
        private String text;
        private Integer orderIndex;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SurveyResponse {
        private Long id;
        private String title;
        private String description;
        private LocalDateTime createdAt;
        private LocalDateTime expiresAt;
        private boolean active;
        private String creatorEmail;
        private String categoryName;
        private Integer xpReward;
        private Long responseCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SurveyDetailResponse {
        private Long id;
        private String title;
        private String description;
        private LocalDateTime createdAt;
        private LocalDateTime expiresAt;
        private boolean active;
        private String creatorEmail;
        private String categoryName;
        private Integer xpReward;
        private Integer minimumTimeSeconds;
        private List<QuestionResponse> questions;
        private Long responseCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionResponse {
        private Long id;
        private String text;
        private Integer orderIndex;
        private Question.QuestionType type;
        private boolean required;
        private List<QuestionOptionResponse> options;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionOptionResponse {
        private Long id;
        private String text;
        private Integer orderIndex;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryRequest {
        private String name;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryResponse {
        private Long id;
        private String name;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SurveySubmissionRequest {
        private Long surveyId;
        private LocalDateTime startedAt;
        private LocalDateTime completedAt;
        private List<QuestionResponseRequest> responses;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionResponseRequest {
        private Long questionId;
        private String textResponse;
        private List<Long> selectedOptionIds;
        private Integer ratingValue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SurveyResponseSummary {
        private Long id;
        private String surveyTitle;
        private LocalDateTime completedAt;
        private Integer xpAwarded;
        private boolean flaggedAsSuspicious;
        private Integer timeSpentSeconds;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SurveyResultStats {
        private Long surveyId;
        private String surveyTitle;
        private Long totalResponses;
        private Long suspiciousResponses;
        private Double averageTimeSpentSeconds;
    }

}