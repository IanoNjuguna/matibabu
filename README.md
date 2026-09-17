# Matibabu

**Matibabu** is an offline-first Electronic Medical Records (EMR) platform designed for healthcare facilities operating in environments where network connectivity cannot be assumed.

The system is being developed around a local-first clinical workflow: clinical data is persisted locally, clinical operations remain independent of continuous connectivity, and synchronization with a central system is being introduced as a separate architectural concern.

---

## Current Status

Matibabu is under active development.

The current backend includes working clinical and facility functionality, local persistence, authentication, database migrations, automated testing, and the initial architectural foundation for offline synchronization.

Current development is moving toward:

* Facility departmentalization
* Facility and department-level reporting
* Continued synchronization implementation
* Remote data exchange
* DHIS2 interoperability

---

## Current Capabilities

The backend currently contains functionality for:

* Patient management
* Clinical encounters
* Medical records
* Clinical observations
* Diagnoses
* Vitals
* Treatments
* Medicines
* ATC mapping and review
* Referrals
* Facility management
* Clinician authentication
* Role-based security
* CSRF protection
* Local SQLite persistence
* Flyway database migrations
* Repository adapters
* Automated tests
* Initial offline synchronization architecture

---

# Architecture

The backend follows a domain-oriented architecture that separates business rules, application orchestration, infrastructure, and security concerns.

```text
                         API
                          │
                          ▼
                  Application Layer
                  Use Cases / Services
                          │
                          ▼
                     Domain Layer
               Business Rules / Interfaces
                          ▲
                          │
                          │ implementations
                          │
                 Infrastructure Layer
              Persistence / External Systems
                          │
                          ▼
                       SQLite
```

The architectural goal is to keep clinical business rules independent of persistence technology, framework concerns, and external health-information systems.

---

# Repository Structure

```text
matibabu/
├── backend/
├── frontend/
├── database/
├── docs/
├── requests.http
└── README.md
```

The backend is currently the primary implementation area.

```text
backend/src/main/java/com/matibabu/backend/

├── api/
├── application/
├── domain/
├── infrastructure/
├── security/
├── config/
└── exception/
```

Domain capabilities are organized around clinical and operational concepts rather than being implemented as one large collection of framework-specific classes.

---

# Clinical Domains

## Patients

The patient domain currently supports:

* Patient registration
* Patient retrieval
* Patient listing
* Patient updates
* Patient deletion
* Phone-number search
* Validation
* Duplicate phone-number detection
* UUID-based patient identity
* Identity-document information
* Facility association

Patient behaviour is represented in the domain model, while persistence is handled through repository interfaces and infrastructure adapters.

Patient identity and timestamps are also part of the current synchronization design.

---

## Encounters

Encounters represent clinical interactions involving a patient.

The current implementation supports:

* Starting encounters
* Recording the attending clinician
* Encounter status
* Discharging encounters
* Cancelling encounters
* Retrieving encounters
* Persistence through repository adapters

Encounter lifecycle rules are handled by the domain model.

The attending clinician is recorded explicitly on the encounter rather than inferred from the persistence layer.

---

## Medical Records

The medical-record domain currently supports:

* Creating medical records
* Retrieving medical records
* Listing a patient's medical records
* Clinical observations
* Diagnoses
* Vitals
* Treatments

Medical records are associated with clinical encounters and patients.

The persistence implementation separates the medical-record aggregate from its database representation.

Treatment persistence is currently handled as part of the medical-record persistence boundary, with treatments re-synchronized in full when the medical record is saved.

---

## Medicines

Matibabu contains a medicine domain and supporting persistence functionality.

Current functionality includes:

* Medicine retrieval
* Medicine listing
* ATC classification metadata
* Identification of unresolved ATC mappings
* ATC mapping review
* ATC mapping resolution
* Mapping audit metadata

The medicine catalogue is kept separate from patient clinical records.

---

## Referrals

Referrals are represented as an independent domain concept with an explicit lifecycle.

The current implementation includes:

* Referral creation
* Referral retrieval
* Referral cancellation
* Referral completion
* Referral status
* Referral urgency
* Referring clinician
* Originating encounter
* Patient
* Diagnosis information
* Referral reason
* Receiving facility
* Receiving department information

Referral lifecycle transitions are handled through application services and domain rules.

---

# Facilities

Facility management is already implemented.

The current backend contains:

```text
api/facility/
application/facility/
domain/facility/
infrastructure/persistence/facility/
```

The facility domain includes:

* Facility creation
* Facility listing
* Facility deactivation
* Facility type
* MFL code
* Facility identity
* Facility association with clinical data

The persistence boundary includes:

```text
Facility
FacilityRepository
FacilityEntity
FacilityMapper
FacilityRepositoryAdapter
SpringDataFacilitiesRepository
```

Facility-related schema evolution is represented in the Flyway migration history, including:

