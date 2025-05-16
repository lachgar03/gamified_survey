package org.example.gamified_survey_app.user.service;

import lombok.RequiredArgsConstructor;
import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.survey.model.SurveyResponse;
import org.example.gamified_survey_app.survey.repository.SurveyResponseRepository;
import org.example.gamified_survey_app.user.dto.ProgressionRequestDto;
import org.example.gamified_survey_app.user.dto.ProgressionResponseDto;
import org.example.gamified_survey_app.user.dto.ProgressionResponseDto.MilestoneDto;
import org.example.gamified_survey_app.user.dto.ProgressionResponseDto.ProgressionPoint;
import org.example.gamified_survey_app.user.dto.ProgressionResponseDto.UserStats;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressionService {

    private final SurveyResponseRepository surveyRepo;

    public ProgressionResponseDto getUserProgression(AppUser user, ProgressionRequestDto dto) {
        LocalDate start = dto.getStartDate() != null ? dto.getStartDate() : LocalDate.now().minusDays(30);
        LocalDate end = dto.getEndDate() != null ? dto.getEndDate() : LocalDate.now();

        List<ProgressionResponseDto.ProgressionPoint> data = new ArrayList<>();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            int xpEarned = getXpEarnedOn(user, date);
            int surveysCompleted = surveyRepo.countByUserAndDate(user, date);

            data.add(new ProgressionResponseDto.ProgressionPoint(
                    date.toString(), xpEarned, surveysCompleted
            ));
        }

        return new ProgressionResponseDto(
                new ProgressionResponseDto.UserStats(user.getXp(), user.calculateLevel()),
                data,
                generateMilestones(user, data)
        );
    }

    private int getXpEarnedOn(AppUser user, LocalDate date) {
        return surveyRepo.findByUserAndCompletedAtDate(user, date)
                .stream()
                .mapToInt(sr -> sr.getXpAwarded() != null ? sr.getXpAwarded() : 0)
                .sum();
    }

    private List<ProgressionResponseDto.MilestoneDto> generateMilestones(AppUser user, List<ProgressionResponseDto.ProgressionPoint> data) {
        List<ProgressionResponseDto.MilestoneDto> milestones = new ArrayList<>();
        if (user.getXp() >= 1000) {
            milestones.add(new ProgressionResponseDto.MilestoneDto(
                    "LEVEL_UP",
                    "Reached Level " + user.calculateLevel(),
                    LocalDate.now().toString()
            ));
        }
        return milestones;
    }
}
