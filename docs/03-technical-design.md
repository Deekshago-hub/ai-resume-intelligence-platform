# AI Resume Intelligence Platform

## Technical Design & Architecture

**Version:** 1.0
**Status:** Approved
**Related Documents:** `01-project-vision.md`, `02-srs.md`

---

## 1. Purpose

This document defines the high-level technical architecture of the AI Resume Intelligence Platform.

The architecture is designed to support:

* candidate and recruiter authentication;
* resume upload and processing;
* general resume intelligence;
* job-specific AI matching;
* job management;
* applications;
* secure file storage;
* structured AI integration;
* testing and deployment.

Version 1 prioritizes maintainability and clear module boundaries without introducing infrastructure unnecessary for an MVP.

---

# 2. Architecture Style

The backend will use a **modular monolithic architecture**.

This means the entire backend runs as one Spring Boot application, while the codebase is internally separated into business modules.

Conceptually:

```text
Spring Boot Application
│
├── Authentication
├── Users
├── Resumes
├── Resume Analysis
├── Job Matching
├── Jobs
└── Applications
```

## Why a Modular Monolith?

Version 1 does not require independently deployed services.

A modular monolith provides:

* simpler development;
* simpler deployment;
* easier debugging;
* fewer infrastructure requirements;
* clear business boundaries;
* easier database transactions;
* sufficient scalability for the MVP.

Microservices would introduce unnecessary complexity such as service discovery, distributed communication, multiple deployments, distributed tracing, and additional DevOps requirements.

Modules can be extracted into independent services later if actual scaling requirements justify it.

---

# 3. High-Level Architecture

```text
                        USERS
                          │
              ┌───────────┴───────────┐
              │                       │
          Candidate                Recruiter
              │                       │
              └───────────┬───────────┘
                          │
                          ▼
              React 19 + TypeScript
                   Vite Frontend
                          │
                       HTTPS
                    REST / JSON
                          │
                          ▼
                 Spring Boot API
                          │
                  Spring Security
                     JWT / RBAC
                          │
          ┌───────────────┼─────────────────┐
          │               │                 │
          ▼               ▼                 ▼
     PostgreSQL       Supabase           Gemini API
      Database         Storage
                          │
                          ▼
                     Resume PDFs
```

The frontend never directly communicates with Gemini.

Gemini API credentials remain on the backend.

---

# 4. Frontend Architecture

## Technology

The frontend uses:

* React 19
* TypeScript
* Vite
* Tailwind CSS
* shadcn/ui
* TanStack Query
* React Hook Form
* Zod

## Responsibilities

The frontend is responsible for:

* rendering UI;
* collecting user input;
* client-side validation;
* communicating with backend APIs;
* displaying loading/error/success states;
* maintaining authentication state;
* candidate/recruiter navigation;
* displaying resume and job-match analysis.

The frontend should not contain critical authorization or business rules.

Those rules must be enforced by the backend.

---

# 5. Frontend Feature Organization

The frontend should primarily be organized by feature.

Conceptually:

```text
frontend/src/

├── components/
│   └── reusable UI components
│
├── features/
│   ├── auth/
│   ├── resumes/
│   ├── analysis/
│   ├── job-match/
│   ├── jobs/
│   └── applications/
│
├── layouts/
│   ├── CandidateLayout
│   └── RecruiterLayout
│
├── pages/
├── lib/
├── hooks/
├── types/
└── routes/
```

The exact folder structure will be finalized during project initialization.

---

# 6. Frontend Data Management

## TanStack Query

TanStack Query will manage server-side data such as:

* resumes;
* resume analysis;
* jobs;
* applications;
* job-match results.

It provides useful functionality including:

* loading states;
* error states;
* caching;
* refetching;
* request lifecycle management.

## React Hook Form

React Hook Form will manage forms such as:

* registration;
* login;
* job creation;
* job-description input.

## Zod

Zod will provide frontend schema validation.

Backend validation remains mandatory because client-side validation can be bypassed.

---

# 7. Backend Architecture

The Spring Boot backend follows layered responsibilities inside business modules.

Typical request flow:

```text
HTTP Request
     │
     ▼
Controller
     │
     ▼
Service
     │
     ▼
Repository / External Client
     │
     ▼
Database / External Service
```

## Controller Layer

Responsible for:

* receiving HTTP requests;
* validating request DTOs;
* invoking application services;
* returning API responses.

Controllers should contain minimal business logic.

## Service Layer

Responsible for:

* business rules;
* workflow orchestration;
* ownership checks;
* coordinating repositories;
* coordinating external integrations.

