package com.talentflow.dto.response;

import com.talentflow.enums.EmploymentType;
import com.talentflow.enums.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private EmploymentType employmentType;
    private List<String> requiredSkills;
    private String experienceLevel;
    private JobStatus status;
    private String postedBy;
    private Long postedById;
    private LocalDateTime createdAt;
}