```text
V19__create_facilities_table.sql
V20__restructure_referrals_table.sql
V21__add_facility_id_and_identity_documents.sql
```

Facility structure is the foundation for the next organizational modelling step: **departmentalization for reporting**.

---

# Security

Authentication and authorization are implemented using Spring Security.

The security boundary currently includes:

```text
security/
├── controllers/
│   ├── AdminController
│   ├── AuthController
│   ├── CsrfController
│   └── DemoController
│
├── CustomUserDetails.java
├── CustomUserDetailsService.java
├── SecurityConfig.java
│
├── entity/
│   ├── Clinician.java
│   └── Role.java
│
├── repository/
│   └── ClinicianRepository.java
│
└── services/
    ├── ClinicianService.java
    └── ClinicianServiceImpl.java
```

Security is kept outside the clinical domain.

CSRF protection is explicitly configured as part of the web security boundary.

---

# Offline-First Architecture

Offline-first operation is a core architectural requirement.

The system assumes that a facility may need to continue clinical operations when a central service or network connection is unavailable.

The local system therefore acts as the operational source for clinical activity:

```text
┌───────────────────┐
│   Clinical Client │
└─────────┬─────────┘
          │
          ▼
┌───────────────────┐
│ Local Application │
└─────────┬─────────┘
          │
          ▼
┌───────────────────┐
│      SQLite       │
└─────────┬─────────┘
          │
          │ synchronization
          ▼
┌───────────────────┐
│  Central / Remote │
│      System       │
└─────────┬─────────┘
          │
          ▼
┌───────────────────┐
│      DHIS2        │
└───────────────────┘
```

The local database is currently SQLite:

```text
jdbc:sqlite:./matibabu-local.db
```

This allows the clinical application to operate locally without requiring a continuously available remote database.

---

# Initial Synchronization Strategy

The synchronization architecture has already started to take shape.

The backend contains a dedicated `NodeIdentity` configuration component, and the synchronization design is documented through:

```text
docs/decisions/ADR-08-synchronization.md
```

The patient API and domain model also explicitly account for requirements established by the synchronization design.

This establishes an important concept for an offline-first system:

> A local installation is an identifiable node rather than an anonymous database replica.

Conceptually:

```text
                 Matibabu
                    │
             ┌──────┴──────┐
             │             │
          Node A         Node B
             │             │
          SQLite         SQLite
             │             │
             └──────┬──────┘
                    │
                    ▼
             Central System
```

The synchronization strategy is intentionally being developed incrementally.

### Currently established

* Node identity
* Synchronization-aware patient identity
* Synchronization-aware patient timestamps
* Synchronization architectural documentation
* Persistence decisions that account for re-synchronization

### Not yet implemented as a complete synchronization subsystem

* General-purpose sync service
* Outbox/change-log processing
* Push/pull protocol
* Sync checkpoints or cursors
* Conflict-resolution engine
* Retry/acknowledgement protocol
* Complete remote synchronization workflow

These remain part of the continuing synchronization work.

---

# Persistence

The current local persistence implementation uses:

* SQLite
* Spring Data JPA
* Hibernate
* Hibernate Community Dialects
* Flyway

The persistence architecture follows repository ports and infrastructure adapters.

```text
Domain Repository
       │
       ▼
Persistence Adapter
       │
       ▼
Spring Data Repository
       │
       ▼
Persistence Entity
       │
       ▼
SQLite
```

MapStruct is used where domain and persistence representations require mapping.

Persistence entities are not treated as the domain model.

---

# Database Migrations

Database schema evolution is managed through Flyway.

Migrations are stored under:

```text
backend/src/main/resources/db/migration/
```

The migration history covers the evolution of the system from the initial patient and encounter schema through:

* Patient details
* Medical records
* Clinical data
* Clinicians and sessions
* Medicines
* ATC metadata
* Referrals
* Facilities
* Facility associations
* Patient identity documents

The current migration sequence reaches:

```text
V21__add_facility_id_and_identity_documents.sql
```

Database changes are therefore explicit, versioned, and source controlled.

---

# Application Layer

Application behaviour is represented through use-case interfaces and service implementations.

For example:

```text
StartEncounterUseCase
        │
        ▼
StartEncounterService
        │
        ▼
EncounterRepository
```

This pattern is used across the major application areas:

* Patients
* Encounters
* Facilities
* Medical records
* Medicines
* Referrals

The application layer coordinates workflows without becoming the owner of domain rules.

---

# API

The backend exposes REST endpoints for the implemented clinical and operational capabilities.

Current API areas include:

```text
/api/patients
/api/encounters
/api/facilities
/api/medical-records
/api/medicines
/api/referrals
```

Authentication, administration, and CSRF-related endpoints are handled separately by the security layer.

API request and response models are kept separate from domain objects.

---

# Error Handling

API exception handling is centralized through:

```text
api/exception/GlobalExceptionHandler
```

