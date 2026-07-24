# AI Resume Intelligence Platform

## Data Model & REST API Design

**Version:** 1.0
**Status:** Approved
**Related Documents:** `01-project-vision.md`, `02-srs.md`, `03-technical-design.md`

---

# 1. Purpose

This document defines the Version 1 relational data model and REST API surface for the AI Resume Intelligence Platform.

The design supports:

* authentication;
* candidate and recruiter roles;
* resume management;
* general resume analysis;
* job-specific match analysis;
* recruiter-created jobs;
* candidate applications.

---

# 2. Database

**Database:** PostgreSQL
**Hosting:** Supabase
**Persistence:** Spring Data JPA / Hibernate

The primary entities are:

```text
User
Resume
ResumeAnalysis
Job
JobMatchAnalysis
Application
```

---

# 3. User

Represents both candidates and recruiters.

```text
users
--------------------------------
id                UUID / BIGINT PK
name              VARCHAR NOT NULL
email             VARCHAR UNIQUE NOT NULL
password_hash     VARCHAR NOT NULL
role              VARCHAR NOT NULL
created_at        TIMESTAMP NOT NULL
updated_at        TIMESTAMP
```

Supported roles:

```text
CANDIDATE
RECRUITER
```

### Important Constraints

* Email must be unique.
* Passwords must never be stored as plain text.
* Role must contain a supported value.

---

# 4. Resume

Represents a resume uploaded by a candidate.

```text
resumes
--------------------------------
id                UUID / BIGINT PK
candidate_id      FK → users.id
original_filename VARCHAR NOT NULL
storage_key       VARCHAR NOT NULL
content_type      VARCHAR
file_size         BIGINT
extracted_text    TEXT
created_at        TIMESTAMP NOT NULL
```

### Relationship

```text
User (Candidate)
       1
       │
       │
       N
     Resume
```

A candidate can upload multiple resumes.

A resume belongs to exactly one candidate.

### Important Rule

A candidate may only access their own resumes.

---

# 5. Resume Analysis

Stores general resume intelligence.

```text
resume_analyses
--------------------------------
id                UUID / BIGINT PK
resume_id         FK → resumes.id
status            VARCHAR NOT NULL
quality_score     INTEGER
summary           TEXT
skills            JSONB / TEXT
strengths         JSONB / TEXT
weaknesses        JSONB / TEXT
error_message     TEXT
created_at        TIMESTAMP NOT NULL
updated_at        TIMESTAMP
```

Supported statuses:

```text
PENDING
PROCESSING
COMPLETED
FAILED
```

### Relationship

For Version 1:

```text
Resume
   1
   │
   │
   0..1
ResumeAnalysis
```

A resume has at most one current general analysis.

Re-analysis/version history can be introduced later if needed.

### Score Constraint

When present:

```text
0 <= quality_score <= 100
```

---

# 6. Job

Represents a recruiter-created opportunity.

```text
jobs
--------------------------------
id                UUID / BIGINT PK
recruiter_id      FK → users.id
title             VARCHAR NOT NULL
description       TEXT NOT NULL
requirements      TEXT
required_skills   JSONB / TEXT
created_at        TIMESTAMP NOT NULL
updated_at        TIMESTAMP
```

### Relationship

```text
Recruiter
    1
    │
    │
    N
   Job
```

A recruiter may create multiple jobs.

A job belongs to one recruiter.

### Authorization Rule

Only the recruiter who owns the job may perform recruiter-specific management operations on it.

---

# 7. Job Match Analysis

Stores the result of comparing a resume with a job description.

```text
job_match_analyses
--------------------------------
id                  UUID / BIGINT PK
resume_id           FK → resumes.id
job_id              FK → jobs.id NULLABLE
external_job_text   TEXT NULLABLE
status              VARCHAR NOT NULL
match_score         INTEGER
matched_skills      JSONB / TEXT
missing_skills      JSONB / TEXT
missing_keywords    JSONB / TEXT
match_summary       TEXT
error_message       TEXT
created_at          TIMESTAMP NOT NULL
updated_at          TIMESTAMP
```

A job match can originate from either:

### Platform Job

```text
resume_id + job_id
```

or:

### External Job Description

```text
resume_id + external_job_text
```

The service layer must ensure that a valid job description source exists.

### Score Constraint

When present:

```text
0 <= match_score <= 100
```

### Ownership

The candidate requesting an analysis must own the selected resume.

---

# 8. Application

Represents a candidate applying to a platform job.

```text
applications
--------------------------------
id                UUID / BIGINT PK
candidate_id      FK → users.id
job_id            FK → jobs.id
resume_id         FK → resumes.id
status            VARCHAR NOT NULL
created_at        TIMESTAMP NOT NULL
updated_at        TIMESTAMP
```

Supported statuses:

```text
PENDING
SHORTLISTED
REJECTED
```

### Relationship

```text
Candidate ─────┐
               │
Resume ────────┼── Application ─── Job ─── Recruiter
               │
```