## Repository Layer

Responsible for persistence through Spring Data JPA.

## Integration Layer

Responsible for communication with external systems including:

* Gemini;
* Supabase Storage.

---

# 8. Backend Module Organization

Conceptually:

```text
backend/

└── src/main/java/.../

    ├── auth/
    │   ├── controller/
    │   ├── service/
    │   └── dto/
    │
    ├── user/
    │
    ├── resume/
    │   ├── controller/
    │   ├── service/
    │   ├── repository/
    │   ├── entity/
    │   └── dto/
    │
    ├── analysis/
    │
    ├── jobmatch/
    │
    ├── job/
    │
    ├── application/
    │
    ├── integration/
    │   ├── gemini/
    │   └── storage/
    │
    ├── security/
    │
    ├── exception/
    │
    └── config/
```

This is a feature-oriented modular structure rather than one global folder containing every controller, another containing every service, etc.

---

# 9. Authentication Architecture

Authentication uses Spring Security and JWT.

## Login Flow

```text
User
 │
 ▼
POST /auth/login
 │
 ▼
Spring Boot
 │
 ▼
Verify credentials
 │
 ▼
Generate JWT
 │
 ▼
Return token
```

Subsequent protected requests include the token.

```text
Frontend

Authorization:
Bearer <JWT>

        │
        ▼

Spring Security

        │
        ▼

Validate token

        │
        ▼

Determine authenticated user

        │
        ▼

Check role / ownership

        │
    ┌───┴────┐
    ▼        ▼
 Allowed   Rejected
```

Roles:

```text
CANDIDATE
RECRUITER
```

Passwords will be hashed using a Spring Security-supported secure password encoder.

---

# 10. Authorization Strategy

Authorization occurs on the backend.

Examples:

A candidate may:

* upload their own resume;
* access their own analysis;
* create their own job-match analysis;
* submit applications.

A recruiter may:

* create jobs;
* access jobs they own;
* access applicants to their jobs;
* update application statuses for their jobs.

Authentication answers:

> Who is the user?

Authorization answers:

> Is this user allowed to perform this action?

Both role checks and resource ownership may therefore be required.

---

# 11. Resume Upload Architecture

Resume processing follows:

```text
Candidate
    │
    ▼
Upload PDF
    │
    ▼
Spring Boot
    │
    ├── Validate MIME/type
    ├── Validate size
    └── Validate non-empty
    │
    ▼
Supabase Storage
    │
    ▼
Store PDF
    │
    ▼
PostgreSQL
    │
    ▼
Store Resume Metadata
    │
    ▼
Extract Text
```

The actual PDF is stored in object storage.

PostgreSQL stores metadata such as:

* resume ID;
* owner;
* original filename;
* storage location/key;
* upload timestamp;
* extracted text or processing metadata as determined during database design.

---

# 12. Why Object Storage Instead of PostgreSQL?

PDF files are binary objects.

PostgreSQL is primarily used for structured relational information.

Therefore:

```text
PostgreSQL

Resume
-------
id
candidate_id
filename
storage_key
created_at
```

while:

```text
Supabase Storage

resumes/
    candidate-id/
        resume-file.pdf
```

stores the actual file.

This keeps database records focused on application data while object storage handles files.

---

# 13. PDF Text Extraction

Gemini analysis requires textual resume content.

Processing flow:

```text
PDF
 │
 ▼
PDF Text Extractor
 │
 ▼
Plain Text
 │
 ▼
AI Analysis
```

A Java PDF-processing library will be selected during implementation.

Version 1 supports text-based PDFs.

OCR for scanned/image-only PDFs is outside the MVP scope.

The system should detect or report cases where meaningful text cannot be extracted.

---

# 14. General Resume Intelligence Architecture

General analysis does not require a job description.

```text
Stored Resume
      │
      ▼
Extracted Text
      │
      ▼
ResumeAnalysisService
      │
      ▼
AIAnalysisClient
      │
      ▼
Gemini API
      │
      ▼
Structured AI Response
      │
      ▼
Validation
      │
      ▼
PostgreSQL
      │
      ▼
Candidate Dashboard
```

Expected information includes:

* Resume Quality Score;
* Summary;
* Skills;
* Strengths;
* Weaknesses.

---

# 15. Job Match Architecture

Job matching requires:

```text
Resume Text
     +
Job Description
```

The job description can come from:

### External Job

The candidate manually pastes a job description.

or

### Platform Job

The description comes from a recruiter-created job.

Flow:

