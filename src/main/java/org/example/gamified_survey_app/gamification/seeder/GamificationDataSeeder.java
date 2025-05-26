package org.example.gamified_survey_app.gamification.seeder;

import org.example.gamified_survey_app.gamification.model.Challenge;
import org.example.gamified_survey_app.gamification.model.Gift;
import org.example.gamified_survey_app.gamification.model.Level;
import org.example.gamified_survey_app.gamification.repository.ChallengeRepository;
import org.example.gamified_survey_app.gamification.repository.GiftRepository;
import org.example.gamified_survey_app.gamification.repository.LevelRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.example.gamified_survey_app.gamification.constant.ChallengePeriod;
import org.example.gamified_survey_app.gamification.constant.ChallengeType;

import java.util.List;

@Configuration
public class GamificationDataSeeder {

    @Bean
    CommandLineRunner seedData(
            ChallengeRepository challengeRepository,
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

            if (challengeRepository.count() == 0) {
                challengeRepository.saveAll(List.of(
                        new Challenge(
                                null,
                                "Engagé",
                                "Laisser 3 commentaires sur des sondages",
                                3,
                                3,
                                ChallengeType.CREATE_COMMENTS,
                                "Laisser 3 commentaires sur des sondages",
                                ChallengePeriod.PERMANENT
                        ),
                        new Challenge(
                                null,
                                "Explorer",
                                "Répondre à au moins 1 sondage par catégorie",
                                1,
                                1,
                                ChallengeType.COMPLETE_CATEGORY_SURVEYS,
                                "Répondre à 1 sondage par catégorie",
                                ChallengePeriod.PERMANENT
                        ),
                        new Challenge(
                                null,
                                "Matinal",
                                "Se connecter 7 jours consécutifs",
                                7,
                                7,
                                ChallengeType.DAILY_LOGIN,
                                "Connexion quotidienne pendant 7 jours",
                                ChallengePeriod.DAILY
                        ),
                        new Challenge(
                                null,
                                "Surfeur Régulier",
                                "Participer à des sondages chaque semaine pendant 4 semaines",
                                4,
                                4,
                                ChallengeType.WEEKLY_PARTICIPATION,
                                "Participation hebdomadaire pendant 4 semaines",
                                ChallengePeriod.WEEKLY
                        ),
                        new Challenge(
                                null,
                                "Réactif",
                                "Compléter 5 sondages en moins de 24 heures",
                                5,
                                5,
                                ChallengeType.QUICK_RESPONSE,
                                "Répondre rapidement à 5 sondages",
                                ChallengePeriod.DAILY
                        ),
                        new Challenge(
                                null,
                                "Influenceur",
                                "Parrainer 3 nouveaux utilisateurs",
                                3,
                                3,
                                ChallengeType.REFER_USERS,
                                "Parrainer 3 utilisateurs",
                                ChallengePeriod.PERMANENT
                        ),
                        new Challenge(
                                null,
                                "Fidèle",
                                "Participer à 10 sondages",
                                10,
                                10,
                                ChallengeType.COMPLETE_SURVEYS,
                                "Participer à 10 sondages",
                                ChallengePeriod.PERMANENT
                        )
                ));
            }



            // Seeder pour les cadeaux (exemple)
            if (giftRepository.count() == 0) {
                giftRepository.saveAll(List.of(
                        new Gift(null, "Bon d'achat Amazon", "Bon de 10€ à utiliser sur Amazon.fr", "https://global24.com/wp-content/uploads/2023/12/amazon-logo.png", 1500, 50, true),
                        new Gift(null, "Réduction Spotify", "Réduction de 50% sur un mois d’abonnement Spotify", "https://blog.push.fm/wp-content/uploads/2022/05/folder_920_201707260845-1-min.png", 1000, 100, true),
                        new Gift(null, "Carte cadeau Fnac", "Carte cadeau de 20€", "https://www.lefildaurelie.fr/img_s1/139034/boutique/screenshot_20230205_160015_chrome.jpg", 2500, 30, true)
                ));
            }
        };
    }
}

