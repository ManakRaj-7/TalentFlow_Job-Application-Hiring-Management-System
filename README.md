# TalentFlow – Job Application & Hiring Management System

A comprehensive backend REST API built with Spring Boot for managing job postings, applications, and the hiring process. This is a pure backend solution designed to be consumed by any frontend application or API client like Postman.

## 🏗️ Architecture

The project follows a clean layered architecture with clear separation of concerns:

```
com.talentflow
├── config          # Configuration classes (Security, OpenAPI)
├── controller      # REST API endpoints
├── dto             # Data Transfer Objects (request/response)
│   ├── request
│   └── response
├── entity          # JPA entities
├── enums           # Enumeration types
├── exception       # Custom exceptions and global handler
├── repository      # Data access layer
├── security        # JWT authentication and authorization
└── service         # Business logic layer
```

## 🔐 Authentication & Security

### JWT-Based Authentication
- **Token-based stateless authentication** using JWT (JSON Web Tokens)
- **BCrypt password hashing** for secure password storage
- **Role-based access control** with three roles:
  - `ADMIN`: Full system access
  - `RECRUITER`: Can post jobs and manage applications
  - `CANDIDATE`: Can apply for jobs and view own applications

### Security Features
- JWT filter for request authentication
- Stateless sessions (no server-side session storage)
- Role-based endpoint protection
- Secure password encoding with BCrypt

## � Live API Documentation

### Swagger UI
Once the application is running, you can interact with the API directly using Swagger UI:

**URL:** `http://localhost:8080/swagger-ui/index.html`

#### API Home Page
![TalentFlow API Swagger UI](screenshots/01-swagger-ui-home.png)

The Swagger UI provides:
- ✅ Complete API documentation
- ✅ Request/response examples
- ✅ Interactive "Try it out" feature to test endpoints
- ✅ JWT authentication support (click "Authorize" button)
- ✅ Real-time API testing without external tools

**How to use Swagger UI:**
1. Navigate to `http://localhost:8080/swagger-ui/index.html`
2. Click the green **"Authorize"** button
3. Paste your JWT token in the `Bearer` field
4. Click any endpoint to expand it
5. Click **"Try it out"** to test the endpoint
6. View request/response details

---

### ✅ Authentication & Login

#### Login Endpoint Request
![Login Request Form](screenshots/02-login-request.png)

The login endpoint accepts email and password credentials:
```json
{
  "email": "abc@gmail.com",
  "password": "password123"
}
```

#### Successful Login Response
![Login Success Response](screenshots/03-login-response-success.png)

**Response contains:**
- ✅ `token` — JWT token for authenticated requests
- ✅ `email` — User email
- ✅ `role` — User role (ADMIN, RECRUITER, JOB_SEEKER)
- ✅ `fullName` — User's full name
- ✅ `userId` — User ID in database

**Token Usage:**
Copy the token and use it in the `Authorization` header for all protected endpoints:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---
### 📑 Get All Jobs (Public Endpoint)

#### Jobs API Endpoint
![Get All Jobs API](screenshots/04-get-all-jobs.png)

**This endpoint:**
- ✅ Retrieves paginated list of all job postings
- ✅ Supports pagination (page, size parameters)
- ✅ Supports sorting (sortBy, sortDir parameters)
- ✅ **No authentication required** (public endpoint)
- ✅ Returns real job data from database