```text
Resume Text
     │
     │
     ├──────────────┐
     │              │
     ▼              ▼
              Job Description
                     │
          ┌──────────┘
          ▼
    JobMatchService
          │
          ▼
    AIAnalysisClient
          │
          ▼
       Gemini
          │
          ▼
 Structured Response
          │
          ▼
      Validation
          │
          ▼
   Job Match Result
```

Expected information:

* Job Match Score;
* Matched Skills;
* Missing Skills;
* Missing Keywords;
* Match Summary.

---

# 16. Avoiding Repeated PDF Processing

PDF extraction should not be repeated every time the candidate analyzes the same resume against another job.

Preferred flow:

```text
Upload PDF
    │
    ▼
Extract once
    │
    ▼
Persist reusable extracted content
    │
    ├── General Analysis
    │
    ├── Job A Match
    │
    ├── Job B Match
    │
    └── External JD Match
```

This reduces unnecessary processing and simplifies repeated analysis.

The exact persistence approach will be finalized during database design.

---

# 17. Gemini Integration Architecture

Gemini integration should be isolated from business logic.

Avoid:

```text
ResumeController
      │
      ▼
Direct Gemini HTTP Call
```

Preferred:

```text
ResumeController
      │
      ▼
ResumeAnalysisService
      │
      ▼
AIAnalysisClient
      │
      ▼
GeminiClient
      │
      ▼
Gemini API
```

This separation provides:

* easier testing;
* cleaner business logic;
* centralized Gemini configuration;
* easier error handling;
* easier replacement of Gemini later.

Version 1 directly integrates Gemini without LangChain4j.

---

# 18. Structured AI Response Strategy

Gemini output is treated as untrusted external data.

The backend requests structured JSON.

Example:

```json
{
  "qualityScore": 82,
  "summary": "Backend-focused candidate...",
  "skills": ["Java", "Spring Boot"],
  "strengths": ["Relevant projects"],
  "weaknesses": ["Few quantified achievements"]
}
```

The response should then follow:

```text
Gemini Response
      │
      ▼
Deserialize
      │
      ▼
Validate
      │
   ┌──┴───┐
   ▼      ▼
 Valid  Invalid
   │      │
 Store   Fail safely
```

AI-generated data should never be assumed valid merely because the API request succeeded.

---

# 19. AI Processing State

AI operations should have explicit states.

```text
PENDING
   │
   ▼
PROCESSING
   │
 ┌─┴─────────┐
 ▼           ▼
COMPLETED   FAILED
```

The UI should display the corresponding state.

Examples:

`PROCESSING`

> Analyzing your resume...

`FAILED`

> We couldn't complete the analysis. Please try again.

This prevents indefinite loading states.

---

# 20. Job and Application Architecture

A recruiter creates a job.

```text
Recruiter
    │
    ▼
Job
```

A candidate applies:

```text
Candidate
   │
   ├──── Resume
   │
   ▼
Application
   │
   ▼
Job
```

An application conceptually associates:

```text
Candidate
+
Job
+
Resume
+
Status
```

Status values:

```text
PENDING
SHORTLISTED
REJECTED
```

Recruiters may only manage applications belonging to jobs they own.

---

# 21. Database Architecture

PostgreSQL hosted on Supabase will serve as the primary relational database.

Primary concepts include:

```text
User
Resume
ResumeAnalysis
Job
JobMatchAnalysis
Application
```

Relationships and exact columns will be finalized in `04-data-api-design.md`.

Spring Data JPA and Hibernate will provide persistence access.

---

# 22. Error Handling Architecture

The backend will use centralized exception handling.

Conceptually:

```text
Application Exception
       │
       ▼
Global Exception Handler
       │
       ▼
Standard API Error
```

Example API error:

```json
{
  "status": 400,
  "code": "INVALID_RESUME",
  "message": "Only PDF resumes are supported."
}
```

Expected categories include:

* validation errors;
* authentication errors;
* authorization errors;
* resource-not-found errors;
* storage failures;
* PDF-processing failures;
* AI-service failures;
* malformed AI responses.

The frontend should display useful messages without exposing internal implementation details.

---

# 23. API Design Principles

REST APIs will:

* use nouns for resources;
* use appropriate HTTP methods;
* use appropriate HTTP status codes;
* accept and return DTOs;
* validate request payloads;
* return consistent errors.

Example conceptual routes:

```text
POST   /api/auth/register
POST   /api/auth/login

POST   /api/resumes
GET    /api/resumes
GET    /api/resumes/{id}

POST   /api/resumes/{id}/analysis

POST   /api/job-matches

POST   /api/jobs
GET    /api/jobs
GET    /api/jobs/{id}

POST   /api/jobs/{id}/applications
GET    /api/applications
```

