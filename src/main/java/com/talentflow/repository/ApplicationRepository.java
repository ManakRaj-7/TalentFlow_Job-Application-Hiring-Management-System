package com.talentflow.repository;

import com.talentflow.entity.Application;
import com.talentflow.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByCandidate_Id(Long candidateId);
    List<Application> findByJob_Id(Long jobId);
    Optional<Application> findByCandidate_IdAndJob_Id(Long candidateId, Long jobId);
    long countByJob_IdAndStatus(Long jobId, ApplicationStatus status);
}

