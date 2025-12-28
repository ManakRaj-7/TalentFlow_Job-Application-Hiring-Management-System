# How to Use TalentFlow API

This guide explains how developers and users can integrate with and use the TalentFlow Job Application & Hiring Management System API.

---

## Table of Contents
1. [Authentication Flow](#authentication-flow)
2. [API Endpoints & Usage](#api-endpoints--usage)
3. [Testing Tools](#testing-tools)
4. [Code Examples](#code-examples)
5. [Common Scenarios](#common-scenarios)
6. [Error Handling](#error-handling)
7. [Rate Limiting & Security](#rate-limiting--security)

---

## Authentication Flow

The TalentFlow API uses **JWT (JSON Web Token)** authentication. All protected endpoints require a valid token.

### Step 1: Register a New User

**Endpoint:** `POST /api/auth/register`  
**Public:** Yes (no authentication required)

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "Password123!"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "role": "JOB_SEEKER"
  }
}
```

**Response Codes:**
- `200 OK` — Registration successful
- `400 Bad Request` — Invalid input or user already exists
- `500 Internal Server Error` — Server error

---

### Step 2: Login & Get JWT Token

**Endpoint:** `POST /api/auth/login`  
**Public:** Yes (no authentication required)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "Password123!"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
  }
}
```

**Save the token for future requests:**
```powershell
$token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## API Endpoints & Usage

### **PUBLIC ENDPOINTS** (No authentication required)

#### Get All Jobs
**Endpoint:** `GET /api/jobs`

```bash
curl -X GET http://localhost:8080/api/jobs
```

**Optional Query Parameters:**
```bash
curl -X GET "http://localhost:8080/api/jobs?page=0&size=10&sort=createdDate,desc"
```

**Response:**
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
      "status": "OPEN",
      "createdDate": "2025-12-28T10:30:00Z"
    }
  ]
}
```

#### Get Single Job
**Endpoint:** `GET /api/jobs/{id}`

```bash
curl -X GET http://localhost:8080/api/jobs/1
```

---

### **PROTECTED ENDPOINTS** (JWT authentication required)

#### Create Job Posting
**Endpoint:** `POST /api/jobs`  
**Required Role:** `RECRUITER` or `ADMIN`

```bash
curl -X POST http://localhost:8080/api/jobs \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "Senior Java Developer",
    "description": "5+ years experience with Spring Boot",
    "company": "TechCorp",
    "location": "Remote",
    "salary": "120000-150000",
    "employmentType": "FULL_TIME"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "Job created successfully",
  "data": {
    "id": 1,
    "title": "Senior Java Developer",
    "createdDate": "2025-12-28T10:30:00Z"
  }
}
```

---

#### Apply for Job
**Endpoint:** `POST /api/applications`  
**Required Role:** `JOB_SEEKER` or `RECRUITER`

```bash
curl -X POST http://localhost:8080/api/applications \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "jobId": 1,
    "coverLetter": "I am very interested in this position. I have 6 years of experience with Spring Boot..."
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "Application submitted successfully",
  "data": {
    "id": 5,
    "jobId": 1,
    "userId": 2,
    "status": "PENDING",
    "appliedDate": "2025-12-28T11:15:00Z"
  }
}
```

---

#### Get User's Applications
**Endpoint:** `GET /api/applications`  
**Required Role:** Authenticated user

```bash
curl -X GET http://localhost:8080/api/applications \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 5,
      "jobId": 1,
      "jobTitle": "Senior Java Developer",
      "status": "PENDING",
      "appliedDate": "2025-12-28T11:15:00Z"
    }
  ]
}
```

---

#### Update Application Status
**Endpoint:** `PATCH /api/applications/{id}/status`  
**Required Role:** `RECRUITER` (must own the job) or `ADMIN`

```bash
curl -X PATCH http://localhost:8080/api/applications/5/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "status": "ACCEPTED"
  }'
```

**Valid Statuses:** `PENDING`, `ACCEPTED`, `REJECTED`, `UNDER_REVIEW`

---

## Testing Tools

### **Option 1: Swagger UI (Easiest & Interactive)**

**URL:** `http://localhost:8080/swagger-ui/index.html`

**How to use:**
1. Open the URL in your browser
2. Click the "Authorize" button (top right)
3. Paste your JWT token: `Bearer YOUR_TOKEN_HERE`
4. Click "Authorize"
5. Try any endpoint directly from the UI
6. View responses, errors, and documentation

**Advantages:**
- ✅ Interactive testing
- ✅ Built-in documentation
- ✅ No tools required
- ✅ See request/response examples

---

### **Option 2: Postman (Professional Testing)**

**Steps:**
1. Download [Postman](https://www.postman.com/downloads/)
2. Import API spec:
   - Click **Import** → **Link**
   - URL: `http://localhost:8080/v3/api-docs`
3. Create a new request:
   - Method: `POST`
   - URL: `http://localhost:8080/api/auth/login`
   - Body (raw JSON):
     ```json
     {
       "username": "john_doe",
       "password": "Password123!"
     }
     ```
4. Set token as environment variable:
   - Click **Environments** → Create new
   - Add variable: `token` = paste JWT response
5. Use in Authorization header:
   - Type: `Bearer Token`
   - Value: `{{token}}`

**Advantages:**
- ✅ Professional testing
- ✅ Save requests/collections
- ✅ Environment variables
- ✅ Automated testing & CI/CD

---

### **Option 3: cURL (Command Line)**

**Set token as variable:**
```powershell
$token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Use in requests:**
```bash
curl -X GET http://localhost:8080/api/jobs \
  -H "Authorization: Bearer $token"
```

---

### **Option 4: JavaScript/Frontend Integration**

```javascript
// 1. Register
async function register() {
  const response = await fetch('http://localhost:8080/api/auth/register', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      username: 'user123',
      email: 'user@example.com',
      password: 'SecurePass123!'
    })
  });
  const data = await response.json();
  console.log(data);
}

// 2. Login & Store Token
async function login() {
  const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      username: 'user123',
      password: 'SecurePass123!'
    })
  });
  const { data } = await response.json();
  localStorage.setItem('token', data.token);
  return data.token;
}

