package com.talentflow.controller;

import com.talentflow.dto.request.ApplicationRequest;
import com.talentflow.dto.request.UpdateApplicationStatusRequest;
import com.talentflow.dto.response.ApiResponse;
import com.talentflow.dto.response.ApplicationResponse;
import com.talentflow.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@Tag(name = "Applications", description = "APIs for job application management")
public class ApplicationController {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    @Autowired
    private ApplicationService applicationService;

    @PostMapping("/apply/{jobId}")
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Apply for a job", description = "Candidates can apply for open jobs", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<ApplicationResponse>> applyForJob(
            @PathVariable Long jobId,
            @Valid @RequestBody ApplicationRequest request) {
        logger.info("Application request for job id: {}", jobId);
        ApplicationResponse response = applicationService.applyForJob(jobId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Application submitted successfully", response));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Get my applications", description = "Returns all applications by the logged-in candidate", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getMyApplications() {
        List<ApplicationResponse> applications = applicationService.getMyApplications();
        return ResponseEntity.ok(ApiResponse.success("Applications retrieved successfully", applications));
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
    @Operation(summary = "Get applications for a job", description = "Recruiters can view applications for their jobs", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getApplicationsByJob(@PathVariable Long jobId) {
        List<ApplicationResponse> applications = applicationService.getApplicationsByJob(jobId);
        return ResponseEntity.ok(ApiResponse.success("Applications retrieved successfully", applications));
    }

    @PutMapping("/{applicationId}/status")
    @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
    @Operation(summary = "Update application status", description = "Recruiters can update application status", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<ApplicationResponse>> updateApplicationStatus(
            @PathVariable Long applicationId,
            @Valid @RequestBody UpdateApplicationStatusRequest request) {
        logger.info("Updating application status for id: {}", applicationId);
        ApplicationResponse response = applicationService.updateApplicationStatus(applicationId, request);
        return ResponseEntity.ok(ApiResponse.success("Application status updated successfully", response));
    }
}

