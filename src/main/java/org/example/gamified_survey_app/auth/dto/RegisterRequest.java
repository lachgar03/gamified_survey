    package org.example.gamified_survey_app.auth.dto;

    import lombok.Data;

    @Data
    public class RegisterRequest {
        private String email;
        private String password;
        private String firstname;
        private String lastname;
        private String role;
        private String referralCode;
        private Integer age;
        private String region;

    }