// 3. Get All Jobs
async function getJobs() {
  const response = await fetch('http://localhost:8080/api/jobs');
  const data = await response.json();
  return data.data;
}

// 4. Apply for Job (Protected)
async function applyForJob(jobId, coverLetter) {
  const token = localStorage.getItem('token');
  const response = await fetch('http://localhost:8080/api/applications', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({
      jobId: jobId,
      coverLetter: coverLetter
    })
  });
  return await response.json();
}

// 5. Get User's Applications (Protected)
async function getMyApplications() {
  const token = localStorage.getItem('token');
  const response = await fetch('http://localhost:8080/api/applications', {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  return await response.json();
}
```

---

## Code Examples

### **Python Example**
```python
import requests
import json

BASE_URL = 'http://localhost:8080'

# 1. Login
login_data = {
    'username': 'john_doe',
    'password': 'Password123!'
}
response = requests.post(f'{BASE_URL}/api/auth/login', json=login_data)
token = response.json()['data']['token']

# 2. Get Jobs
headers = {'Authorization': f'Bearer {token}'}
jobs = requests.get(f'{BASE_URL}/api/jobs', headers=headers).json()
print(jobs)

# 3. Apply for Job
application_data = {
    'jobId': 1,
    'coverLetter': 'I am interested...'
}
response = requests.post(f'{BASE_URL}/api/applications', 
                        json=application_data, 
                        headers=headers)
print(response.json())
```

---

## Common Scenarios

### **Scenario 1: Job Seeker Workflow**

```bash
# 1. Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","email":"alice@example.com","password":"Pass123!"}'

# 2. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"Pass123!"}'
# Save token from response

# 3. Browse Jobs
curl -X GET http://localhost:8080/api/jobs

# 4. Apply for Job
curl -X POST http://localhost:8080/api/applications \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"jobId":1,"coverLetter":"I am interested..."}'

# 5. Check Application Status
curl -X GET http://localhost:8080/api/applications \
  -H "Authorization: Bearer $TOKEN"
```

---

### **Scenario 2: Recruiter Workflow**

```bash
# 1. Register as Recruiter
# Note: Default role is JOB_SEEKER. Contact admin to change to RECRUITER

# 2. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"recruiter1","password":"Pass123!"}'
# Save token

# 3. Post a Job
curl -X POST http://localhost:8080/api/jobs \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title":"Backend Developer",
    "description":"Java Spring Boot experience required",
    "company":"TechCorp",
    "location":"New York, NY",
    "salary":"100000-130000",
    "employmentType":"FULL_TIME"
  }'

# 4. View Applications on Your Jobs
curl -X GET http://localhost:8080/api/applications \
  -H "Authorization: Bearer $TOKEN"

# 5. Update Application Status
curl -X PATCH http://localhost:8080/api/applications/2/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"status":"ACCEPTED"}'
```

---

## Error Handling

### **Common Error Responses**

#### 401 Unauthorized (Invalid/Missing Token)
```json
{
  "timestamp": "2025-12-28T12:00:00.000Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource"
}
```
**Solution:** Add valid JWT token to `Authorization: Bearer` header

---

#### 403 Forbidden (Insufficient Permissions)
```json
{
  "timestamp": "2025-12-28T12:00:00.000Z",
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied: You don't have permission to perform this action"
}
```
**Solution:** Ensure user has required role (RECRUITER/ADMIN)

---

#### 400 Bad Request (Invalid Input)
```json
{
  "success": false,
  "message": "Validation failed",
  "errors": {
    "email": "Email should be valid"
  }
}
```
**Solution:** Check request body format and required fields

---

#### 404 Not Found
```json
{
  "success": false,
  "message": "Resource not found: Job with id 999 not found"
}
```
**Solution:** Verify resource ID exists

---

#### 500 Internal Server Error
```json
{
  "timestamp": "2025-12-28T12:00:00.000Z",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Database connection failed"
}
```
**Solution:** Check server logs and ensure MySQL is running

---

## Rate Limiting & Security

### **Best Practices**

1. **Never expose tokens:**
   - ❌ Don't commit tokens to git
   - ✅ Store in environment variables
   - ✅ Store in secure storage (localStorage in browsers, secure HTTP cookies)

2. **HTTPS in Production:**
   - Configure SSL/TLS certificate
   - Use `https://` instead of `http://`

3. **Token Expiration:**
   - Tokens expire after a set time
   - Re-login to get a new token
   - Implement refresh token mechanism for frontend

4. **Validate Input:**
   - Check email format
   - Use strong passwords (8+ chars, mix of uppercase, numbers, special chars)
   - Sanitize user inputs

5. **CORS Configuration:**
   - Currently accepts all origins
   - In production, restrict to your frontend domain only

---

## API Reference Summary

| Endpoint | Method | Auth | Purpose |
|----------|--------|------|---------|
| `/api/auth/register` | POST | No | Create new account |
| `/api/auth/login` | POST | No | Get JWT token |
| `/api/jobs` | GET | No | List all jobs |
| `/api/jobs/{id}` | GET | No | Get single job |
| `/api/jobs` | POST | Yes (RECRUITER) | Create job posting |
| `/api/applications` | POST | Yes | Apply for job |
| `/api/applications` | GET | Yes | Get user's applications |
| `/api/applications/{id}/status` | PATCH | Yes (RECRUITER) | Update application status |

---

## Support

- **API Documentation:** `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI Spec:** `http://localhost:8080/v3/api-docs`
- **Issues:** Check [RUNNING_PROJECT_STEPS.md](RUNNING_PROJECT_STEPS.md) for troubleshooting

---

**Last Updated:** December 28, 2025  
**Version:** 1.0.0
