# AI Resume Intelligence Platform

## Project Vision Document

**Version:** 1.0
**Status:** Approved
**Target Release:** MVP / Version 1
**Development Target:** 25–30 focused hours

---

## 1. Product Overview

The AI Resume Intelligence Platform is a full-stack web application that helps candidates evaluate their resumes, understand how well they match job descriptions, and apply to recruiter-posted opportunities.

The platform combines three connected capabilities:

1. **Resume Intelligence** — analyzes the overall quality and content of a candidate's resume.
2. **AI Job Matching** — compares a resume against a specific job description and provides structured match insights.
3. **Lightweight Recruitment Portal** — allows recruiters to post jobs, receive applications, review candidate information, and manage application status.

The primary focus of Version 1 is the candidate resume-analysis and job-matching experience. Recruiter functionality intentionally remains lightweight.

The system is designed as a production-style SaaS MVP using React, Spring Boot, PostgreSQL, Supabase Storage, and the Gemini API.

---

## 2. Problem Statement

### Candidate Problem

Students, recent graduates, and entry-level job seekers often apply for jobs without clearly understanding:

* how strong their resume is;
* which skills are visible in their resume;
* whether their resume matches a specific job description;
* which required skills are missing;
* which important job-related keywords are absent;
* what parts of their resume could be improved.

Candidates may therefore submit the same resume to multiple jobs without understanding how well it aligns with each opportunity.

### Recruiter Problem

Recruiters frequently receive resumes containing large amounts of unstructured information.

Initial applicant review requires manually identifying:

* relevant skills;
* experience;
* strengths;
* potential skill gaps;
* alignment with the job requirements.

The platform aims to make this information easier to understand while ensuring that final hiring decisions remain with human recruiters.

---

## 3. Product Vision

Build an AI-powered resume intelligence and job-matching platform where candidates can analyze their resumes, compare them against job descriptions, and apply to opportunities while recruiters can post jobs and review applicants.

The product should demonstrate how AI can transform unstructured resume and job-description data into structured, useful insights without presenting AI recommendations as authoritative hiring decisions.

---

## 4. Target Users

### Candidates

Primary candidate users include:

* university students;
* recent graduates;
* internship applicants;
* entry-level job seekers.

Candidates use the platform to improve their resumes and understand their suitability for particular opportunities.

### Recruiters

Recruiters and small hiring teams use the platform to:

* create job postings;
* provide job descriptions;
* receive candidate applications;
* view candidate resumes and analysis;
* update application status.

---

## 5. Core Product Capabilities

Version 1 contains three connected capabilities.

### 5.1 Resume Intelligence

Candidates upload a PDF resume and receive general feedback that does not depend on a particular job.

General analysis may include:

* Resume Quality Score
* Resume Summary
* Extracted Skills
* Strengths
* Weaknesses

This analysis answers:

> "How strong and well-structured is my resume in general?"

It does not claim to represent the score of a real company's Applicant Tracking System.

---

### 5.2 AI Job Match Analysis

Candidates can compare their resume against a specific job description.

The job description can come from:

1. a recruiter-created job inside the platform; or
2. an external job description pasted manually by the candidate.

The system compares:

**Resume + Job Description**

and generates structured insights such as:

* Job Match Score
* Matched Skills
* Missing Skills
* Missing Keywords
* Match Summary

This answers:

> "How well does my current resume align with this particular job?"

The Job Match Score is an AI-generated estimate and must not be presented as an official score from an employer's ATS.

---

### 5.3 Lightweight Recruitment Portal

Recruiters can create job postings containing:

* job title;
* description;
* requirements;
* required skills.

Candidates can browse available jobs and apply using one of their uploaded resumes.

Recruiters can:

* view applicants;
* open candidate resume information;
* view existing resume analysis;
* view job-match information where available;
* shortlist or reject applicants.

The platform is not intended to replace a full recruitment system such as LinkedIn or a large Applicant Tracking System.

---

## 6. Candidate Workflow

