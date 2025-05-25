package org.example.gamified_survey_app.survey.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailedSurveyResults {
    private Long id;
    private String title;
    private Integer totalResponses;
    private List<ResponseByDay> responsesByDay;
    private List<QuestionStat> questionStats;
    private DemographicData demographicData;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseByDay {
        private LocalDate date;
        private Long count;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionStat {
        private Long questionId;
        private String questionText;
        private String questionType;
        private ChartData chartData;
        private List<TextResponse> textResponses;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartData {
        private List<String> labels;
        private List<Dataset> datasets;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Dataset {
            private String label;
            private List<Integer> data;
            private List<String> backgroundColor;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TextResponse {
        private String response;
        private Integer count;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DemographicData {
        private List<DemographicGroup> ageGroups;
        private List<DemographicGroup> regionDistribution;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DemographicGroup {
        private String range; // For age groups: "18-24", "25-34", etc.
        private String region; // For regions
        private Integer count;
    }
}