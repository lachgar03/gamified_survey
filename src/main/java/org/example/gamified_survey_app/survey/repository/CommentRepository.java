package org.example.gamified_survey_app.survey.repository;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.survey.model.Comment;
import org.example.gamified_survey_app.survey.model.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findBySubject(Subject subject);
    Page<Comment> findBySubjectOrderBySentDateAsc(Subject subject, Pageable pageable);
    List<Comment> findByCreator(AppUser creator);
}