### Resume Analysis

Candidate registers or logs in.

↓

Candidate uploads a PDF resume.

↓

Backend validates the file.

↓

PDF is stored in Supabase Storage.

↓

Resume metadata is stored in PostgreSQL.

↓

Text is extracted from the PDF.

↓

Extracted resume text is sent to Gemini.

↓

Gemini returns structured general resume analysis.

↓

Backend validates and stores the analysis.

↓

Candidate views the Resume Intelligence Dashboard.

---

### Job Match Analysis

Candidate selects an uploaded resume.

↓

Candidate either:

* selects a recruiter-posted job; or
* pastes an external job description.

↓

Backend sends the resume content and job description to Gemini.

↓

Gemini returns structured job-match analysis.

↓

Backend validates and stores the result where appropriate.

↓

Candidate views:

* Job Match Score
* Matched Skills
* Missing Skills
* Missing Keywords
* Match Summary

---

### Job Application

Candidate selects a recruiter-posted job.

↓

Candidate selects an uploaded resume.

↓

Candidate applies.

↓

An application is created.

↓

Initial status:

`PENDING`

↓

Recruiter reviews the application.

↓

Status may become:

`SHORTLISTED`

or

`REJECTED`

---

## 7. Recruiter Workflow

Recruiter registers or logs in.

↓

Recruiter creates a job.

↓

Job becomes available to candidates.

↓

Candidates apply.

↓

Recruiter views applicants.

↓

Recruiter opens an applicant.

↓

Recruiter can inspect:

* candidate information;
* uploaded resume;
* resume analysis;
* available job-match analysis.

↓

Recruiter updates the application status.

---

## 8. Core Value Proposition

### For Candidates

The platform helps candidates move from:

> "Is my resume good?"

to two more useful questions:

> "What are the strengths and weaknesses of my resume?"

and:

> "How well does my resume align with this specific job?"

### For Recruiters

The platform converts unstructured resume information into structured insights that can support faster initial applicant review.

AI assists the process but does not make final hiring decisions.

---

## 9. Version 1 Priorities

Features are divided into priorities to keep the project achievable within the development target.

### P0 — Core MVP

These features must work.

#### Authentication

* Candidate registration
* Recruiter registration
* Login
* Secure password hashing
* JWT authentication
* `CANDIDATE` role
* `RECRUITER` role
* Role-based endpoint authorization

#### Resume Management

* PDF upload
* File-type validation
* File-size validation
* Supabase Storage integration
* Resume metadata persistence
* PDF text extraction

#### Resume Intelligence

* Resume Quality Score
* Resume Summary
* Extracted Skills
* Strengths
* Weaknesses
* Structured Gemini response
* Backend response validation

#### Job Match Analysis

Support:

* recruiter-posted job descriptions;
* manually pasted external job descriptions.

Generate:

* Job Match Score
* Matched Skills
* Missing Skills
* Missing Keywords
* Match Summary

#### Candidate Dashboard

Candidates can view:

* uploaded resumes;
* resume analysis;
* job-match analysis.

#### AI Failure Handling

Analysis operations should support states such as:

* `PENDING`
* `PROCESSING`
* `COMPLETED`
* `FAILED`

Users should receive a clear failure state when AI analysis cannot be completed.

A retry mechanism should be provided where practical.

---

### P1 — Portfolio-Ready Product

These features should be implemented after the core AI workflow is stable.

#### Recruiter

* Create job
* View jobs
* View applicants
* View applicant resume
* View available analysis
* Shortlist applicant
* Reject applicant

#### Candidate Jobs

* Browse jobs
* View job details
* Apply using an uploaded resume
* View application status

#### Engineering Quality

* DTO-based API contracts
* Bean Validation
* Centralized exception handling
* Swagger/OpenAPI
* Automated backend tests
* Critical frontend tests where valuable
* Dockerfile
* GitHub Actions CI
* Environment-based configuration
* Cloud deployment

---

