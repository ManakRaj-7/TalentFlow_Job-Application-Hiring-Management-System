package com.talentflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApplicationRequest {

    @NotBlank(message = "Resume link is required")
    private String resumeLink;
}

