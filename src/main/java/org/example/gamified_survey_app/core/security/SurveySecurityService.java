package org.example.gamified_survey_app.core.security;

import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.core.constants.Roles;
import org.example.gamified_survey_app.survey.model.Survey;
import org.example.gamified_survey_app.survey.repository.SurveyRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurveySecurityService {
    
    private final SurveyRepository surveyRepository;
    
    /**
     * Vérifie si l'utilisateur est le créateur du sondage
     */
    public boolean isCreator(Long surveyId, UserDetails principal) {
        if (!(principal instanceof AppUser)) {
            return false;
        }
        
        AppUser user = (AppUser) principal;
        return surveyRepository.findById(surveyId)
                .map(survey -> survey.getCreator().getId().equals(user.getId()))
                .orElse(false);
    }
    
    /**
     * Vérifie si l'utilisateur peut voir les résultats d'un sondage
     */
    public boolean canViewResults(Long surveyId, UserDetails principal) {
        if (!(principal instanceof AppUser)) {
            return false;
        }
        
        AppUser user = (AppUser) principal;
        Survey survey = surveyRepository.findById(surveyId).orElse(null);
        
        if (survey == null) {
            return false;
        }
        
        // Le créateur du sondage ou un admin peut voir les résultats
        boolean isCreator = survey.getCreator().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Roles.ADMIN;

        return isCreator || isAdmin;


    }
    
    /**
     * Vérifie si l'utilisateur peut modifier un sondage
     */
    public boolean canEditSurvey(Long surveyId, UserDetails principal) {
        return isCreator(surveyId, principal);
    }
} 