# Academy Connect Project Structure

## Overview
Academy Connect is a Spring Boot application that manages academy-related functionalities including user management, consulting sessions, lunch matching, and more.

## Project Layout

```
academy-connect/
├── src/
│   └── main/
│       ├── java/store/mtvs/academyconnect/
│       │   ├── user/                  # User management
│       │   │   ├── domain/
│       │   │   │   ├── entity/       # User entities
│       │   │   │   └── enums/        # User-related enums
│       │   │   ├── dto/              # Data transfer objects
│       │   │   ├── infrastructure/    # Repositories
│       │   │   └── service/          # Business logic
│       │   │
│       │   ├── profile/              # User profile management
│       │   │   ├── domain/
│       │   │   ├── infrastructure/
│       │   │   └── service/
│       │   │
│       │   ├── consulting/           # Consulting session management
│       │   │   ├── domain/
│       │   │   │   └── entity/       # Consulting-related entities
│       │   │   ├── infrastructure/
│       │   │   └── service/
│       │   │
│       │   ├── counselingresult/     # Counseling results management
│       │   │   ├── domain/
│       │   │   │   └── entity/       # CounselingResult entity
│       │   │   ├── dto/              # DTOs for counseling results
│       │   │   ├── infrastructure/
│       │   │   │   └── repository/   # CounselingResult repository
│       │   │   └── service/          # Counseling results business logic
│       │   │
│       │   ├── lunchmatching/        # Lunch matching feature
│       │   │   ├── domain/
│       │   │   ├── infrastructure/
│       │   │   └── service/
│       │   │
│       │   ├── classgroup/           # Class group management
│       │   │   ├── domain/
│       │   │   ├── infrastructure/
│       │   │   └── service/
│       │   │
│       │   └── global/               # Global configurations
│       │       └── config/
│       │
│       └── resources/                # Application properties and resources
│
├── gradle/                           # Gradle wrapper files
├── sql/                             # SQL scripts and database schemas
│   ├── sql_tables.dbml              # Current database schema
│   └── init.sql                     # Database initialization script
│
├── docs/                            # Project documentation
│   ├── CURSOR_RULES.md              # Development guidelines and rules
│   └── PROJECT_STRUCTURE.md         # Project structure documentation
│
├── build.gradle                      # Gradle build configuration
├── docker-compose.yml               # Docker compose configuration
├── README.md                        # Project documentation
└── .gitignore                      # Git ignore rules

```

## Key Components

### 1. User Management (`user/`)
- User entity and authentication
- Role-based access control (Student, Instructor, Admin)
- User profile management

### 2. Profile Management (`profile/`)
- User profile information
- GitHub, blog, and contact information
- Profile markdown content

### 3. Consulting System (`consulting/`)
- Consulting session booking
- Time slot management
- Undefined consulting requests
- Integration with counseling results

### 4. Counseling Results (`counselingresult/`)
- Recording and managing counseling session outcomes
- Support for both scheduled and impromptu sessions
- Markdown-based content storage
- Relationships with users (student and instructor)
- Optional linking with consulting bookings
- Complete audit trail (creation, updates, deletion)

### 5. Lunch Matching (`lunchmatching/`)
- Lunch group matching
- Class-based matching rules
- Matching history

### 6. Class Group Management (`classgroup/`)
- Class group organization
- Class membership
- Class period management

### 7. Global Configuration (`global/`)
- Application-wide settings
- Security configurations
- Common utilities

## Database Structure
The database schema is documented in `sql_tables.dbml` and includes:
- User management tables
- Profile information
- Consulting session management
  - Booking management
  - Time slot availability
  - Counseling results tracking
    - Support for both scheduled and impromptu sessions
    - Student-Instructor relationships
    - Markdown content storage
    - Temporal tracking (counsel date, creation, updates)
- Lunch matching system
- Class group organization

## Documentation
- `CURSOR_RULES.md`: Development guidelines and coding standards
- `PROJECT_STRUCTURE.md`: Project organization and architecture
- Code-level documentation in JavaDoc format
- Database schema documentation in DBML format

## Build and Deployment
- Gradle-based build system
- Docker support for containerization
- SQL initialization scripts for database setup

## Development Guidelines
1. Follow the domain-driven design pattern
2. Maintain separation of concerns between layers
3. Use DTOs for data transfer
4. Implement proper error handling
5. Write comprehensive tests
6. Document API endpoints and significant changes
7. Follow the guidelines in CURSOR_RULES.md

## Getting Started
1. Clone the repository
2. Set up the database using init.sql
3. Configure application.properties
4. Run with Gradle: `./gradlew bootRun`
5. Access the application at `http://localhost:8080`

## Recent Changes
- Added counseling results tracking system
  - Entity with proper JPA mappings
  - Repository with optimized queries
  - Service layer with transaction management
  - DTOs for data transfer
- Support for both scheduled and impromptu counseling sessions
- Enhanced documentation structure with CURSOR_RULES.md
- Updated project structure documentation 