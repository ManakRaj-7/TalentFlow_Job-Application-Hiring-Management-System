-- TalentFlow Database Schema
-- This schema is automatically created by Hibernate, but provided here for reference

CREATE DATABASE IF NOT EXISTS talentflow_db;
USE talentflow_db;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    INDEX idx_email (email),
    INDEX idx_role (role)
);

-- Jobs table
CREATE TABLE IF NOT EXISTS jobs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(255) NOT NULL,
    employment_type VARCHAR(50) NOT NULL,
    experience_level VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    posted_by_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (posted_by_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_status (status),
    INDEX idx_posted_by (posted_by_id),
    INDEX idx_created_at (created_at)
);

-- Job skills collection table
CREATE TABLE IF NOT EXISTS job_skills (
    job_id BIGINT NOT NULL,
    skill VARCHAR(255) NOT NULL,
    PRIMARY KEY (job_id, skill),
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE
);

-- Applications table
CREATE TABLE IF NOT EXISTS applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    candidate_id BIGINT NOT NULL,
    job_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'APPLIED',
    resume_link VARCHAR(500) NOT NULL,
    applied_at DATETIME NOT NULL,
    FOREIGN KEY (candidate_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    UNIQUE KEY unique_application (candidate_id, job_id),
    INDEX idx_candidate (candidate_id),
    INDEX idx_job (job_id),
    INDEX idx_status (status)
);

