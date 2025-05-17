package org.example.gamified_survey_app.survey.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.auth.repository.UserRepository;
import org.example.gamified_survey_app.core.constants.Roles;
import org.example.gamified_survey_app.core.exception.CustomException;
import org.example.gamified_survey_app.core.service.FraudDetectionService;
import org.example.gamified_survey_app.gamification.service.LeaderboardService;
import org.example.gamified_survey_app.gamification.service.UserXpService;
import org.example.gamified_survey_app.survey.dto.SurveyDtos;
import org.example.gamified_survey_app.survey.model.Category;
import org.example.gamified_survey_app.survey.model.Question;
import org.example.gamified_survey_app.survey.model.QuestionOption;
import org.example.gamified_survey_app.survey.model.QuestionResponse;
import org.example.gamified_survey_app.survey.model.Survey;
import org.example.gamified_survey_app.survey.model.SurveyResponse;
import org.example.gamified_survey_app.survey.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository optionRepository;
    private final SurveyResponseRepository surveyResponseRepository;
    private final QuestionResponseRepository questionResponseRepository;
    private final UserRepository userRepository;
    private final LeaderboardService leaderboardService;
    private final FraudDetectionService fraudDetectionService;
    private final UserXpService userXpService;

    @Transactional
    public SurveyDtos.SurveyResponse createSurvey(SurveyDtos.SurveyRequest request) {
        AppUser creator = getCurrentUser();

        if (!(creator.getRole() == Roles.CREATOR || creator.getRole() == Roles.ADMIN)) {
            throw new CustomException("Only survey creators can create surveys");
        }



        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CustomException("Category not found"));

        Survey survey = new Survey();
        survey.setTitle(request.getTitle());
        survey.setDescription(request.getDescription());
        survey.setCreatedAt(LocalDateTime.now());
        survey.setExpiresAt(request.getExpiresAt());
        survey.setCreator(creator);
        survey.setCategory(category);
        survey.setXpReward(request.getXpReward());
        survey.setMinimumTimeSeconds(request.getMinimumTimeSeconds());
        survey.setActive(true);
        survey.setVerified(false);

        Survey savedSurvey = surveyRepository.save(survey);

        if (request.getQuestions() != null) {
            for (SurveyDtos.QuestionRequest questionRequest : request.getQuestions()) {
                Question question = new Question();
                question.setText(questionRequest.getText());
                question.setOrderIndex(questionRequest.getOrderIndex());
                question.setType(questionRequest.getType());
                question.setRequired(questionRequest.isRequired());
                question.setSurvey(savedSurvey);

                Question savedQuestion = questionRepository.save(question);

                if ((questionRequest.getType() == Question.QuestionType.SINGLE_CHOICE
                        || questionRequest.getType() == Question.QuestionType.MULTIPLE_CHOICE)
                        && questionRequest.getOptions() != null) {

                    for (SurveyDtos.QuestionOptionRequest optionRequest : questionRequest.getOptions()) {
                        QuestionOption option = new QuestionOption();
                        option.setText(optionRequest.getText());
                        option.setOrderIndex(optionRequest.getOrderIndex());
                        option.setQuestion(savedQuestion);

                        optionRepository.save(option);
                    }
                }
            }
        }

        return mapToSurveyResponse(savedSurvey);
    }

    public SurveyDtos.SurveyDetailResponse getSurveyById(Long id) {
        AppUser currentUser = getCurrentUser();
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new CustomException("Survey not found"));
        if (!survey.isActive() || survey.getExpiresAt().isBefore(LocalDateTime.now()) ) {
            throw new CustomException("This survey is no longer active");
        }
        if (survey.getCreator().getId().equals(currentUser.getId())) {
            throw new CustomException("can t participate in your own survey");
        }
        SurveyResponse surveyResponse = surveyResponseRepository.findBySurveyAndUser(survey, currentUser);
       if (surveyResponse.getCompletedAt() != null) {
           throw new CustomException("You can't participate many times");
       }

        if (surveyResponseRepository.findBySurveyAndUser(survey, currentUser) == null) {
            SurveyResponse response = new SurveyResponse();
            response.setSurvey(survey);
            response.setUser(currentUser);
            response.setStartedAt(LocalDateTime.now());
            surveyResponseRepository.save(response);
        }
        return mapToSurveyDetailResponse(survey);
    }

    public Page<SurveyDtos.SurveyResponse> getActiveSurveys(Pageable pageable) {
        Page<Survey> surveys = surveyRepository.findActiveSurveys(LocalDateTime.now(), pageable);
        return surveys.map(this::mapToSurveyResponse);
    }

    public Page<SurveyDtos.SurveyResponse> getAvailableSurveysForCurrentUser(Pageable pageable) {
        AppUser currentUser = getCurrentUser();
        Page<Survey> surveys = surveyRepository.findAvailableSurveysForUser(LocalDateTime.now(), currentUser, pageable);
        return surveys.map(this::mapToSurveyResponse);
    }

    public List<SurveyDtos.SurveyResponse> getSurveysByCreator() {
        AppUser creator = getCurrentUser();
        List<Survey> surveys = surveyRepository.findByCreator(creator);
        return surveys.stream().map(this::mapToSurveyResponse).collect(Collectors.toList());
    }

    @Transactional
    public void deactivateSurvey(Long id) {
        AppUser currentUser = getCurrentUser();
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new CustomException("Survey not found"));

        if (!survey.getCreator().getId().equals(currentUser.getId())
                && currentUser.getRole() != Roles.ADMIN) {
            throw new CustomException("You don't have permission to deactivate this survey");
        }

        survey.setActive(false);
        surveyRepository.save(survey);
    }

    @Transactional
    public SurveyDtos.SurveyResponseSummary submitSurveyResponse(SurveyDtos.SurveySubmissionRequest request) {
        AppUser currentUser = getCurrentUser();
        Survey survey = surveyRepository.findById(request.getSurveyId())
                .orElseThrow(() -> new CustomException("Survey not found"));
        SurveyResponse surveyResponse = surveyResponseRepository.findBySurveyAndUser(survey, currentUser);


        if (!survey.isActive() || survey.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomException("This survey is no longer active");
        }

        if (surveyResponse.getCompletedAt() != null) {
            throw new CustomException("You have already completed this survey");
        }


        Duration timeSpent = Duration.between(surveyResponse.getStartedAt(), LocalDateTime.now());
        int timeSpentSeconds = (int) timeSpent.getSeconds();
        
        // Use fraud detection service to check for suspicious responses
        boolean isSuspicious = fraudDetectionService.isSuspiciousResponse(
            survey, timeSpentSeconds, currentUser);

        int xpAwarded = isSuspicious ? 0 : survey.getXpReward();

        surveyResponse.setCompletedAt(request.getCompletedAt());
        surveyResponse.setTimeSpentSeconds(timeSpentSeconds);
        surveyResponse.setFlaggedAsSuspicious(isSuspicious);
        surveyResponse.setXpAwarded(xpAwarded);

        SurveyResponse savedResponse = surveyResponseRepository.save(surveyResponse);

        if (request.getResponses() != null) {
            for (SurveyDtos.QuestionResponseRequest questionResponseRequest : request.getResponses()) {
                Question question = questionRepository.findById(questionResponseRequest.getQuestionId())
                        .orElseThrow(() -> new CustomException("Question not found"));

                if (!question.getSurvey().getId().equals(survey.getId())) {
                    throw new CustomException("Question does not belong to this survey");
                }

                QuestionResponse questionResponse = new QuestionResponse();
                questionResponse.setQuestion(question);
                questionResponse.setSurveyResponse(savedResponse);

                switch (question.getType()) {
                    case TEXT:
                        questionResponse.setTextResponse(questionResponseRequest.getTextResponse());
                        break;
                    case SINGLE_CHOICE:
                    case MULTIPLE_CHOICE:
                        List<QuestionOption> selectedOptions = new ArrayList<>();
                        if (questionResponseRequest.getSelectedOptionIds() != null) {
                            for (Long optionId : questionResponseRequest.getSelectedOptionIds()) {
                                QuestionOption option = optionRepository.findById(optionId)
                                        .orElseThrow(() -> new CustomException("Option not found"));
                                selectedOptions.add(option);
                            }
                        }
                        questionResponse.setSelectedOptions(selectedOptions);
                        break;
                    case RATING:
                        questionResponse.setRatingValue(questionResponseRequest.getRatingValue());
                        break;
                }

                questionResponseRepository.save(questionResponse);
            }
        }

        // Perform deeper pattern analysis after responses are saved
        if (!isSuspicious && fraudDetectionService.analyzeResponsePatterns(savedResponse)) {
            // If suspicious patterns detected in response analysis
            savedResponse.setFlaggedAsSuspicious(true);
            savedResponse.setXpAwarded(0);
            surveyResponseRepository.save(savedResponse);
            isSuspicious = true;
            xpAwarded = 0;
            
            // Flag user for suspicious activity
            fraudDetectionService.flagUser(currentUser, 
                "Suspicious response patterns detected in survey ID: " + survey.getId());
        }
        // Update user's XP
        if (xpAwarded > 0) {
            userXpService.updateUserXp(currentUser, xpAwarded);
        }



        return new SurveyDtos.SurveyResponseSummary(
                savedResponse.getId(),
                survey.getTitle(),
                savedResponse.getCompletedAt(),
                xpAwarded,
                isSuspicious,
                timeSpentSeconds
        );
    }



    private AppUser getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new CustomException("User not found"));
    }

    private SurveyDtos.SurveyResponse mapToSurveyResponse(Survey survey) {
        Long responseCount = surveyResponseRepository.countResponsesBySurvey(survey);

        return new SurveyDtos.SurveyResponse(
                survey.getId(),
                survey.getTitle(),
                survey.getDescription(),
                survey.getCreatedAt(),
                survey.getExpiresAt(),
                survey.isActive(),
                survey.isVerified(),
                survey.getCreator().getEmail(),
                survey.getCategory().getName(),
                survey.getXpReward(),
                responseCount
        );
    }

    private SurveyDtos.SurveyDetailResponse mapToSurveyDetailResponse(Survey survey) {
        List<Question> questions = questionRepository.findBySurveyOrderByOrderIndexAsc(survey);
        List<SurveyDtos.QuestionResponse> questionResponses = new ArrayList<>();

        for (Question question : questions) {
            List<QuestionOption> options = optionRepository.findByQuestionOrderByOrderIndexAsc(question);
            List<SurveyDtos.QuestionOptionResponse> optionResponses = options.stream()
                    .map(option -> new SurveyDtos.QuestionOptionResponse(
                            option.getId(),
                            option.getText(),
                            option.getOrderIndex()
                    ))
                    .collect(Collectors.toList());

            questionResponses.add(new SurveyDtos.QuestionResponse(
                    question.getId(),
                    question.getText(),
                    question.getOrderIndex(),
                    question.getType(),
                    question.isRequired(),
                    optionResponses
            ));
        }

        Long responseCount = surveyResponseRepository.countResponsesBySurvey(survey);

        return new SurveyDtos.SurveyDetailResponse(
                survey.getId(),
                survey.getTitle(),
                survey.getDescription(),
                survey.getCreatedAt(),
                survey.getExpiresAt(),
                survey.isActive(),
                survey.isVerified(),
                survey.getCreator().getEmail(),
                survey.getCategory().getName(),
                survey.getXpReward(),
                survey.getMinimumTimeSeconds(),
                questionResponses,
                responseCount
        );
    }

    public Page<SurveyDtos.SurveyResponse> getAllSurveys(Pageable pageable) {
        Page<Survey> surveys = surveyRepository.findAll(pageable);
        return surveys.map(this::mapToSurveyResponse);
    }

    @Transactional
    public void deleteSurvey(Long id) {
        AppUser currentUser = getCurrentUser();
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new CustomException("Survey not found"));

        if (!survey.getCreator().getId().equals(currentUser.getId())
                && currentUser.getRole() != Roles.ADMIN) {
            throw new CustomException("You don't have permission to delete this survey");
        }

        surveyRepository.delete(survey);
    }

    @Transactional
    public SurveyDtos.SurveyResponse updateSurvey(Long id, SurveyDtos.SurveyRequest updatedSurvey) {
        AppUser currentUser = getCurrentUser();
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new CustomException("Survey not found"));

        if (!survey.getCreator().getId().equals(currentUser.getId())
                && currentUser.getRole() != Roles.ADMIN) {
            throw new CustomException("You don't have permission to update this survey");
        }

        survey.setTitle(updatedSurvey.getTitle());
        survey.setDescription(updatedSurvey.getDescription());
        survey.setExpiresAt(updatedSurvey.getExpiresAt());
        survey.setXpReward(updatedSurvey.getXpReward());
        survey.setMinimumTimeSeconds(updatedSurvey.getMinimumTimeSeconds());

        Category category = categoryRepository.findById(updatedSurvey.getCategoryId())
                .orElseThrow(() -> new CustomException("Category not found"));
        survey.setCategory(category);

        return mapToSurveyResponse(surveyRepository.save(survey));
    }

    public SurveyDtos.SurveyResultStats getSurveyResults(Long id) {
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new CustomException("Survey not found"));

        Long totalResponses = surveyResponseRepository.countResponsesBySurvey(survey);
        Long flaggedResponses = surveyResponseRepository.countFlaggedResponsesBySurvey(survey);
        Double averageTimeSpent = surveyResponseRepository.averageTimeSpentBySurvey(survey);

        return new SurveyDtos.SurveyResultStats(
                survey.getId(),
                survey.getTitle(),
                totalResponses,
                flaggedResponses,
                averageTimeSpent
        );
    }
    public List<SurveyDtos.SurveyResponseSummary> getUserSurveyAnswerHistory(Long userId) {
        List<SurveyResponse> responses = surveyResponseRepository.findByUserId(userId);

        return responses.stream()
                .map(response -> new SurveyDtos.SurveyResponseSummary(
                        response.getId(),
                        response.getSurvey().getTitle(),
                        response.getCompletedAt(),
                        response.getXpAwarded(),
                        response.isFlaggedAsSuspicious(),
                        response.getTimeSpentSeconds()
                ))
                .collect(Collectors.toList());
    }

}
