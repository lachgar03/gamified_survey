package org.example.gamified_survey_app.survey.service;

import lombok.RequiredArgsConstructor;
import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.auth.repository.UserRepository;
import org.example.gamified_survey_app.core.exception.CustomException;
import org.example.gamified_survey_app.survey.dto.SurveyDtos;
import org.example.gamified_survey_app.survey.model.*;
import org.example.gamified_survey_app.survey.repository.*;
import org.example.gamified_survey_app.user.model.UserProfile;
import org.example.gamified_survey_app.user.repository.UserProfileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForumService {

    private final ForumRepository forumRepository;
    private final SubjectRepository subjectRepository;
    private final CommentRepository commentRepository;
    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    // Helper method to get current user
    private AppUser getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new CustomException("User not found"));
    }

    @Transactional
    public SurveyDtos.ForumResponse createForum(SurveyDtos.ForumRequest request) {
        Survey survey = surveyRepository.findById(request.getSurveyId())
                .orElseThrow(() -> new CustomException("Survey not found"));

        // Only survey creator or admin can create a forum
        AppUser currentUser = getCurrentUser();
        if (!survey.getCreator().getId().equals(currentUser.getId())) {
            throw new CustomException("Only the survey creator can add a forum");
        }

        if (survey.isHasForum()) {
            throw new CustomException("This survey already has a forum");
        }

        Forum forum = new Forum();
        forum.setTitle(request.getTitle());
        forum.setDescription(request.getDescription());
        forum.setCreatedAt(LocalDateTime.now());
        forum.setSurvey(survey);

        Forum savedForum = forumRepository.save(forum);

        // Update the survey
        survey.setHasForum(true);
        survey.setForum(savedForum);
        surveyRepository.save(survey);

        return mapToForumResponse(savedForum);
    }

    public SurveyDtos.ForumResponse getForumBySurvey(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new CustomException("Survey not found"));

        Forum forum = forumRepository.findBySurvey(survey)
                .orElseThrow(() -> new CustomException("Forum not found for this survey"));

        return mapToForumResponse(forum);
    }

    @Transactional
    public SurveyDtos.SubjectResponse createSubject(SurveyDtos.SubjectRequest request) {
        AppUser currentUser = getCurrentUser();

        Forum forum = forumRepository.findById(request.getForumId())
                .orElseThrow(() -> new CustomException("Forum not found"));

        Subject subject = new Subject();
        subject.setTitle(request.getTitle());
        subject.setPostedAt(LocalDateTime.now());
        subject.setCreator(currentUser);
        subject.setForum(forum);

        Subject savedSubject = subjectRepository.save(subject);

        return mapToSubjectResponse(savedSubject);
    }

    public Page<SurveyDtos.SubjectResponse> getSubjectsByForum(Long forumId, Pageable pageable) {
        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new CustomException("Forum not found"));

        Page<Subject> subjects = subjectRepository.findByForumOrderByPostedAtDesc(forum, pageable);

        return subjects.map(this::mapToSubjectResponse);
    }

    @Transactional
    public SurveyDtos.CommentResponse createComment(SurveyDtos.CommentRequest request) {
        AppUser currentUser = getCurrentUser();

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new CustomException("Subject not found"));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setSentDate(LocalDateTime.now());
        comment.setCreator(currentUser);
        comment.setSubject(subject);

        Comment savedComment = commentRepository.save(comment);

        return mapToCommentResponse(savedComment);
    }

    public Page<SurveyDtos.CommentResponse> getCommentsBySubject(Long subjectId, Pageable pageable) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new CustomException("Subject not found"));

        Page<Comment> comments = commentRepository.findBySubjectOrderBySentDateAsc(subject, pageable);

        return comments.map(this::mapToCommentResponse);
    }

    @Transactional
    public void createForumForSurvey(Survey survey) {
        if (forumRepository.existsBySurvey(survey)) {
            throw new CustomException("Forum already exists for this survey");
        }

        Forum forum = new Forum();
        forum.setTitle(survey.getTitle() + " Discussion");
        forum.setEnabled(true);
        forum.setDescription("Forum for discussing the survey: " + survey.getTitle());
        forum.setCreatedAt(LocalDateTime.now());
        forum.setSurvey(survey);

        forumRepository.save(forum);
    }
    @Transactional
    public void removeForumFromSurvey(Survey survey) {
        forumRepository.findBySurvey(survey).ifPresent(forum -> {
            forum.setEnabled(false);
        });
    }

    // Mapping methods
    private SurveyDtos.ForumResponse mapToForumResponse(Forum forum) {
        int subjectCount = forum.getSubjects() != null ? forum.getSubjects().size() : 0;

        return new SurveyDtos.ForumResponse(
                forum.getId(),
                forum.getTitle(),
                forum.getDescription(),
                forum.getCreatedAt(),
                forum.getSurvey().getId(),
                forum.getSurvey().getTitle(),
                subjectCount
        );
    }

    private SurveyDtos.SubjectResponse mapToSubjectResponse(Subject subject) {
        int commentCount = subject.getComments() != null ? subject.getComments().size() : 0;
        Optional<UserProfile> profile = userProfileRepository.findByUser(subject.getCreator());
        String name = profile.get().getLastName() + " " + profile.get().getFirstName();

        return new SurveyDtos.SubjectResponse(
                subject.getId(),
                subject.getTitle(),
                subject.getPostedAt(),
                name,
                subject.getForum().getId(),
                commentCount
        );
    }

    private SurveyDtos.CommentResponse mapToCommentResponse(Comment comment) {
        Optional<UserProfile> profile = userProfileRepository.findByUser(comment.getCreator());
        String name = profile.get().getLastName() + " " + profile.get().getFirstName();
        return new SurveyDtos.CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getSentDate(),
                name,
                comment.getSubject().getId()
        );
    }
}