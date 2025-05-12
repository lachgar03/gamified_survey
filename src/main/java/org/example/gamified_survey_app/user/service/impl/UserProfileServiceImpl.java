package org.example.gamified_survey_app.user.service.impl;

import java.util.Optional;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.user.model.UserProfile;
import org.example.gamified_survey_app.user.repository.UserProfileRepository;
import org.example.gamified_survey_app.user.service.UserProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Override
    public String getUserDisplayName(AppUser user) {
        if (user == null) {
            return "Anonymous";
        }
        
        UserProfile profile = getUserProfile(user);
        
        if (profile != null && profile.getFirstName() != null && profile.getLastName() != null) {
            return profile.getFirstName() + " " + profile.getLastName();
        } else if (profile != null && profile.getFirstName() != null) {
            return profile.getFirstName();
        } else {
            return user.getEmail().split("@")[0]; // Use part before @ in email
        }
    }

    @Override
    public UserProfile getUserProfile(AppUser user) {
        if (user == null) {
            return null;
        }
        
        Optional<UserProfile> profileOpt = userProfileRepository.findByUser(user);
        return profileOpt.orElse(null);
    }

    @Override
    @Transactional
    public UserProfile updateUserProfile(AppUser user, UserProfile profileData) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        // Find existing profile or create new one
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(user);
                    return newProfile;
                });
        
        // Update fields
        if (profileData.getFirstName() != null) {
            profile.setFirstName(profileData.getFirstName());
        }
        
        if (profileData.getLastName() != null) {
            profile.setLastName(profileData.getLastName());
        }
        
        if (profileData.getAge() != null) {
            profile.setAge(profileData.getAge());
        }
        
        if (profileData.getPhoneNumber() != null) {
            profile.setPhoneNumber(profileData.getPhoneNumber());
        }
        
        if (profileData.getProfession() != null) {
            profile.setProfession(profileData.getProfession());
        }
        
        if (profileData.getRegion() != null) {
            profile.setRegion(profileData.getRegion());
        }
        
        return userProfileRepository.save(profile);
    }
} 