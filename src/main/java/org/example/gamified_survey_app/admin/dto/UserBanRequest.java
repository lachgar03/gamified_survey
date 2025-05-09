package org.example.gamified_survey_app.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBanRequest {
    private Long userId;
    private String reason;
    private boolean permanent;
} 