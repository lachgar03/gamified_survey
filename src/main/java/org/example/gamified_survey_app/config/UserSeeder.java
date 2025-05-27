package org.example.gamified_survey_app.config;

import lombok.RequiredArgsConstructor;
import org.example.gamified_survey_app.auth.dto.RegisterRequest;
import org.example.gamified_survey_app.auth.repository.UserRepository;
import org.example.gamified_survey_app.auth.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Order(3)
public class UserSeeder implements CommandLineRunner {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final Random random = new Random();

    // Listes de données fictives
    private final List<String> firstNames = Arrays.asList(
            "Ahmed", "Fatima", "Mohammed", "Aicha", "Omar", "Khadija", "Youssef", "Nadia",
            "Hassan", "Samira", "Karim", "Latifa", "Rachid", "Zineb", "Abdelkader", "Malika",
            "Said", "Amina", "Mustapha", "Hafsa", "Abderrahim", "Souad", "Khalid", "Nawal",
            "Brahim", "Rajae", "Noureddine", "Btissam", "Driss", "Safaa", "Aziz", "Siham",
            "Lahcen", "Wafaa", "Abdellah", "Karima", "Jamal", "Houda", "Mostafa", "Imane",
            "Hamid", "Sanaa", "Adil", "Ghizlane", "Tarik", "Najat", "Fouad", "Laila",
            "Hicham", "Nezha", "Ismail", "Zahra", "Abdessamad", "Ilham", "Redouane", "Hayat"
    );

    private final List<String> lastNames = Arrays.asList(
            "Alami", "Bennani", "Cherkaoui", "Douiri", "El Amrani", "Fassi", "Guessous", "Hajji",
            "Idrissi", "Jebari", "Kabbaj", "Lahlou", "Mahrach", "Naciri", "Ouali", "Qadiri",
            "Raji", "Sabir", "Tazi", "Usmani", "Valli", "Wahbi", "Xerri", "Yaacoubi", "Zaki",
            "Berrada", "Chraibi", "Dahbi", "Ennaciri", "Filali", "Ghali", "Hilali", "Ismaili",
            "Jazouli", "Kettani", "Loudiyi", "Mekouar", "Nahli", "Ouazzani", "Pacha", "Qorchi"
    );

    private final List<String> regions = Arrays.asList(
            "Rabat-Salé-Kénitra", "Casablanca-Settat", "Marrakech-Safi", "Fès-Meknès",
            "Tanger-Tétouan-Al Hoceïma", "Oriental", "Souss-Massa", "Drâa-Tafilalet",
            "Béni Mellal-Khénifra", "Laâyoune-Sakia El Hamra", "Dakhla-Oued Ed-Dahab", "Guelmim-Oued Noun"
    );

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 1) { // Plus de 1 car il peut y avoir l'admin
            System.out.println("Users already exist. Skipping seeding.");
            return;
        }

        System.out.println("Starting user seeding...");

        try {
            // Créer 20 créateurs
            for (int i = 1; i <= 20; i++) {
                createUser(i, "CREATOR");
            }

            // Créer 80 participants
            for (int i = 21; i <= 100; i++) {
                createUser(i, "PARTICIPANT");
            }

            System.out.println("User seeding completed: 20 creators and 80 participants created.");

        } catch (Exception e) {
            System.err.println("Error during user seeding: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createUser(int index, String role) {
        try {
            RegisterRequest request = new RegisterRequest();

            // Informations de base
            request.setEmail(generateEmail(index, role));
            request.setPassword("password123"); // Mot de passe par défaut
            request.setRole(role);

            // Informations du profil
            request.setFirstname(getRandomElement(firstNames));
            request.setLastname(getRandomElement(lastNames));
            request.setAge(random.nextInt(45) + 18); // Âge entre 18 et 62 ans
            request.setRegion(getRandomElement(regions));

            // Pas de code de parrainage pour le seeding
            request.setReferralCode(null);

            // Enregistrer l'utilisateur via le service
            authService.register(request);

            System.out.printf("Created %s: %s (%s %s) - %d years old from %s%n",
                    role.toLowerCase(),
                    request.getEmail(),
                    request.getFirstname(),
                    request.getLastname(),
                    request.getAge(),
                    request.getRegion());

        } catch (Exception e) {
            System.err.println("Error creating user " + index + " (" + role + "): " + e.getMessage());
            // Continue avec les autres utilisateurs même si un échoue
        }
    }

    private String generateEmail(int index, String role) {
        String prefix = role.equals("CREATOR") ? "creator" : "participant";
        return String.format("%s%d@example.com", prefix, index);
    }

    private <T> T getRandomElement(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }
}