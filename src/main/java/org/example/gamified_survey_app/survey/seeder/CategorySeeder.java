package org.example.gamified_survey_app.survey.seeder;

import org.example.gamified_survey_app.survey.model.Category;
import org.example.gamified_survey_app.survey.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

@Configuration
public class CategorySeeder {

    @Bean
    @Order(1)
    CommandLineRunner seedSurveyCategories(CategoryRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                List<Category> categories = List.of(
                        new Category(null, "Satisfaction Client", "Mesurer la satisfaction des clients vis-à-vis des produits ou services."),
                        new Category(null, "Étude de Marché", "Analyser les besoins et comportements des consommateurs."),
                        new Category(null, "Feedback Employé", "Recueillir l’avis des employés sur leur environnement de travail."),
                        new Category(null, "Événements", "Évaluer le succès ou l’organisation des événements."),
                        new Category(null, "Produits", "Obtenir des retours sur les produits ou fonctionnalités."),
                        new Category(null, "Services Publics", "Sonder l’opinion publique sur les services gouvernementaux."),
                        new Category(null, "Éducation", "Évaluer la qualité de l'enseignement ou des formations."),
                        new Category(null, "Santé", "Collecter des retours sur les soins médicaux."),
                        new Category(null, "Technologie", "Mesurer l’utilisation ou l’intérêt pour des outils technologiques."),
                        new Category(null, "Réseaux Sociaux", "Analyser les tendances et comportements en ligne.")
                );
                repository.saveAll(categories);
            }
        };
    }
}
