package com.talentflow.dto.request;

import com.talentflow.enums.EmploymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class JobRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, message = "Description must be at least 10 characters")
    private String description;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;

    @NotEmpty(message = "At least one skill is required")
    private List<String> requiredSkills;

    @NotBlank(message = "Experience level is required")
    private String experienceLevel;
}

