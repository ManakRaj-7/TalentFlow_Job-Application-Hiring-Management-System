package com.talentflow.service;

import com.talentflow.dto.request.JobRequest;
import com.talentflow.dto.response.JobResponse;
import com.talentflow.entity.Job;
import com.talentflow.entity.User;
import com.talentflow.enums.JobStatus;
import com.talentflow.exception.ResourceNotFoundException;
import com.talentflow.exception.UnauthorizedAccessException;
import com.talentflow.repository.JobRepository;
import com.talentflow.repository.UserRepository;
import com.talentflow.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class JobService {

    private static final Logger logger = LoggerFactory.getLogger(JobService.class);

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public JobResponse createJob(JobRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User recruiter = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!recruiter.getRole().name().equals("RECRUITER") && !recruiter.getRole().name().equals("ADMIN")) {
            throw new UnauthorizedAccessException("Only recruiters can post jobs");
        }

        Job job = new Job();
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setEmploymentType(request.getEmploymentType());
        job.setRequiredSkills(request.getRequiredSkills());
        job.setExperienceLevel(request.getExperienceLevel());
        job.setStatus(JobStatus.OPEN);
        job.setPostedBy(recruiter);

        job = jobRepository.save(job);
        logger.info("Job created successfully: {} by {}", job.getTitle(), recruiter.getEmail());

        return convertToResponse(job);
    }

    @Transactional
    public JobResponse updateJob(Long id, JobRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));

        if (!job.getPostedBy().getId().equals(userPrincipal.getId()) && 
            !userPrincipal.getRole().name().equals("ADMIN")) {
            throw new UnauthorizedAccessException("You can only update your own jobs");
        }

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setEmploymentType(request.getEmploymentType());
        job.setRequiredSkills(request.getRequiredSkills());
        job.setExperienceLevel(request.getExperienceLevel());

        job = jobRepository.save(job);
        logger.info("Job updated successfully: {}", job.getTitle());

        return convertToResponse(job);
    }

    @Transactional
    public void deleteJob(Long id) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));

        if (!job.getPostedBy().getId().equals(userPrincipal.getId()) && 
            !userPrincipal.getRole().name().equals("ADMIN")) {
            throw new UnauthorizedAccessException("You can only delete your own jobs");
        }

        jobRepository.delete(job);
        logger.info("Job deleted successfully: {}", job.getTitle());
    }

    public Page<JobResponse> getAllJobs(Pageable pageable) {
        return jobRepository.findAll(pageable).map(this::convertToResponse);
    }

    public Page<JobResponse> searchJobs(String skill, String location, JobStatus status, Pageable pageable) {
        return jobRepository.searchJobs(skill, location, status, pageable).map(this::convertToResponse);
    }

    public JobResponse getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
        return convertToResponse(job);
    }

    private JobResponse convertToResponse(Job job) {
        JobResponse response = new JobResponse();
        response.setId(job.getId());
        response.setTitle(job.getTitle());
        response.setDescription(job.getDescription());
        response.setLocation(job.getLocation());
        response.setEmploymentType(job.getEmploymentType());
        response.setRequiredSkills(job.getRequiredSkills());
        response.setExperienceLevel(job.getExperienceLevel());
        response.setStatus(job.getStatus());
        response.setPostedBy(job.getPostedBy().getFullName());
        response.setPostedById(job.getPostedBy().getId());
        response.setCreatedAt(job.getCreatedAt());
        return response;
    }
}