**Example Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "title": "Senior Java Developer",
      "description": "5+ years experience required",
      "company": "TechCorp",
      "location": "Remote",
      "salary": "120000-150000",
      "employmentType": "FULL_TIME",
      "status": "OPEN"
    }
  ]
}
```

---
## �📋 API Endpoints

### Authentication APIs

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "CANDIDATE"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "email": "john@example.com",
    "role": "CANDIDATE",
    "fullName": "John Doe",
    "userId": 1
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:** Same structure as register response

### Job APIs

#### Create Job (RECRUITER/ADMIN only)
```http
POST /api/jobs
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Senior Java Developer",
  "description": "We are looking for an experienced Java developer...",
  "location": "New York, NY",
  "employmentType": "FULL_TIME",
  "requiredSkills": ["Java", "Spring Boot", "MySQL"],
  "experienceLevel": "5+ years"
}
```

#### Get All Jobs (Public)
```http
GET /api/jobs?page=0&size=10&sortBy=createdAt&sortDir=DESC
```

#### Search Jobs (Public)
```http
GET /api/jobs/search?skill=Java&location=New York&status=OPEN&page=0&size=10
```

#### Update Job (RECRUITER/ADMIN only)
```http
PUT /api/jobs/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Updated Job Title",
  "description": "Updated description...",
  "location": "Remote",
  "employmentType": "FULL_TIME",
  "requiredSkills": ["Java", "Spring"],
  "experienceLevel": "3+ years"
}
```

#### Delete Job (RECRUITER/ADMIN only)
```http
DELETE /api/jobs/{id}
Authorization: Bearer {token}
```

### Application APIs

#### Apply for Job (CANDIDATE only)
```http
POST /api/applications/apply/{jobId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "resumeLink": "https://example.com/resume.pdf"
}
```

#### Get My Applications (CANDIDATE only)
```http
GET /api/applications/my
Authorization: Bearer {token}
```

#### Get Applications for Job (RECRUITER/ADMIN only)
```http
GET /api/applications/job/{jobId}
Authorization: Bearer {token}
```

#### Update Application Status (RECRUITER/ADMIN only)
```http
PUT /api/applications/{applicationId}/status
Authorization: Bearer {token}
Content-Type: application/json

{
  "status": "SHORTLISTED"
}
```

**Status values:** `APPLIED`, `SHORTLISTED`, `REJECTED`, `HIRED`

## 🛢️ Database

### MySQL Configuration
The application uses MySQL database. Update the connection details in `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/talentflow_db?createDatabaseIfNotExist=true
    username: root
    password: root
```

### Database Schema
The application uses JPA with Hibernate's `ddl-auto: update`, which automatically creates/updates tables based on entities.

**Key Tables:**
- `users`: User accounts with roles
- `jobs`: Job postings
- `applications`: Job applications
- `job_skills`: Job required skills (collection table)

### Relationships
- **User → Job**: One-to-Many (One recruiter can post many jobs)
- **User → Application**: One-to-Many (One candidate can have many applications)
- **Job → Application**: One-to-Many (One job can have many applications)

## 🚀 Getting Started

### Prerequisites
- **Java 20** (Oracle JDK 20)
- **Maven 3.6+**
- **MySQL 8.0+**
- **IDE** (IntelliJ IDEA, Eclipse, or VS Code)

### Setup Steps

1. **Clone/Download the project**

2. **Configure MySQL**
   - Create a MySQL database (or let the app create it automatically)
   - Update `application.yml` with your MySQL credentials:
     ```yaml
     spring:
       datasource:
         username: your_username
         password: your_password
     ```

3. **Configure JWT Secret** (Optional but recommended)
   - Update `application.yml` or set environment variable:
     ```yaml
     jwt:
       secret: your-256-bit-secret-key-minimum-32-characters
     ```
   - Or set environment variable: `JWT_SECRET=your-secret-key`

4. **Build the project**
   ```bash
   mvn clean install
   ```

5. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   Or run the `TalentFlowApplication` class from your IDE.

6. **Access the API**
   - Base URL: `http://localhost:8080`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - API Docs: `http://localhost:8080/api-docs`

## 📚 API Documentation

### Swagger/OpenAPI
The application includes Swagger UI for interactive API documentation:
- **URL**: `http://localhost:8080/swagger-ui.html`
- **Features**:
  - Test endpoints directly from the browser
  - JWT authentication support
  - Complete API documentation with request/response examples

### Using JWT in Swagger
1. Register/Login to get a JWT token
2. Click the "Authorize" button in Swagger UI
3. Enter: `Bearer {your-token}`
4. Now you can test protected endpoints

## 📝 Sample Postman Requests

### 1. Register a Recruiter
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "fullName": "Jane Recruiter",
  "email": "recruiter@example.com",
  "password": "password123",
  "role": "RECRUITER"
}
```

### 2. Login
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "recruiter@example.com",
  "password": "password123"
}
```
**Copy the token from the response.**

### 3. Create a Job (Use token from step 2)
```http
POST http://localhost:8080/api/jobs
Authorization: Bearer {paste-token-here}
Content-Type: application/json

{
  "title": "Full Stack Developer",
  "description": "We need a full stack developer with React and Spring Boot experience.",
  "location": "San Francisco, CA",
  "employmentType": "FULL_TIME",
  "requiredSkills": ["React", "Spring Boot", "PostgreSQL"],
  "experienceLevel": "3-5 years"
}
```

