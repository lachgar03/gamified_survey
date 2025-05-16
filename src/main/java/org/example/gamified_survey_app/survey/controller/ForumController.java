package org.example.gamified_survey_app.survey.controller;

import lombok.RequiredArgsConstructor;
import org.example.gamified_survey_app.survey.dto.SurveyDtos;
import org.example.gamified_survey_app.survey.service.ForumService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ForumController {

    private final ForumService forumService;
    @PostMapping("/forums")
    public ResponseEntity<SurveyDtos.ForumResponse> createForum(@RequestBody SurveyDtos.ForumRequest request) {
        return ResponseEntity.ok(forumService.createForum(request));
    }

    @GetMapping("/surveys/{surveyId}/forum")
    public ResponseEntity<SurveyDtos.ForumResponse> getForumBySurvey(@PathVariable Long surveyId) {
        return ResponseEntity.ok(forumService.getForumBySurvey(surveyId));
    }

    @PostMapping("/subjects")
    public ResponseEntity<SurveyDtos.SubjectResponse> createSubject(@RequestBody SurveyDtos.SubjectRequest request) {
        return ResponseEntity.ok(forumService.createSubject(request));
    }

    @GetMapping("/forums/{forumId}/subjects")
    public ResponseEntity<Page<SurveyDtos.SubjectResponse>> getSubjectsByForum(
            @PathVariable Long forumId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("postedAt").descending());
        return ResponseEntity.ok(forumService.getSubjectsByForum(forumId, pageRequest));
    }

    @PostMapping("/comments")
    public ResponseEntity<SurveyDtos.CommentResponse> createComment(@RequestBody SurveyDtos.CommentRequest request) {
        return ResponseEntity.ok(forumService.createComment(request));
    }

    @GetMapping("/subjects/{subjectId}/comments")
    public ResponseEntity<Page<SurveyDtos.CommentResponse>> getCommentsBySubject(
            @PathVariable Long subjectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("sentDate").ascending());
        return ResponseEntity.ok(forumService.getCommentsBySubject(subjectId, pageRequest));
    }
}