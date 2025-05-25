package org.example.gamified_survey_app.admin.service;

import lombok.RequiredArgsConstructor;
import org.example.gamified_survey_app.admin.dto.UserBanRequest;
import org.example.gamified_survey_app.admin.dto.UserProfileadmiDTO;
import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.auth.repository.UserRepository;
import org.example.gamified_survey_app.core.constants.Roles;
import org.example.gamified_survey_app.core.exception.CustomException;
import org.example.gamified_survey_app.survey.model.Survey;
import org.example.gamified_survey_app.survey.repository.SurveyRepository;
import org.example.gamified_survey_app.user.model.UserProfile;
import org.example.gamified_survey_app.user.repository.UserProfileRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final SurveyRepository surveyRepository;
    private final UserProfileRepository userProfileRepository;

    @Transactional
    public AppUser banUser(UserBanRequest request) {
        // Check if current user is admin
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new CustomException("Current user not found"));

        if (currentUser.getRole() != Roles.ADMIN) {
            throw new CustomException("Only admins can ban users");
        }

        // Find the user to ban
        AppUser userToBan = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException("User not found"));

        // Set ban info
        userToBan.setBanned(true);
        userToBan.setBanReason(request.getReason());
        userToBan.setBannedAt(LocalDateTime.now());

        if (request.isPermanent()) {
            userToBan.setBanExpiresAt(null); // Permanent
        } else {
            if (request.getDuration() == null || request.getDuration() <= 0) {
                throw new CustomException("Duration must be a positive number for temporary bans");
            }
            userToBan.setBanExpiresAt(LocalDateTime.now().plusDays(request.getDuration()));
        }

        return userRepository.save(userToBan);
    }

    @Transactional
    public AppUser unbanUser(Long userId) {
        // Check if current user is admin
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser currentUser = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new CustomException("Current user not found"));

        if (currentUser.getRole() != Roles.ADMIN) {
            throw new CustomException("Only admins can ban users");
        }


        // Find the user to unban
        AppUser userToUnban = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException("User not found"));
        
        // Remove ban
        userToUnban.setBanned(false);
        userToUnban.setBanReason(null);
        userToUnban.setBannedAt(null);
        userToUnban.setBanExpiresAt(null);
        
        return userRepository.save(userToUnban);
    }
    
    @Transactional
    public List<AppUser> getBannedUsers() {
        // Check if current user is admin
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser currentUser = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new CustomException("Current user not found"));

        if (currentUser.getRole() != Roles.ADMIN) {
            throw new CustomException("Only admins can ban users");
        }


        return userRepository.findByBannedTrue();
    }
    
    @Transactional
    public Survey verifySurvey(Long surveyId) {
        // Check if current user is admin
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser currentUser = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new CustomException("Current user not found"));

        if (currentUser.getRole() != Roles.ADMIN) {
            throw new CustomException("Only admins can ban users");
        }


        Survey survey = surveyRepository.findById(surveyId)
            .orElseThrow(() -> new CustomException("Survey not found"));
        
        survey.setVerified(true);
        return surveyRepository.save(survey);
    }

    @Transactional
    public List<UserProfileadmiDTO> getAllUserProfiles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new CustomException("Current user not found"));

        if (currentUser.getRole() != Roles.ADMIN) {
            throw new CustomException("Only admins can ban users");
        }
        List<UserProfile> profiles = userProfileRepository.findAll();
        List<UserProfileadmiDTO> dtos = new ArrayList<>();
        for (UserProfile profile : profiles) {
            UserProfileadmiDTO dto = new UserProfileadmiDTO();
            dto.setFirstName(profile.getFirstName());
            dto.setLastName(profile.getLastName());
            dto.setId(profile.getUser().getId());
            dtos.add(dto);
        }
        return dtos;



    }

} 