### 4. Register a Candidate
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "fullName": "John Candidate",
  "email": "candidate@example.com",
  "password": "password123",
  "role": "CANDIDATE"
}
```

### 5. Apply for Job (Use candidate token)
```http
POST http://localhost:8080/api/applications/apply/1
Authorization: Bearer {candidate-token}
Content-Type: application/json

{
  "resumeLink": "https://drive.google.com/resume.pdf"
}
```

### 6. View Applications for a Job (Use recruiter token)
```http
GET http://localhost:8080/api/applications/job/1
Authorization: Bearer {recruiter-token}
```

### 7. Update Application Status
```http
PUT http://localhost:8080/api/applications/1/status
Authorization: Bearer {recruiter-token}
Content-Type: application/json

{
  "status": "SHORTLISTED"
}
```

## ⚠️ Exception Handling

The application includes a global exception handler that returns consistent error responses:

- **ResourceNotFoundException** → 404 Not Found
- **UnauthorizedAccessException** → 403 Forbidden
- **ValidationException** → 400 Bad Request
- **BadCredentialsException** → 401 Unauthorized
- **MethodArgumentNotValidException** → 400 Bad Request (with field errors)

**Error Response Format:**
```json
{
  "success": false,
  "message": "Error message here",
  "data": null,
  "timestamp": "2024-01-15T10:30:00"
}
```

## ✅ Validation

All request DTOs are validated using Jakarta Validation:
- `@NotNull`, `@NotBlank`, `@Email`, `@Size`, `@Valid`
- Validation errors are automatically handled and returned with field-level details

## 🔧 Configuration

### Application Properties
Key configurations in `application.yml`:
- **Server Port**: 8080
- **Database**: MySQL with auto-create
- **JWT Expiration**: 24 hours (86400000 ms)
- **Logging**: DEBUG level for application, SQL queries visible

### Environment Variables
- `JWT_SECRET`: Override JWT secret key (default provided but should be changed in production)

## 📦 Project Structure Details

### Entities
- **User**: Represents users (ADMIN, RECRUITER, CANDIDATE)
- **Job**: Job postings with skills, location, employment type
- **Application**: Job applications linking candidates to jobs

### Services
- **AuthService**: Handles registration and authentication
- **JobService**: Manages job CRUD operations and search
- **ApplicationService**: Handles application submission and status updates

### Security
- **JwtTokenProvider**: JWT token generation and validation
- **CustomUserDetailsService**: User details loading for Spring Security
- **JwtAuthenticationFilter**: Filters requests and validates JWT tokens
- **SecurityConfig**: Spring Security configuration with role-based access

## 🧪 Testing

### Manual Testing
1. Use **Swagger UI** for interactive testing
2. Use **Postman** with the sample requests above
3. Use **cURL** commands

### Example cURL
```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Test User","email":"test@example.com","password":"password123","role":"CANDIDATE"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

## 📌 Important Notes

1. **JWT Secret**: Change the default JWT secret in production
2. **Database**: Ensure MySQL is running before starting the application
3. **Port**: Default port is 8080, change if needed in `application.yml`
4. **CORS**: If connecting from a frontend, configure CORS in `SecurityConfig`
5. **Password**: Minimum 6 characters required
6. **Email**: Must be unique across all users

## 🎯 Features Implemented

✅ JWT-based authentication  
✅ Role-based authorization (ADMIN, RECRUITER, CANDIDATE)  
✅ User registration and login  
✅ Job CRUD operations with pagination and sorting  
✅ Job search by skill, location, and status  
✅ Application submission and management  
✅ Application status updates  
✅ Global exception handling  
✅ Request validation  
✅ Swagger/OpenAPI documentation  
✅ Clean layered architecture  
✅ SOLID principles  
✅ Proper logging  
✅ MySQL database with JPA/Hibernate  

## 📄 License

This project is provided as-is for educational and development purposes.

## 🤝 Support

For issues or questions, please refer to the API documentation at `/swagger-ui.html` or check the application logs.

---

**Built with Spring Boot 3.2.0 and Java 20**

