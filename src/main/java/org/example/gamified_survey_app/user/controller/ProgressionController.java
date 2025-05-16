package org.example.gamified_survey_app.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.auth.repository.UserRepository;
import org.example.gamified_survey_app.user.dto.ProgressionRequestDto;
import org.example.gamified_survey_app.user.dto.ProgressionResponseDto;
import org.example.gamified_survey_app.user.service.ProgressionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/users/progression")
@RequiredArgsConstructor
public class ProgressionController {

    private final ProgressionService progressionService;
    private final UserRepository userRepository;

    @GetMapping
    public ProgressionResponseDto getProgression(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        if (userDetails == null) {
            throw new IllegalArgumentException("Utilisateur non authentifié.");
        }

        String email = userDetails.getUsername();
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé pour l'email : " + email));

        LocalDate start = parseDate(startDate);
        LocalDate end = parseDate(endDate);

        ProgressionRequestDto dto = new ProgressionRequestDto();
        dto.setStartDate(start);
        dto.setEndDate(end);

        return progressionService.getUserProgression(user, dto);
    }

    private LocalDate parseDate(String input) {
        if (input == null || input.isBlank()) return null;
        return LocalDate.parse(input.trim());  // Trims and parses
    }


}
