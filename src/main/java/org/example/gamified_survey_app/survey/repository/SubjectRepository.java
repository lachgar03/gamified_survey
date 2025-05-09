package org.example.gamified_survey_app.survey.repository;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.survey.model.Forum;
import org.example.gamified_survey_app.survey.model.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByForum(Forum forum);
    Page<Subject> findByForumOrderByPostedAtDesc(Forum forum, Pageable pageable);
    List<Subject> findByCreator(AppUser creator);
}