The application defines specific exceptions for cases including:

* Patient not found
* Encounter not found
* Facility not found
* Medical record not found
* Medicine not found
* Referral not found
* Duplicate phone number
* Duplicate MFL code
* Invalid diagnosis reference
* Invalid referral state
* User not found
* Existing clinician conflicts

This keeps domain/application errors separate from HTTP response handling.

---

# Testing

Testing is performed at multiple levels.

### Domain tests

Verify domain behaviour and business invariants independently of infrastructure.

### Application tests

Verify application services and use-case orchestration.

These tests may use lightweight in-memory repository implementations where appropriate.

### Persistence tests

Verify:

* Entity mappings
* Repository adapters
* Database persistence
* Domain/persistence mapping

### API and integration tests

Verify behaviour across application boundaries, including:

* HTTP requests
* Validation
* Persistence
* Error handling
* Security integration

### Testing tools

The project uses:

* JUnit 5
* Spring Boot testing support
* Spring Data JPA testing support
* Mockito where appropriate

The testing strategy is deliberately mixed: some behaviour is tested with real implementations, some with lightweight in-memory implementations, and some dependencies are mocked when that provides an appropriate isolation boundary.

---

# Technology Stack

| Area                | Technology                           |
| ------------------- | ------------------------------------ |
| Language            | Java 25                              |
| Framework           | Spring Boot 4.1                      |
| Web                 | Spring MVC                           |
| Persistence         | Spring Data JPA                      |
| ORM                 | Hibernate                            |
| Local database      | SQLite                               |
| Database migrations | Flyway                               |
| SQLite support      | Hibernate Community Dialects         |
| Mapping             | MapStruct                            |
| Security            | Spring Security                      |
| Testing             | JUnit 5 / Spring Boot Test / Mockito |
| Build               | Maven Wrapper                        |
| Identifiers         | UUID Creator                         |

---

# Development Workflow

Matibabu generally follows this progression when introducing a capability:

```text
Requirement
     │
     ▼
Domain Model / Invariants
     │
     ▼
Application Use Case
     │
     ▼
Repository Port
     │
     ▼
Infrastructure Adapter
     │
     ▼
Database Migration
     │
     ▼
API
     │
     ▼
Tests
```

Architecturally significant decisions are documented through ADRs.

---

# Architectural Principles

## Domain independence

Clinical rules should not depend directly on:

* Spring
* JPA
* Hibernate
* SQLite
* Authentication infrastructure
* DHIS2

## Local-first operation

Clinical workflows should remain usable without continuous network connectivity.

## Explicit schema evolution

Database changes are versioned through Flyway migrations.

## Explicit boundaries

Clinical domains, application orchestration, persistence, security, and synchronization should remain independently understandable.

## External systems at the boundary

Remote systems and DHIS2-specific concerns should be isolated from the core clinical domain wherever practical.

## Incremental architecture

Architectural concepts are introduced as requirements become concrete rather than building large abstractions before they are needed.

---

# Current Development Direction

The current development path is:

```text
Clinical Workflows
       │
       ▼
Facility Model
       │
       ▼
Departmentalization
       │
       ▼
Facility / Department Reporting
       │
       ▼
Synchronization
       │
       ▼
Remote Infrastructure
       │
       ▼
DHIS2 Interoperability
```

### Current focus

**Departmentalization of facilities for reporting.**

The facility model already exists. The next modelling step is to determine how departments belong to facilities and how clinical activity should be attributed to those departments for reporting.

The departmental model should be established before reporting queries and aggregation logic are implemented.

---

# Roadmap

### Clinical

* Continue expanding clinical workflows
* Strengthen clinical validation and domain invariants

### Facility Management

* Departmentalize facilities
* Establish department-level clinical attribution
* Introduce facility and department reporting

### Synchronization

* Continue the initial synchronization implementation
* Establish change tracking
* Define synchronization protocol
* Implement remote synchronization
* Handle retries and failures
* Define conflict-resolution behaviour

### Interoperability

* Establish reporting mappings
* Integrate with DHIS2
* Separate DHIS2-specific concepts from the clinical domain

---

# Development Setup

## Requirements

* Java 25
* Git
* Maven Wrapper

A system-wide Maven installation is not required.

## Clone

```bash
git clone git@github.com:mfalme1k0/matibabu.git
cd matibabu/backend
```

## Verify Java

```bash
java -version
```

## Run tests

```bash
./mvnw clean test
```

## Run the backend

```bash
./mvnw spring-boot:run
```

The local backend uses SQLite for development.

---

# Documentation

Architectural decisions are maintained under:

```text
docs/
```

Synchronization architecture is currently documented in:

```text
docs/decisions/ADR-08-synchronization.md
```

ADRs are reserved for decisions with meaningful long-term architectural consequences rather than every implementation detail.

---

## License

See [`LICENSE`](LICENSE) for licensing information.
