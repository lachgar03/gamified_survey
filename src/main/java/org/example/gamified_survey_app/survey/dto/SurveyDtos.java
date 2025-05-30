package org.example.gamified_survey_app.survey.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.gamified_survey_app.survey.model.Question;
import org.example.gamified_survey_app.survey.model.QuestionResponse;

import java.time.LocalDateTime;
import java.util.List;

public class SurveyDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SurveyRequest {
        private String title;
        private String description;
        private Long categoryId;
        private LocalDateTime expiresAt;
        private boolean hasForum;
        private Integer xpReward;
        private Integer minimumTimeSeconds;
        private List<QuestionRequest> questions;
    }



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionRequest {
        private Long id;
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
        private Long id;
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
        private boolean hasForum;
        private boolean active;
        private boolean verified;
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
        private boolean hasForum;
        private boolean active;
        private boolean verified;
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
        private List<QuestionAnalytics> questionAnalytics; // <-- ADD THIS
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionAnalytics {
        private String text;
        private String type;
        private Long responseCount;
        private List<OptionAnalytics> optionAnalytics; // Only for SINGLE/MULTIPLE CHOICE
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionAnalytics {
        private String text;
        private Long count;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ForumRequest {
        private String title;
        private String description;
        private Long surveyId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ForumResponse {
        private Long id;
        private String title;
        private String description;
        private LocalDateTime createdAt;
        private Long surveyId;
        private String surveyTitle;
        private int subjectCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubjectRequest {
        private String title;
        private Long forumId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubjectResponse {
        private Long id;
        private String title;
        private LocalDateTime postedAt;
        private String creator;
        private Long creatorId;
        private Long forumId;
        private int commentCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentRequest {
        private String content;
        private Long subjectId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentResponse {
        private Long id;
        private String content;
        private LocalDateTime sentDate;
        private String creator;
        private Long creatorId;
        private Long subjectId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteCommentRequest {
        private Long forumId;
        private Long subjectId;
        private Long commentId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteSubjectRequest {
        private Long forumId;
        private Long subjectId;
    }



    }