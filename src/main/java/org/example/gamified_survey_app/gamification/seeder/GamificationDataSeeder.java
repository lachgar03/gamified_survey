package org.example.gamified_survey_app.gamification.seeder;

import org.example.gamified_survey_app.gamification.model.Badge;
import org.example.gamified_survey_app.gamification.model.Gift;
import org.example.gamified_survey_app.gamification.model.Level;
import org.example.gamified_survey_app.gamification.repository.BadgeRepository;
import org.example.gamified_survey_app.gamification.repository.GiftRepository;
import org.example.gamified_survey_app.gamification.repository.LevelRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class GamificationDataSeeder {

    @Bean
    CommandLineRunner seedData(
            BadgeRepository badgeRepository,
            LevelRepository levelRepository,
            GiftRepository giftRepository) {
        return args -> {
            // Seeder pour les niveaux
            if (levelRepository.count() == 0) {
                levelRepository.saveAll(List.of(
                        new Level(null, "🐣 Débutant", 1, 0, "Bienvenue ! Le voyage commence.", null),
                        new Level(null, "🔍 Novice Curieux", 2, 200, "Vous avez commencé à explorer quelques sondages.", null),
                        new Level(null, "🚀 Explorateur", 3, 600, "Vous vous impliquez de plus en plus.", null),
                        new Level(null, "🧠 Répondant Actif", 4, 1200, "Vous répondez régulièrement aux enquêtes.", null),
                        new Level(null, "🧭 Analyste Stratège", 5, 2000, "Vos réponses sont précises et constantes.", null),
                        new Level(null, "💡 Influenceur", 6, 3500, "Vous avez un réel impact sur les résultats.", null),
                        new Level(null, "🔥 Expert Confirmé", 7, 5000, "Vos insights sont reconnus et recherchés.", null),
                        new Level(null, "👑 Maître Sondé", 8, 7500, "Vous êtes une référence sur la plateforme.", null),
                        new Level(null, "🐉 Légende", 9, 10000, "Presque intouchable. Peu atteignent ce sommet.", null)
                ));
            }

            // Seeder pour les badges
            if (badgeRepository.count() == 0) {
                badgeRepository.saveAll(List.of(
                        new Badge(null, "Engagé", "Laisser 3 commentaires sur des sondages", null, "Laisser 3 commentaires sur des forums"),
                        new Badge(null, "Explorer", "Répondre à au moins 1 sondage par catégorie", null, "Répondre à 1 sondage par catégorie"),
                        new Badge(null, "Nouveau Répondant", "Un filleul a répondu à 5 sondages", null, "Un filleul a répondu à 5 sondages"),
                        new Badge(null, "Super Parrain", "Un filleul a atteint le niveau 3", null, "Un filleul atteint le niveau 3")
                ));
            }

            // Seeder pour les cadeaux (exemple)
            if (giftRepository.count() == 0) {
                giftRepository.saveAll(List.of(
                        new Gift(null, "Bon d'achat Amazon", "Bon de 10€ à utiliser sur Amazon.fr", null, 1500, 50, true),
                        new Gift(null, "Réduction Spotify", "Réduction de 50% sur un mois d’abonnement Spotify", null, 1000, 100, true),
                        new Gift(null, "Carte cadeau Fnac", "Carte cadeau de 20€", null, 2500, 30, true)
                ));
            }
        };
    }
}

