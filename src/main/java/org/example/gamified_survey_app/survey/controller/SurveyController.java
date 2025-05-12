package org.example.gamified_survey_app.survey.controller;

import java.util.List;

import org.example.gamified_survey_app.core.dto.PaginationParams;
import org.example.gamified_survey_app.survey.dto.SurveyDtos;
import org.example.gamified_survey_app.survey.service.SurveyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/surveys")
@RequiredArgsConstructor
public class SurveyController {

    private static final Logger log = LoggerFactory.getLogger(SurveyController.class);
    private final SurveyService surveyService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<SurveyDtos.SurveyResponse> createSurvey(@Valid @RequestBody SurveyDtos.SurveyRequest surveyRequest) {
        log.info("Création d'un nouveau sondage: {}", surveyRequest.getTitle());
        SurveyDtos.SurveyResponse response = surveyService.createSurvey(surveyRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<SurveyDtos.SurveyResponse>> getAllSurveys(@Valid PaginationParams params) {
        log.info("Récupération de tous les sondages, page: {}, taille: {}", params.getPage(), params.getSize());
        Page<SurveyDtos.SurveyResponse> surveys = surveyService.getAllSurveys(params.toPageable());
        return ResponseEntity.ok(surveys);
    }

    @GetMapping("/active")
    public ResponseEntity<Page<SurveyDtos.SurveyResponse>> getActiveSurveys(@Valid PaginationParams params) {
        log.info("Récupération des sondages actifs, page: {}, taille: {}", params.getPage(), params.getSize());
        Page<SurveyDtos.SurveyResponse> surveys = surveyService.getActiveSurveys(params.toPageable());
        return ResponseEntity.ok(surveys);
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('PARTICIPANT', 'CREATOR', 'ADMIN')")
    public ResponseEntity<Page<SurveyDtos.SurveyResponse>> getAvailableSurveys(@Valid PaginationParams params) {
        log.info("Récupération des sondages disponibles, page: {}, taille: {}", params.getPage(), params.getSize());
        Page<SurveyDtos.SurveyResponse> surveys = surveyService.getAvailableSurveysForCurrentUser(params.toPageable());
        return ResponseEntity.ok(surveys);
    }

    @GetMapping("/created")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<List<SurveyDtos.SurveyResponse>> getSurveysByCreator() {
        log.info("Récupération des sondages créés par l'utilisateur");
        List<SurveyDtos.SurveyResponse> surveys = surveyService.getSurveysByCreator();
        return ResponseEntity.ok(surveys);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SurveyDtos.SurveyDetailResponse> getSurveyById(@PathVariable Long id) {
        log.info("Récupération du sondage avec l'id: {}", id);
        SurveyDtos.SurveyDetailResponse survey = surveyService.getSurveyById(id);
        return ResponseEntity.ok(survey);
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN') and @surveySecurityService.isCreator(#id, principal)")
    public ResponseEntity<Void> deactivateSurvey(@PathVariable Long id) {
        log.info("Désactivation du sondage avec l'id: {}", id);
        surveyService.deactivateSurvey(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/respond")
    @PreAuthorize("hasAnyRole('PARTICIPANT', 'CREATOR', 'ADMIN')")
    public ResponseEntity<SurveyDtos.SurveyResponseSummary> submitSurveyResponse(
            @Valid @RequestBody SurveyDtos.SurveySubmissionRequest surveySubmissionRequest) {
        log.info("Soumission d'une réponse pour le sondage: {}", surveySubmissionRequest.getSurveyId());
        SurveyDtos.SurveyResponseSummary summary = surveyService.submitSurveyResponse(surveySubmissionRequest);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/{id}/results")
    @PreAuthorize("hasRole('ADMIN') or @surveySecurityService.canViewResults(#id, principal)")
    public ResponseEntity<SurveyDtos.SurveyResultStats> getSurveyResults(@PathVariable Long id) {
        log.info("Récupération des résultats du sondage: {}", id);
        SurveyDtos.SurveyResultStats stats = surveyService.getSurveyResults(id);
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/{id}/update")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN') and @surveySecurityService.canEditSurvey(#id, principal)")
    public ResponseEntity<SurveyDtos.SurveyResponse> updateSurvey(
            @PathVariable Long id,
            @Valid @RequestBody SurveyDtos.SurveyRequest surveyRequest) {
        log.info("Mise à jour du sondage: {}", id);
        SurveyDtos.SurveyResponse response = surveyService.updateSurvey(id, surveyRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN') and @surveySecurityService.canEditSurvey(#id, principal)")
    public ResponseEntity<Void> deleteSurvey(@PathVariable Long id) {
        log.info("Suppression du sondage: {}", id);
        surveyService.deleteSurvey(id);
        return ResponseEntity.noContent().build();
    }
}
