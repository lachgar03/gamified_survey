package org.example.gamified_survey_app.user.service;

import lombok.RequiredArgsConstructor;
import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.auth.repository.UserRepository;
import org.example.gamified_survey_app.core.exception.CustomException;
import org.example.gamified_survey_app.user.dto.AvatarConfigDto;
import org.example.gamified_survey_app.user.dto.UserProfileDto;
import org.example.gamified_survey_app.user.model.AvatarConfig;
import org.example.gamified_survey_app.user.model.UserProfile;
import org.example.gamified_survey_app.user.repository.UserProfileRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public UserProfileDto getUserProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser user = userRepository.findByEmail(email).orElseThrow();

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(user);
                    return userProfileRepository.save(newProfile);
                });

        UserProfileDto dto = new UserProfileDto();
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setAge(profile.getAge());
        dto.setPhoneNumber(profile.getPhoneNumber());
        dto.setProfession(profile.getProfession());
        dto.setRegion(profile.getRegion());
        dto.setEmail(user.getEmail());
        return dto;
    }

    public UserProfileDto updateUserProfile(UserProfileDto profileDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser user = userRepository.findByEmail(email).orElseThrow();

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(user);
                    return newProfile;
                });

        profile.setFirstName(profileDto.getFirstName());
        profile.setLastName(profileDto.getLastName());
        profile.setAge(profileDto.getAge());
        profile.setPhoneNumber(profileDto.getPhoneNumber());
        profile.setProfession(profileDto.getProfession());
        profile.setRegion(profileDto.getRegion());

        userProfileRepository.save(profile);

        return profileDto;
    }

    public boolean changePassword(String oldPassword, String newPassword) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser user = userRepository.findByEmail(email).orElseThrow();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return true;
    }

    public AvatarConfigDto updateAvatar(AvatarConfigDto newConfig) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser user = userRepository.findByEmail(email).orElseThrow();

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("User profile not found"));

        AvatarConfig avatarConfig = profile.getAvatarConfig();
        if (avatarConfig == null) {
            avatarConfig = new AvatarConfig();
            avatarConfig.setUserProfile(profile);
        }

        avatarConfig.setTopType(newConfig.getTopType());
        avatarConfig.setHairColor(newConfig.getHairColor());
        avatarConfig.setAccessoriesType(newConfig.getAccessoriesType());
        avatarConfig.setEyeType(newConfig.getEyeType());
        avatarConfig.setSkinColor(newConfig.getSkinColor());
        avatarConfig.setAccessoriesColor(newConfig.getAccessoriesColor());
        avatarConfig.setMouth(newConfig.getMouth());
        avatarConfig.setEyebrows(newConfig.getEyebrows());
        avatarConfig.setFacialHair(newConfig.getFacialHair());
        avatarConfig.setFacialHairColor(newConfig.getFacialHairColor());
        avatarConfig.setClothes(newConfig.getClothes());
        avatarConfig.setClothesColor(newConfig.getClothesColor());

        profile.setAvatarConfig(avatarConfig); // ensures bidirectional link
        userProfileRepository.save(profile);   // saves avatar too via cascade

        return newConfig;
    }


    public AvatarConfigDto getAvatarConfig() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser user = userRepository.findByEmail(email).orElseThrow();

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("User profile not found"));

        AvatarConfig avatarConfig = profile.getAvatarConfig();
        if (avatarConfig == null) {
            return new AvatarConfigDto(); // return empty config
        }

        AvatarConfigDto configDto = new AvatarConfigDto();
        configDto.setTopType(avatarConfig.getTopType());
        configDto.setHairColor(avatarConfig.getHairColor());
        configDto.setAccessoriesType(avatarConfig.getAccessoriesType());
        configDto.setEyeType(avatarConfig.getEyeType());
        configDto.setSkinColor(avatarConfig.getSkinColor());
        configDto.setAccessoriesColor(avatarConfig.getAccessoriesColor());
        configDto.setMouth(avatarConfig.getMouth());
        configDto.setEyebrows(avatarConfig.getEyebrows());
        configDto.setFacialHair(avatarConfig.getFacialHair());
        configDto.setFacialHairColor(avatarConfig.getFacialHairColor());
        configDto.setClothes(avatarConfig.getClothes());
        configDto.setClothesColor(avatarConfig.getClothesColor());

        return configDto;
    }

}