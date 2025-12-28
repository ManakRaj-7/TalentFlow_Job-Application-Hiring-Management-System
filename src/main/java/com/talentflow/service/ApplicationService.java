package com.talentflow.service;

import com.talentflow.dto.request.ApplicationRequest;
import com.talentflow.dto.request.UpdateApplicationStatusRequest;
import com.talentflow.dto.response.ApplicationResponse;
import com.talentflow.entity.Application;
import com.talentflow.entity.Job;
import com.talentflow.entity.User;
import com.talentflow.enums.ApplicationStatus;
import com.talentflow.enums.JobStatus;
import com.talentflow.exception.ResourceNotFoundException;
import com.talentflow.exception.UnauthorizedAccessException;
import com.talentflow.exception.ValidationException;
import com.talentflow.repository.ApplicationRepository;
import com.talentflow.repository.JobRepository;
import com.talentflow.repository.UserRepository;
import com.talentflow.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ApplicationResponse applyForJob(Long jobId, ApplicationRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User candidate = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!candidate.getRole().name().equals("CANDIDATE")) {
            throw new UnauthorizedAccessException("Only candidates can apply for jobs");
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (job.getStatus() != JobStatus.OPEN) {
            throw new ValidationException("Cannot apply for a closed job");
        }

        if (applicationRepository.findByCandidate_IdAndJob_Id(candidate.getId(), jobId).isPresent()) {
            throw new ValidationException("You have already applied for this job");
        }

        Application application = new Application();
        application.setCandidate(candidate);
        application.setJob(job);
        application.setStatus(ApplicationStatus.APPLIED);
        application.setResumeLink(request.getResumeLink());

        application = applicationRepository.save(application);
        logger.info("Application created successfully: Candidate {} applied for Job {}", 
                candidate.getEmail(), job.getTitle());

        return convertToResponse(application);
    }

    public List<ApplicationResponse> getMyApplications() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Application> applications = applicationRepository.findByCandidate_Id(userPrincipal.getId());
        return applications.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public List<ApplicationResponse> getApplicationsByJob(Long jobId) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (!job.getPostedBy().getId().equals(user.getId()) && !user.getRole().name().equals("ADMIN")) {
            throw new UnauthorizedAccessException("You can only view applications for your own jobs");
        }

        List<Application> applications = applicationRepository.findByJob_Id(jobId);
        return applications.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Transactional
    public ApplicationResponse updateApplicationStatus(Long applicationId, UpdateApplicationStatusRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        if (!application.getJob().getPostedBy().getId().equals(user.getId()) && 
            !user.getRole().name().equals("ADMIN")) {
            throw new UnauthorizedAccessException("You can only update applications for your own jobs");
        }

        application.setStatus(request.getStatus());
        application = applicationRepository.save(application);
        logger.info("Application status updated: {} to {}", applicationId, request.getStatus());

        return convertToResponse(application);
    }

    private ApplicationResponse convertToResponse(Application application) {
        ApplicationResponse response = new ApplicationResponse();
        response.setId(application.getId());
        response.setCandidateId(application.getCandidate().getId());
        response.setCandidateName(application.getCandidate().getFullName());
        response.setCandidateEmail(application.getCandidate().getEmail());
        response.setJobId(application.getJob().getId());
        response.setJobTitle(application.getJob().getTitle());
        response.setStatus(application.getStatus());
        response.setResumeLink(application.getResumeLink());
        response.setAppliedAt(application.getAppliedAt());
        return response;
    }
}

