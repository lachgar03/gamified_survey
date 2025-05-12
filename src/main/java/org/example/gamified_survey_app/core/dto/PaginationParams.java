package org.example.gamified_survey_app.core.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PaginationParams {
    @Min(value = 0, message = "La page doit être supérieure ou égale à 0")
    private int page = 0;
    
    @Min(value = 1, message = "La taille doit être au minimum 1")
    @Max(value = 100, message = "La taille ne peut pas dépasser 100")
    private int size = 20;
    
    private String sortBy;
    private String sortDirection;
    
    public Pageable toPageable() {
        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.isEmpty()) {
            sort = Sort.by(sortBy);
            if (sortDirection != null && "desc".equalsIgnoreCase(sortDirection)) {
                sort = sort.descending();
            } else {
                sort = sort.ascending();
            }
        }
        return PageRequest.of(page, size, sort);
    }
} 