### P2 — Optional Enhancements

Only implement these after P0 and P1 are stable.

Possible features include:

* grammar feedback;
* personalized learning roadmap;
* recommended job roles;
* recommended projects;
* certification recommendations;
* additional resume improvement suggestions.

P2 functionality must never delay testing, deployment, or stabilization.

---

## 10. AI Integration Strategy

Version 1 will integrate directly with the Gemini API from Spring Boot.

LangChain4j will not be used.

The application should avoid tightly coupling resume business logic to Gemini.

Conceptually:

Resume Analysis Service

↓

AI Analysis Client

↓

Gemini Client

↓

Gemini API

The same principle applies to job-match analysis.

This allows another AI provider to replace Gemini in a future version without requiring major changes to the application's business logic.

---

## 11. Structured AI Output

Gemini should not return arbitrary text that is directly displayed to users.

The backend should request structured responses.

### Example Resume Analysis

```json
{
  "qualityScore": 82,
  "summary": "Backend-focused software engineering candidate...",
  "skills": [
    "Java",
    "Spring Boot",
    "React"
  ],
  "strengths": [
    "Relevant technical projects",
    "Strong backend technologies"
  ],
  "weaknesses": [
    "Limited quantified achievements"
  ]
}
```

### Example Job Match Analysis

```json
{
  "matchScore": 76,
  "matchedSkills": [
    "Java",
    "Spring Boot"
  ],
  "missingSkills": [
    "AWS",
    "Docker"
  ],
  "missingKeywords": [
    "CI/CD"
  ],
  "matchSummary": "The resume demonstrates strong backend fundamentals but lacks several cloud-related requirements."
}
```

The backend should deserialize and validate these responses before persistence.

---

## 12. AI Reliability Strategy

AI is an external and probabilistic dependency.

The application must account for:

* malformed responses;
* missing fields;
* Gemini API errors;
* API timeouts;
* temporary service failures.

The frontend should never remain indefinitely in a loading state.

A failed analysis should transition to:

`FAILED`

and provide an understandable error/retry experience.

---

## 13. High-Level System Architecture

### General Resume Analysis

React Frontend

↓

Spring Boot REST API

↓

File Validation

↓

Supabase Storage

↓

PDF Text Extraction

↓

Gemini API

↓

Structured Response Validation

↓

PostgreSQL

↓

Resume Intelligence Dashboard

---

### Job Match Analysis

Resume

*

Job Description

↓

Spring Boot

↓

Gemini API

↓

Structured Response Validation

↓

Job Match Result

↓

Candidate Dashboard

---

### Recruitment Flow

Recruiter

↓

Job

↓

Candidate Application

↓

Candidate + Resume

↓

Analysis

↓

Application Status

---

## 14. High-Level Data Relationships

Conceptually:

User

↓

Candidate or Recruiter Role

Candidate

↓

Resume

↓

Resume Analysis

A candidate may have multiple resumes.

A recruiter may create multiple jobs.

Job

↓

Applications

Each application connects:

* Candidate
* Job
* Resume
* Application Status

Job-match analysis is based on:

* Resume
* Job Description

The detailed database schema will be defined during the Database Schema planning stage.

---

## 15. Success Criteria

Version 1 is considered successful when the following workflows operate correctly.

### Candidate

A candidate can:

* register and log in;
* upload a PDF resume;
* receive general resume analysis;
* paste an external job description;
* receive job-match analysis;
* browse recruiter-created jobs;
* analyze their resume against a platform job;
* apply to a job;
* view application status.

### Recruiter

A recruiter can:

* register and log in;
* create a job;
* view their jobs;
* view applicants;
* view candidate resume information;
* view available analysis;
* shortlist or reject applicants.

### Engineering

The system:

* protects private endpoints;
* enforces candidate/recruiter authorization;
* validates user input;
* validates uploaded files;
* handles application errors consistently;
* handles Gemini failures;
* persists application data in PostgreSQL;
* stores PDF files using object storage;
* provides REST API documentation;
* contains meaningful automated tests;
* passes basic CI checks;
* can run its backend using Docker;
* is deployed and publicly demonstrable;
* contains sufficient documentation for another developer to understand the system.

