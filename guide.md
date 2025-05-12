
# Documentation Complète - Plateforme Gamifiée de Sondages

## Table des matières
1. [Introduction](#introduction)
2. [Guide utilisateur](#guide-utilisateur)
   - [Utilisateurs participants](#utilisateurs-participants)
   - [Utilisateurs créateurs](#utilisateurs-créateurs)
   - [Administrateurs](#administrateurs)
3. [Architecture technique](#architecture-technique)
4. [Fonctionnalités principales](#fonctionnalités-principales)
5. [Sécurité et performances](#sécurité-et-performances)
6. [Guide de développement](#guide-de-développement)
7. [Intégration et API](#intégration-et-api)

## Introduction

La Plateforme Gamifiée de Sondages est une application web permettant de créer, gérer et répondre à des sondages tout en intégrant des mécanismes de gamification. L'objectif est d'encourager l'engagement des utilisateurs grâce à un système de récompenses basé sur des points d'expérience (XP), des niveaux, des badges et des défis.

### Principales fonctionnalités
- Création et gestion de sondages
- Participation aux sondages avec gain de points XP
- Système de progression par niveaux
- Obtention de badges pour certaines réalisations
- Défis hebdomadaires et mensuels
- Système de parrainage
- Échange de points contre des récompenses
- Classements périodiques (leaderboards)

## Guide utilisateur

### Utilisateurs participants

#### Création de compte et connexion
1. **Inscription** : Créez un compte en fournissant votre email, mot de passe et informations de base.
2. **Connexion** : Utilisez vos identifiants pour accéder à votre compte.
3. **Profil utilisateur** : Complétez votre profil avec des informations supplémentaires (profession, région, âge).

#### Participation aux sondages
1. **Parcourir les sondages disponibles** : Accédez à la liste des sondages disponibles depuis le tableau de bord.
2. **Répondre à un sondage** : Sélectionnez un sondage et répondez aux questions proposées.
3. **Gagner des XP** : Recevez des points d'expérience (XP) après avoir complété un sondage.
4. **Progression de niveau** : Accumulez des XP pour monter de niveau et débloquer de nouvelles possibilités.

#### Système de gamification
1. **Badges** : Obtenez des badges pour certaines réalisations (premier sondage, 10 sondages complétés, etc.).
2. **Défis** : Participez à des défis hebdomadaires et mensuels pour gagner des XP supplémentaires.
3. **Classements** : Consultez votre position dans les classements hebdomadaires, mensuels et globaux.
4. **Récompenses** : Échangez vos points XP contre des récompenses (bons d'achat, réductions, etc.).

#### Parrainage
1. **Inviter des amis** : Générez un code de parrainage unique pour inviter vos amis.
2. **Bonus de parrainage** : Gagnez des XP supplémentaires lorsque vos filleuls complètent des sondages.

### Utilisateurs créateurs

#### Création et gestion de sondages
1. **Créer un sondage** : Créez un nouveau sondage en définissant son titre, sa description et sa date d'expiration.
2. **Ajouter des questions** : Ajoutez différents types de questions (choix multiple, texte, échelle, etc.).
3. **Paramètres avancés** : Définissez les XP offerts, le temps minimum requis et le nombre maximum de participants.
4. **Catégorisation** : Associez votre sondage à des catégories thématiques.

#### Analyse des résultats
1. **Statistiques en temps réel** : Consultez les résultats de vos sondages dès que des réponses sont soumises.
2. **Visualisations** : Accédez à des graphiques et tableaux représentant les résultats.
3. **Exportation** : Téléchargez les résultats au format CSV ou Excel pour une analyse approfondie.

### Administrateurs

#### Gestion de la plateforme
1. **Gestion des utilisateurs** : Consultez, modifiez et gérez les comptes utilisateurs.
2. **Modération des sondages** : Approuvez, modifiez ou supprimez les sondages si nécessaire.
3. **Gestion des catégories** : Créez et gérez les catégories thématiques pour les sondages.

#### Configuration de la gamification
1. **Paramètres des niveaux** : Définissez les seuils XP pour chaque niveau.
2. **Gestion des badges** : Créez et attribuez des badges pour diverses réalisations.
3. **Configuration des défis** : Créez des défis hebdomadaires et mensuels avec des récompenses.
4. **Gestion des récompenses** : Ajoutez et gérez les récompenses disponibles dans la boutique.

#### Surveillance et modération
1. **Détection de fraude** : Consultez les activités signalées comme suspectes par le système anti-fraude.
2. **Bannissement** : Suspendez ou bannissez les utilisateurs qui enfreignent les règles.
3. **Tableau de bord administratif** : Accédez à des statistiques globales sur l'utilisation de la plateforme.

## Architecture technique

### Stack technologique
- **Backend** : Spring Boot (Java 21)
- **Base de données** : PostgreSQL
- **Authentification** : JWT (JSON Web Tokens)
- **Cache** : Caffeine
- **Documentation API** : Spring Doc (OpenAPI)

### Structure du projet
```
src/
├── main/
│   ├── java/org/example/gamified_survey_app/
│   │   ├── admin/            # Fonctionnalités d'administration
│   │   ├── auth/             # Authentification et sécurité
│   │   ├── config/           # Configuration de l'application
│   │   ├── core/             # Composants centraux et utilitaires
│   │   ├── gamification/     # Système de gamification (badges, niveaux, défis)
│   │   ├── survey/           # Gestion des sondages et réponses
│   │   └── user/             # Profils utilisateurs et parrainage
│   └── resources/
│       ├── application.properties  # Configuration principale
│       ├── application-dev.properties  # Configuration de développement
│       └── application-prod.properties # Configuration de production
└── test/
    └── java/org/example/gamified_survey_app/
        └── ...               # Tests unitaires et d'intégration
```

## Fonctionnalités principales

### Système d'authentification
- **Inscription et connexion** : Gestion des utilisateurs avec validation d'emails
- **Gestion des rôles** : PARTICIPANT, CREATOR, ADMIN
- **Sécurité JWT** : Authentification stateless avec tokens signés
- **Récupération de mot de passe** : Procédure de réinitialisation par email

### Système de sondages
- **Création de sondages** : Interface pour créer des sondages avec différents types de questions
- **Réponses aux sondages** : Collecte et stockage sécurisé des réponses
- **Analyse des résultats** : Agrégation et visualisation des données collectées
- **Catégorisation** : Classification des sondages par thèmes

### Système de gamification
- **Points XP** : Attribution de points pour diverses actions (compléter un sondage, parrainer un ami)
- **Niveaux** : Progression basée sur les XP cumulés, avec des seuils configurables
- **Badges** : Récompenses pour des accomplissements spécifiques
- **Défis** : Missions temporaires avec des récompenses supplémentaires
- **Classements** : Comparaison des performances entre utilisateurs

### Système de parrainage
- **Génération de codes** : Création de codes uniques pour inviter des amis
- **Tracking des parrainages** : Suivi des relations parrain-filleul
- **Bonus XP** : Récompenses pour les parrains lorsque leurs filleuls sont actifs

### Système de récompenses
- **Boutique de récompenses** : Catalogue d'avantages échangeables contre des XP
- **Gestion des échanges** : Suivi des demandes et distribution des récompenses
- **Inventaire** : Gestion des quantités disponibles pour chaque récompense

## Sécurité et performances

### Sécurité
- **Protection contre les attaques courantes** : XSS, CSRF, injection SQL
- **Validation des entrées** : Validation stricte des données utilisateur
- **Gestion des permissions** : Contrôle d'accès précis aux ressources
- **En-têtes de sécurité** : CSP, X-Frame-Options, HSTS, etc.
- **Détection de fraude** : Algorithmes pour identifier les comportements suspects

### Performances
- **Mise en cache** : Utilisation de Caffeine pour optimiser les accès fréquents
- **Pagination** : Limitation du volume de données dans les réponses
- **Rate limiting** : Protection contre les abus (60 requêtes/minute, 1000/heure)
- **Optimisation des requêtes** : Requêtes SQL efficaces et indexation adaptée

## Guide de développement

### Prérequis
- JDK 21 ou supérieur
- Maven 3.8 ou supérieur
- PostgreSQL 13 ou supérieur

### Installation et exécution
1. Cloner le dépôt Git
2. Configurer la base de données dans `application.properties`
3. Exécuter `mvn clean install` pour construire le projet
4. Lancer l'application avec `mvn spring-boot:run`

### Structure des packages
- **controller** : Points d'entrée REST API
- **service** : Logique métier
- **repository** : Accès aux données
- **model** : Entités JPA
- **dto** : Objets de transfert de données
- **exception** : Gestion des erreurs

### Bonnes pratiques
- Utiliser les DTOs pour les transferts de données entre frontend et backend
- Implémenter les vérifications de fraude avant d'enregistrer une réponse
- Toujours fournir des feedback visuels pour les actions utilisateur
- Ne jamais exposer les données sensibles ou non filtrées au frontend
- Utiliser la validation appropriée pour toutes les entrées utilisateur

## Intégration et API

### Documentation API
- Documentation complète des endpoints disponible à `/swagger-ui.html`
- Tous les endpoints retournent des réponses standardisées :
  ```json
  {
    "code": "SUCCESS",
    "message": "Opération réussie",
    "data": { /* Données de réponse */ }
  }
  ```
  ou en cas d'erreur :
  ```json
  {
    "code": "ERROR_CODE",
    "message": "Message d'erreur",
    "details": ["Détails supplémentaires", "..."]
  }
  ```

### Authentification API
Toutes les requêtes (sauf authentification) nécessitent un token JWT :
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Pagination
Les endpoints retournant des collections supportent la pagination :
- `page` : Index de page (commence à 0)
- `size` : Nombre d'éléments par page (maximum 100)
- `sortBy` : Champ de tri
- `sortDirection` : Direction du tri (`asc` ou `desc`)

### Webhooks
Pour les intégrations tierces, la plateforme peut notifier des événements via webhooks :
- Nouvelle réponse à un sondage
- Utilisateur atteignant un nouveau niveau
- Badge débloqué
- Défi complété

### Extensions futures
- API pour l'intégration avec d'autres plateformes
- Webhooks pour les événements importants
- SDK pour faciliter l'intégration mobile
- Support pour les sondages multilingues
- Outils d'analyse avancée des résultats

Cette documentation fournit une vue d'ensemble complète de la Plateforme Gamifiée de Sondages. Pour des informations plus détaillées sur des aspects spécifiques, veuillez consulter les sections dédiées ou contacter l'équipe de développement.