An application therefore records:

```text
WHO applied
+
WHERE they applied
+
WHICH resume they submitted
+
CURRENT application status
```

### Important Constraints

The candidate must own the selected resume.

A recruiter may only update applications belonging to jobs they own.

For Version 1, a candidate should not submit duplicate applications to the same job.

Conceptually:

```text
UNIQUE(candidate_id, job_id)
```

---

# 9. High-Level ER Diagram

```text
                     USER
                  ┌────┴────┐
                  │         │
             Candidate   Recruiter
                  │         │
                  │         │
                  │         └──────────────┐
                  │                        │
                  ▼                        ▼
               RESUME                    JOB
                  │                        │
          ┌───────┴────────┐               │
          │                │               │
          ▼                ▼               │
 RESUME_ANALYSIS    JOB_MATCH_ANALYSIS ◄───┘
          │                │
          │                │
          └────────────────┘

Candidate ───────┐
Resume ──────────┼──── APPLICATION ───── JOB
                 │
                 └──── status
```

More precisely:

```text
User 1 ───── N Resume

Resume 1 ─── 0..1 ResumeAnalysis

User(RECRUITER) 1 ───── N Job

Resume 1 ───── N JobMatchAnalysis

Job 1 ───── N JobMatchAnalysis
             (nullable for external JD analysis)

User(CANDIDATE) 1 ───── N Application

Job 1 ───── N Application

Resume 1 ───── N Application
```

---

# 10. Why Resume Analysis and Job Match Are Separate

They answer different questions.

### ResumeAnalysis

Answers:

> Is this generally a strong resume?

Input:

```text
Resume
```

Output:

```text
Quality Score
Summary
Skills
Strengths
Weaknesses
```

### JobMatchAnalysis

Answers:

> How well does this resume align with this particular job description?

Input:

```text
Resume + Job Description
```

Output:

```text
Match Score
Matched Skills
Missing Skills
Missing Keywords
Match Summary
```

Keeping these concepts separate prevents unrelated AI results from being mixed into one database entity.

---

# 11. REST API Conventions

Base path:

```text
/api
```

JSON is used for normal request and response bodies.

Resume upload uses:

```text
multipart/form-data
```

Protected requests require authentication.

Typical protected request:

```text
Authorization: Bearer <JWT>
```

API entities should be represented using DTOs rather than exposing JPA entities directly.

---

# 12. Authentication APIs

## Register Candidate

```text
POST /api/auth/register/candidate
```

Example request:

```json
{
  "name": "Deepthi",
  "email": "candidate@example.com",
  "password": "secure-password"
}
```

---

## Register Recruiter

```text
POST /api/auth/register/recruiter
```

Example request:

```json
{
  "name": "Recruiter",
  "email": "recruiter@example.com",
  "password": "secure-password"
}
```

---

## Login

```text
POST /api/auth/login
```

Request:

```json
{
  "email": "candidate@example.com",
  "password": "secure-password"
}
```

Response conceptually contains:

```json
{
  "accessToken": "<JWT>",
  "role": "CANDIDATE"
}
```

---

# 13. Resume APIs

All resume operations require authentication.

## Upload Resume

```text
POST /api/resumes
```

Role:

```text
CANDIDATE
```

Content type:

```text
multipart/form-data
```

---

## List My Resumes

```text
GET /api/resumes
```

Role:

```text
CANDIDATE
```

Returns resumes belonging to the authenticated candidate.

---

## Get Resume

```text
GET /api/resumes/{resumeId}
```

The backend must verify ownership.

---

# 14. General Resume Analysis APIs

## Start/Get Resume Analysis

Conceptual creation endpoint:

```text
POST /api/resumes/{resumeId}/analysis
```

Role:

```text
CANDIDATE
```

Backend:

1. verifies resume ownership;
2. ensures extracted text exists;
3. performs analysis;
4. validates Gemini response;
5. persists result.

---

## Get Analysis

```text
GET /api/resumes/{resumeId}/analysis
```

Returns analysis status and result.

Example completed response:

```json
{
  "status": "COMPLETED",
  "qualityScore": 82,
  "summary": "Backend-focused candidate...",
  "skills": [
    "Java",
    "Spring Boot",
    "React"
  ],
  "strengths": [
    "Relevant technical projects"
  ],
  "weaknesses": [
    "Few quantified achievements"
  ]
}
```

---

## Retry Failed Analysis

P1:

```text
POST /api/resumes/{resumeId}/analysis/retry
```

Only failed analyses may be retried through this operation.

---

# 15. Job Match APIs

## Analyze External Job Description

```text
POST /api/job-matches/external
```

Role:

```text
CANDIDATE
```

Request:

```json
{
  "resumeId": "resume-id",
  "jobDescription": "We are looking for a software engineer..."
}
```

Response:

```json
{
  "status": "COMPLETED",
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
  "matchSummary": "Strong backend alignment with some cloud gaps."
}
```