---

## 16. Non-Goals for Version 1

Version 1 will NOT attempt to implement:

* a full LinkedIn-style job platform;
* admin dashboard;
* platform analytics;
* social networking;
* messaging;
* advanced recruiter search;
* advanced filtering;
* candidate comparison;
* automatic candidate ranking;
* AI hiring decisions;
* complex company management;
* email notifications;
* real-time notifications;
* resume rewriting;
* OCR for scanned resumes;
* multiple AI providers;
* LangChain4j;
* microservices;
* Kafka;
* Kubernetes;
* complex event-driven architecture.

These capabilities are outside the scope of the MVP.

---

## 17. Key Assumptions

The MVP assumes:

* resumes are primarily PDF files;
* PDFs contain extractable text;
* candidates can provide external job descriptions as text;
* recruiter-created jobs contain sufficient description and requirements;
* Gemini is available for development/demo usage;
* AI output may occasionally fail or be inaccurate;
* the application operates at portfolio/demo scale;
* available free/student cloud tiers are sufficient for the MVP.

---

## 18. Constraints

### Development Time

Version 1 targets approximately **25–30 focused development hours**.

Priority order is:

P0 Core Workflow

↓

P1 Portfolio Features

↓

Testing and Stabilization

↓

Deployment

↓

P2 Enhancements

If development takes longer than expected, P2 functionality will be removed before compromising core functionality, testing, or deployment.

### Architecture

Version 1 will use a modular monolithic Spring Boot backend.

Microservices are intentionally avoided because the expected scale does not justify their operational complexity.

### AI Accuracy

Resume Quality Scores and Job Match Scores are AI-generated estimates.

They must not be represented as official scores produced by an employer or proprietary Applicant Tracking System.

### Cost

Free, student, or development tiers should be preferred where practical.

---

## 19. Product Principles

### Candidate Experience First

Resume intelligence and job matching are the primary product capabilities.

### Quality Over Feature Count

A small number of reliable workflows is preferred over many incomplete features.

### AI Assists Rather Than Decides

AI provides insights and recommendations.

Hiring decisions remain human decisions.

### Structured AI Integration

AI output should be treated as external data that requires parsing, validation, and failure handling.

### Security by Design

Authentication, authorization, password security, file validation, input validation, and secret management are part of the architecture.

### Maintainability Without Overengineering

The application should have clear modular boundaries while avoiding infrastructure that the MVP does not need.

### Failure Is Part of the Design

External APIs and cloud services can fail.

The application should handle those failures predictably.

### Explainable Engineering

Major architecture and technology decisions should be understandable and defensible during technical interviews.

---

## 20. Future Scope

Possible Version 2 features include:

* advanced job-specific resume recommendations;
* AI suggestions for improving resume-job alignment;
* candidate comparison;
* recruiter candidate ranking;
* recruiter analytics;
* admin dashboard;
* company profiles;
* email notifications;
* resume version history;
* AI-assisted resume rewriting;
* learning roadmaps;
* project recommendations;
* certification recommendations;
* additional AI providers;
* LangChain4j evaluation;
* advanced monitoring and observability.

---

## 21. Vision Summary

The AI Resume Intelligence Platform is a candidate-first, AI-powered resume intelligence and lightweight recruitment platform.

The primary product journey is:

**Upload Resume → Understand Resume → Compare Against Job → Improve Understanding → Apply → Recruiter Reviews**

The primary technical journey is:

**React → Spring Boot → PostgreSQL / Supabase Storage → PDF Processing → Gemini → Structured AI Validation → Business Workflow → Testing → CI → Docker → Deployment**

Version 1 intentionally focuses on complete, demonstrable workflows that provide both real user value and strong software-engineering learning opportunities within a limited development timeframe.
