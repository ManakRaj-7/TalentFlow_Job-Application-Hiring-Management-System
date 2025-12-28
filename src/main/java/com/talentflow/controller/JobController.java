package com.talentflow.controller;

import com.talentflow.dto.request.JobRequest;
import com.talentflow.dto.response.ApiResponse;
import com.talentflow.dto.response.JobResponse;
import com.talentflow.enums.JobStatus;
import com.talentflow.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@Tag(name = "Jobs", description = "APIs for job management")
public class JobController {

    private static final Logger logger = LoggerFactory.getLogger(JobController.class);

    @Autowired
    private JobService jobService;

    @PostMapping
    @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
    @Operation(summary = "Create a new job", description = "Only RECRUITER and ADMIN can create jobs", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<JobResponse>> createJob(@Valid @RequestBody JobRequest request) {
        logger.info("Creating new job: {}", request.getTitle());
        JobResponse response = jobService.createJob(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Job created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
    @Operation(summary = "Update a job", description = "Only the job poster or ADMIN can update", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<JobResponse>> updateJob(
            @PathVariable Long id, 
            @Valid @RequestBody JobRequest request) {
        logger.info("Updating job with id: {}", id);
        JobResponse response = jobService.updateJob(id, request);
        return ResponseEntity.ok(ApiResponse.success("Job updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
    @Operation(summary = "Delete a job", description = "Only the job poster or ADMIN can delete", 
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Object>> deleteJob(@PathVariable Long id) {
        logger.info("Deleting job with id: {}", id);
        jobService.deleteJob(id);
        return ResponseEntity.ok(ApiResponse.success("Job deleted successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all jobs", description = "Returns paginated list of all jobs")
    public ResponseEntity<ApiResponse<Page<JobResponse>>> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<JobResponse> jobs = jobService.getAllJobs(pageable);
        return ResponseEntity.ok(ApiResponse.success("Jobs retrieved successfully", jobs));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job by ID", description = "Returns job details by ID")
    public ResponseEntity<ApiResponse<JobResponse>> getJobById(@PathVariable Long id) {
        JobResponse job = jobService.getJobById(id);
        return ResponseEntity.ok(ApiResponse.success("Job retrieved successfully", job));
    }

    @GetMapping("/search")
    @Operation(summary = "Search jobs", description = "Search jobs by skill, location, and status with pagination")
    public ResponseEntity<ApiResponse<Page<JobResponse>>> searchJobs(
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) JobStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<JobResponse> jobs = jobService.searchJobs(skill, location, status, pageable);
        return ResponseEntity.ok(ApiResponse.success("Jobs retrieved successfully", jobs));
    }
}