---

## Analyze Platform Job

```text
POST /api/jobs/{jobId}/match
```

Role:

```text
CANDIDATE
```

Request:

```json
{
  "resumeId": "resume-id"
}
```

The backend obtains the job description from the selected job.

---

# 16. Job APIs

## Create Job

```text
POST /api/jobs
```

Role:

```text
RECRUITER
```

Example:

```json
{
  "title": "Software Engineer Intern",
  "description": "We are looking for...",
  "requirements": "Currently pursuing a CS degree...",
  "requiredSkills": [
    "Java",
    "Spring Boot",
    "PostgreSQL"
  ]
}
```

---

## Browse Jobs

```text
GET /api/jobs
```

Accessible to authenticated candidates.

Pagination may be added if required.

---

## Get Job

```text
GET /api/jobs/{jobId}
```

---

## View My Recruiter Jobs

```text
GET /api/recruiter/jobs
```

Role:

```text
RECRUITER
```

Returns only jobs owned by the authenticated recruiter.

---

# 17. Application APIs

## Apply to Job

```text
POST /api/jobs/{jobId}/applications
```

Role:

```text
CANDIDATE
```

Request:

```json
{
  "resumeId": "resume-id"
}
```

Backend verifies:

* candidate owns resume;
* job exists;
* duplicate application does not exist.

Initial status:

```text
PENDING
```

---

## Candidate Applications

```text
GET /api/applications
```

Role:

```text
CANDIDATE
```

Returns applications belonging to the authenticated candidate.

---

## Recruiter Job Applicants

```text
GET /api/recruiter/jobs/{jobId}/applications
```

Role:

```text
RECRUITER
```

Backend verifies that the recruiter owns the job.

---

## Update Application Status

```text
PATCH /api/applications/{applicationId}/status
```

Role:

```text
RECRUITER
```

Request:

```json
{
  "status": "SHORTLISTED"
}
```

Allowed recruiter-selected outcomes:

```text
SHORTLISTED
REJECTED
```

The recruiter must own the associated job.

---

# 18. Standard HTTP Status Usage

Examples:

```text
200 OK
```

Successful retrieval/update.

```text
201 Created
```

Successful resource creation.

```text
400 Bad Request
```

Invalid input.

```text
401 Unauthorized
```

Authentication is missing or invalid.

```text
403 Forbidden
```

Authenticated user lacks permission.

```text
404 Not Found
```

Requested resource does not exist.

```text
409 Conflict
```

Examples:

* duplicate email;
* duplicate application.

```text
500 Internal Server Error
```

Unexpected internal failure.

External service failures may use an appropriate standardized error response.

---

# 19. Standard Error Shape

Backend errors should use a consistent structure.

Conceptually:

```json
{
  "status": 400,
  "code": "INVALID_RESUME",
  "message": "Only PDF resumes are supported.",
  "timestamp": "..."
}
```

This allows the React frontend to handle errors consistently.

---

# 20. Authorization Matrix

| Capability              | Candidate | Recruiter |
| ----------------------- | --------: | --------: |
| Upload resume           |       Yes |        No |
| View own resumes        |       Yes |        No |
| General resume analysis |       Yes |        No |
| Job match analysis      |       Yes |        No |
| Browse jobs             |       Yes |  Optional |
| Apply to job            |       Yes |        No |
| View own applications   |       Yes |        No |
| Create job              |        No |       Yes |
| View own jobs           |        No |       Yes |
| View applicants         |        No |       Yes |
| Shortlist/reject        |        No |       Yes |

Resource ownership checks remain necessary even when the role is correct.

---

# 21. Important Database Indexes

Version 1 should at minimum consider indexes for frequently queried identifiers such as:

```text
users.email
resumes.candidate_id
jobs.recruiter_id
applications.candidate_id
applications.job_id
job_match_analyses.resume_id
```

The duplicate application constraint should enforce:

```text
UNIQUE(candidate_id, job_id)
```

Indexes should support actual query patterns rather than being added indiscriminately.

---

# 22. Data Security Rules

The application must not:

* return password hashes;
* expose Gemini credentials;
* expose Supabase privileged credentials;
* allow arbitrary resume access;
* allow recruiters to inspect unrelated applications;
* trust candidate/recruiter identifiers supplied by the frontend when identity can be derived from authentication.

For example, the frontend should not be trusted to say:

```text
candidateId = 123
```

for an ownership-sensitive operation.

The backend should derive the current user from the authenticated JWT.

---

# 23. Design Summary

The Version 1 data model centers on six primary entities:

```text
User
Resume
ResumeAnalysis
Job
JobMatchAnalysis
Application
```

The design deliberately separates general resume intelligence from job-specific matching.

REST APIs expose business capabilities rather than database tables directly.

Authentication identifies the current user, while authorization combines role checks with resource ownership.

The resulting model is intentionally small enough for the MVP while supporting the complete candidate-to-recruiter workflow.
