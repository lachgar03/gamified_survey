package org.example.gamified_survey_app.gamification.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.auth.repository.UserRepository;
import org.example.gamified_survey_app.gamification.model.Level;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserXpService {

    private final UserRepository userRepository;
    private final LevelService levelService;
    private final LeaderboardService leaderboardService;

    public AppUser getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
    }

    @Transactional
    public void updateUserXp(AppUser user, int xpToAdd) {
        int oldXp = user.getXp();
        int newXp = oldXp + xpToAdd;
        user.setXp(newXp);
        System.out.println("sdsada");
        // Handle level-up
        if (levelService.hasUserLeveledUp(user, oldXp)) {
            Level newLevel = levelService.getUserLevel(user);
            user.setLevel(newLevel);
            user.setLevelUpAt();
            System.out.println("User "+user.getEmail()+", leveled up to" +newLevel.getNumber());
        }

        userRepository.save(user);

        // Update leaderboard
        System.out.println(xpToAdd);
        leaderboardService.updateUserXp(user, xpToAdd);

        System.out.println("Awarded "+xpToAdd +"XP to user "+user.getEmail());
    }

}
