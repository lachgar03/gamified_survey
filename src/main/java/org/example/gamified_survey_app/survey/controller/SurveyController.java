package org.example.gamified_survey_app.survey.controller;

import lombok.RequiredArgsConstructor;
import org.example.gamified_survey_app.survey.dto.SurveyDtos;
import org.example.gamified_survey_app.survey.service.SurveyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/surveys")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<SurveyDtos.SurveyResponse> createSurvey(@RequestBody SurveyDtos.SurveyRequest request) {
        return ResponseEntity.ok(surveyService.createSurvey(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SurveyDtos.SurveyDetailResponse> getSurveyById(@PathVariable Long id) {
        return ResponseEntity.ok(surveyService.getSurveyById(id));
    }

    @GetMapping("/active")
    public ResponseEntity<Page<SurveyDtos.SurveyResponse>> getActiveSurveys(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(surveyService.getActiveSurveys(pageRequest));
    }

    @GetMapping("/available")
    public ResponseEntity<Page<SurveyDtos.SurveyResponse>> getAvailableSurveys(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(surveyService.getAvailableSurveysForCurrentUser(pageRequest));
    }

    @GetMapping("/created")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<List<SurveyDtos.SurveyResponse>> getSurveysByCreator() {
        return ResponseEntity.ok(surveyService.getSurveysByCreator());
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<Void> deactivateSurvey(@PathVariable Long id) {
        surveyService.deactivateSurvey(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/respond")
    public ResponseEntity<SurveyDtos.SurveyResponseSummary> submitSurveyResponse(
            @RequestBody SurveyDtos.SurveySubmissionRequest request
    ) {
        return ResponseEntity.ok(surveyService.submitSurveyResponse(request));
    }

    @GetMapping
    public ResponseEntity<Page<SurveyDtos.SurveyResponse>> getAllSurveys(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(surveyService.getAllSurveys(pageRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<Void> deleteSurvey(@PathVariable Long id) {
        surveyService.deleteSurvey(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<SurveyDtos.SurveyResponse> updateSurvey(
            @PathVariable Long id,
            @RequestBody SurveyDtos.SurveyRequest updatedSurvey
    ) {
        return ResponseEntity.ok(surveyService.updateSurvey(id, updatedSurvey));
    }

    @GetMapping("/{id}/results")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<SurveyDtos.SurveyResultStats> getSurveyResults(@PathVariable Long id) {
        return ResponseEntity.ok(surveyService.getSurveyResults(id));
    }
}
