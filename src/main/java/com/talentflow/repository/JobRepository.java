package com.talentflow.repository;

import com.talentflow.entity.Job;
import com.talentflow.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    Page<Job> findByStatus(JobStatus status, Pageable pageable);
    
    @Query("SELECT j FROM Job j WHERE " +
           "(:skill IS NULL OR :skill IN (SELECT s FROM j.requiredSkills s)) AND " +
           "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:status IS NULL OR j.status = :status)")
    Page<Job> searchJobs(@Param("skill") String skill, 
                         @Param("location") String location, 
                         @Param("status") JobStatus status, 
                         Pageable pageable);
    
    List<Job> findByPostedBy_Id(Long recruiterId);
}

