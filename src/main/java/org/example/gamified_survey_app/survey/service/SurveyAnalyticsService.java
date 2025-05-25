package org.example.gamified_survey_app.survey.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.survey.dto.DetailedSurveyResults;
import org.example.gamified_survey_app.survey.model.Question;
import org.example.gamified_survey_app.survey.model.QuestionOption;
import org.example.gamified_survey_app.survey.model.Survey;
import org.example.gamified_survey_app.survey.repository.QuestionRepository;
import org.example.gamified_survey_app.survey.repository.QuestionResponseRepository;
import org.example.gamified_survey_app.survey.repository.SurveyRepository;
import org.example.gamified_survey_app.survey.repository.SurveyResponseRepository;
import org.example.gamified_survey_app.user.model.UserProfile;
import org.example.gamified_survey_app.user.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyAnalyticsService {

    private final SurveyRepository surveyRepository;
    private final SurveyResponseRepository surveyResponseRepository;
    private final QuestionResponseRepository questionResponseRepository;
    private final QuestionRepository questionRepository;
    private final UserProfileRepository userProfileRepository;

    @Transactional()
    public DetailedSurveyResults getDetailedSurveyResults(Long surveyId, String requesterEmail) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found"));

        // Verify requester is the creator
        if (!survey.getCreator().getEmail().equals(requesterEmail)) {
            throw new SecurityException("Only survey creator can access detailed results");
        }

        DetailedSurveyResults results = new DetailedSurveyResults();
        results.setId(survey.getId());
        results.setTitle(survey.getTitle());
        results.setTotalResponses(surveyResponseRepository.countBySurveyId(surveyId).intValue());

        // Get responses by day
        results.setResponsesByDay(surveyResponseRepository.countResponsesByDay(surveyId));

        // Get question statistics
        results.setQuestionStats(getQuestionStats(surveyId));

        // Get demographic data
        results.setDemographicData(getDemographicData(surveyId));

        return results;
    }

    private List<DetailedSurveyResults.QuestionStat> getQuestionStats(Long surveyId) {
        List<Question> questions = questionRepository.findBySurveyIdOrderByOrderIndex(surveyId);

        return questions.stream().map(question -> {
            DetailedSurveyResults.QuestionStat stat = new DetailedSurveyResults.QuestionStat();
            stat.setQuestionId(question.getId());
            stat.setQuestionText(question.getText());
            stat.setQuestionType(question.getType().name());

            if (question.getType() == Question.QuestionType.SINGLE_CHOICE ||
                    question.getType() == Question.QuestionType.MULTIPLE_CHOICE) {
                stat.setChartData(getChartDataForOptions(question));
            } else if (question.getType() == Question.QuestionType.TEXT) {
                stat.setTextResponses(getTextResponses(question.getId()));
            }

            return stat;
        }).collect(Collectors.toList());
    }

    private DetailedSurveyResults.ChartData getChartDataForOptions(Question question) {
        List<QuestionOption> options = question.getOptions();

        // Count responses for each option
        Map<Long, Long> optionCounts = questionResponseRepository
                .countByQuestionIdAndSelectedOptionIdIn(question.getId(),
                        options.stream().map(QuestionOption::getId).collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(
                        tuple -> ((Long) tuple[0]),  // First element is the option ID
                        tuple -> ((Long) tuple[1])   // Second element is the count
                ));

        List<String> labels = options.stream()
                .map(QuestionOption::getText)
                .collect(Collectors.toList());

        List<Integer> data = options.stream()
                .map(option -> optionCounts.getOrDefault(option.getId(), 0L).intValue())
                .collect(Collectors.toList());

        List<String> backgroundColors = generateColors(options.size());

        DetailedSurveyResults.ChartData.Dataset dataset = new DetailedSurveyResults.ChartData.Dataset();
        dataset.setLabel("Responses");
        dataset.setData(data);
        dataset.setBackgroundColor(backgroundColors);

        DetailedSurveyResults.ChartData chartData = new DetailedSurveyResults.ChartData();
        chartData.setLabels(labels);
        chartData.setDatasets(Collections.singletonList(dataset));

        return chartData;
    }

    private List<DetailedSurveyResults.TextResponse> getTextResponses(Long questionId) {
        return questionResponseRepository.findByQuestionIdAndTextResponseIsNotNull(questionId).stream()
                .collect(Collectors.groupingBy(
                        response -> response.getTextResponse().toLowerCase().trim(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .map(entry -> new DetailedSurveyResults.TextResponse(entry.getKey(), entry.getValue().intValue()))
                .sorted((a, b) -> b.getCount() - a.getCount())
                .collect(Collectors.toList());
    }

    private DetailedSurveyResults.DemographicData getDemographicData(Long surveyId) {
        DetailedSurveyResults.DemographicData data = new DetailedSurveyResults.DemographicData();
        data.setAgeGroups(getAgeGroupDistribution(surveyId));
        data.setRegionDistribution(getRegionDistribution(surveyId));
        return data;
    }

    private List<DetailedSurveyResults.DemographicGroup> getAgeGroupDistribution(Long surveyId) {
        List<AppUser> respondents = surveyResponseRepository.findDistinctUsersBySurveyId(surveyId);

        Map<String, Long> ageGroups = respondents.stream()
                .collect(Collectors.groupingBy(
                        user -> {
                            Optional<UserProfile> profile = userProfileRepository.findByUser(user);
                            if (profile == null || profile.get().getAge() == null) {
                                return "Unknown";
                            }
                            int age = profile.get().getAge();
                            if (age < 18) return "Under 18";
                            if (age < 25) return "18-24";
                            if (age < 35) return "25-34";
                            if (age < 45) return "35-44";
                            if (age < 55) return "45-54";
                            if (age < 65) return "55-64";
                            return "65+";
                        },
                        Collectors.counting()
                ));

        return ageGroups.entrySet().stream()
                .map(entry -> new DetailedSurveyResults.DemographicGroup(entry.getKey(), null, entry.getValue().intValue()))
                .sorted(Comparator.comparing(DetailedSurveyResults.DemographicGroup::getRange))
                .collect(Collectors.toList());
    }

    private int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    private List<DetailedSurveyResults.DemographicGroup> getRegionDistribution(Long surveyId) {
        List<AppUser> respondents = surveyResponseRepository.findDistinctUsersBySurveyId(surveyId);

        Map<String, Long> regions = respondents.stream()
                .collect(Collectors.groupingBy(
                        user -> {
                            Optional<UserProfile> profile = userProfileRepository.findByUser(user);
                            return (profile.isPresent() && profile.get().getRegion() != null) ? profile.get().getRegion() : "Unknown";
                        },
                        Collectors.counting()
                ));

        return regions.entrySet().stream()
                .map(entry -> new DetailedSurveyResults.DemographicGroup(null, entry.getKey(), entry.getValue().intValue()))
                .sorted((a, b) -> b.getCount() - a.getCount())
                .collect(Collectors.toList());
    }

    private List<String> generateColors(int count) {
        String[] palette = {
                "rgba(255, 99, 132, 0.6)",
                "rgba(54, 162, 235, 0.6)",
                "rgba(255, 206, 86, 0.6)",
                "rgba(75, 192, 192, 0.6)",
                "rgba(153, 102, 255, 0.6)",
                "rgba(255, 159, 64, 0.6)",
                "rgba(199, 199, 199, 0.6)",
                "rgba(83, 102, 255, 0.6)",
                "rgba(255, 99, 255, 0.6)"
        };

        List<String> colors = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            colors.add(palette[i % palette.length]);
        }
        return colors;
    }
}