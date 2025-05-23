package org.example.gamified_survey_app.user.controller;

import org.example.gamified_survey_app.survey.service.SurveyService;
import org.example.gamified_survey_app.user.dto.CreatorStatsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/creator")
public class CreatorStatsController {

    private final SurveyService surveyService;

    @Autowired
    public CreatorStatsController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @GetMapping("/stats")
    public ResponseEntity<CreatorStatsDTO> getCreatorStats() {
        CreatorStatsDTO stats = surveyService.getCreatorStats();
        return ResponseEntity.ok(stats);
    }
}