The final endpoint contracts will be defined in the Data & API Design document.

---

# 24. Configuration and Secrets

Sensitive configuration must not be committed to Git.

Examples:

```text
DATABASE_URL
DATABASE_USERNAME
DATABASE_PASSWORD

JWT_SECRET

GEMINI_API_KEY

SUPABASE_URL
SUPABASE_STORAGE_KEY
```

Development and production environments will provide configuration through environment variables.

A safe `.env.example` or documented configuration template may be provided without real credentials.

---

# 25. Deployment Architecture

```text
                    Internet
                       │
             ┌─────────┴─────────┐
             │                   │
             ▼                   ▼
          Vercel               Render
             │                   │
             ▼                   ▼
       React Frontend      Spring Boot API
                                 │
                    ┌────────────┼─────────────┐
                    ▼            ▼             ▼
                PostgreSQL    Storage       Gemini
                 Supabase     Supabase        API
```

Frontend:

```text
React → Vercel
```

Backend:

```text
Spring Boot → Docker → Render
```

Database:

```text
PostgreSQL → Supabase
```

File storage:

```text
Supabase Storage
```

---

# 26. CI Strategy

GitHub Actions will provide lightweight continuous integration.

For example:

```text
Push / Pull Request
        │
        ▼
GitHub Actions
        │
    ┌───┴────┐
    ▼        ▼
 Backend   Frontend
    │        │
 Build     Build
 Tests     Validation
    │        │
    └───┬────┘
        ▼
       Pass
```

Version 1 does not require a complex enterprise deployment pipeline.

The primary goal is automatically detecting broken builds and failing tests.

---

# 27. Testing Strategy

Testing priority:

### High Priority

* authentication;
* authorization;
* resume ownership;
* job ownership;
* resume processing;
* AI-response parsing/validation;
* application business rules.

### Lower Priority

Pure presentation components and trivial framework-generated behavior do not require excessive testing for the MVP.

External services should be mocked where practical in automated tests rather than repeatedly calling real Gemini or storage services.

---

# 28. Key Technology Decisions

| Area             | Choice                | Reason                                          |
| ---------------- | --------------------- | ----------------------------------------------- |
| Frontend         | React + TypeScript    | Component-based UI with static typing           |
| Build Tool       | Vite                  | Fast modern frontend tooling                    |
| UI               | Tailwind + shadcn/ui  | Rapid, reusable SaaS-style UI development       |
| Server State     | TanStack Query        | API caching and request-state management        |
| Forms            | React Hook Form + Zod | Form handling and client validation             |
| Backend          | Spring Boot           | Mature Java backend ecosystem                   |
| Security         | Spring Security + JWT | Stateless API authentication and RBAC           |
| Persistence      | Spring Data JPA       | Productive relational persistence               |
| Database         | PostgreSQL            | Strong relational model and production usage    |
| Database Hosting | Supabase              | Managed PostgreSQL suitable for MVP             |
| File Storage     | Supabase Storage      | Object storage for uploaded PDFs                |
| AI               | Gemini API            | Direct LLM integration with structured analysis |
| API Docs         | OpenAPI/Swagger       | Discoverable REST API documentation             |
| Containers       | Docker                | Reproducible backend runtime                    |
| CI               | GitHub Actions        | Automated repository-integrated validation      |
| Frontend Hosting | Vercel                | Simple React deployment                         |
| Backend Hosting  | Render                | Straightforward backend/container deployment    |

---

# 29. Explicitly Rejected Architecture

Version 1 intentionally avoids:

* microservices;
* API gateway;
* Kafka;
* Kubernetes;
* Redis unless an actual requirement emerges;
* event-driven distributed architecture;
* multiple databases;
* multiple AI providers;
* LangChain4j.

These technologies are not inherently bad.

They are excluded because Version 1 has no requirement that justifies their complexity.

---

# 30. Architecture Summary

The system uses a React frontend communicating with a modular Spring Boot backend through REST APIs.

Spring Security provides authentication and authorization.

PostgreSQL stores structured application data.

Supabase Storage stores uploaded PDF resumes.

Resume text is extracted once and reused for AI operations.

Gemini provides two distinct AI capabilities:

1. general resume intelligence;
2. job-specific resume matching.

Gemini integration is isolated behind an application boundary, and AI responses are validated before being trusted.

The architecture intentionally favors a well-structured modular monolith over distributed systems because it provides the appropriate balance of maintainability, learning value, deployment simplicity, and development speed for Version 1.
