# TalentFlow - Running Project Steps

Complete guide to run, access, and stop the TalentFlow Spring Boot application.

---

## Prerequisites

Ensure you have the following installed:
- **Java 20+** (JDK-20 or newer)
- **Maven 3.9.12+** (Apache Maven)
- **MySQL 5.5.62+** (with root user, no password)
- **Git** (optional, for version control)

---

## Step 1: Database Setup

### Check MySQL is Running

Open MySQL Command Line Client and verify the connection:

```bash
mysql -u root -h localhost
```

You should see the MySQL prompt (`mysql>`). Type `exit;` to quit.

### Create Database and Schema

The database is automatically created by Hibernate, but you can manually run the schema:

```bash
$schemaFile = "C:\Users\Randhir\OneDrive\Desktop\SPRINGBOOTPROJECTS\TalentFlow – Job Application & Hiring Management System\database\schema.sql"
Get-Content $schemaFile | & "C:\Program Files\MySQL\MySQL Server 5.5\bin\mysql.exe" -u root -h localhost
```

Or manually in MySQL:
```sql
CREATE DATABASE IF NOT EXISTS talentflow_db;
USE talentflow_db;
-- Schema will be auto-created by Hibernate on first run
```

---

## Step 2: Install Maven (if not already installed)

If Maven is not installed, download and install it:

```powershell
# Check if Maven is installed
C:\Users\Randhir\.maven\maven-3.9.12\bin\mvn.cmd --version
```

If not found, download from: https://maven.apache.org/download.cgi

---

## Step 3: Build the Project

Navigate to the project directory and build:

```powershell
# Change to project directory
$path = Get-ChildItem "C:\Users\Randhir\OneDrive\Desktop\SPRINGBOOTPROJECTS" -Directory | Where-Object { Test-Path (Join-Path $_.FullName 'pom.xml') } | Select-Object -First 1 -ExpandProperty FullName
Set-Location $path

# Build the project (skip tests for faster build)
C:\Users\Randhir\.maven\maven-3.9.12\bin\mvn.cmd package -DskipTests
```

Expected output:
```
[INFO] BUILD SUCCESS
[INFO] Total time:  XX.XXX s
```

The JAR file is created at: `target\talentflow-api-1.0.0.jar`

---

## Step 4: Run the Application

### Option A: Run in Background (Recommended)

Start the application in a hidden background PowerShell window:

```powershell
# Navigate to project directory first
$path = Get-ChildItem "C:\Users\Randhir\OneDrive\Desktop\SPRINGBOOTPROJECTS" -Directory | Where-Object { Test-Path (Join-Path $_.FullName 'pom.xml') } | Select-Object -First 1 -ExpandProperty FullName
Set-Location $path

# Start application in background
Start-Process -FilePath powershell -ArgumentList "-NoProfile","-Command","cd '$pwd'; java -jar 'target\talentflow-api-1.0.0.jar' 2>&1 | Tee-Object -FilePath app.log" -WindowStyle Hidden
```

Wait 15-20 seconds for the application to fully start.

### Option B: Run in Foreground (for Debugging)

```powershell
$path = Get-ChildItem "C:\Users\Randhir\OneDrive\Desktop\SPRINGBOOTPROJECTS" -Directory | Where-Object { Test-Path (Join-Path $_.FullName 'pom.xml') } | Select-Object -First 1 -ExpandProperty FullName
Set-Location $path
java -jar "target\talentflow-api-1.0.0.jar"
```

You will see logs in the console. Press `Ctrl+C` to stop.

---

## Step 5: Verify Application is Running

### Check Java Process

```powershell
tasklist /FI "IMAGENAME eq java.exe"
```

Expected output: Shows `java.exe` process

### Check Port 8080 is Listening

```powershell
netstat -ano | findstr ":8080"
```

Expected output:
```
TCP    0.0.0.0:8080           0.0.0.0:0              LISTENING       <PID>
TCP    [::]:8080              [::]:0                 LISTENING       <PID>
```

### Test Connection

```powershell
C:\Windows\System32\curl.exe -I http://localhost:8080/swagger-ui.html
```

Expected output:
```
HTTP/1.1 302
Location: /swagger-ui/index.html
```

---

## Step 6: Access the Application

Open your browser and visit these URLs:

### Main Links

| Link | Purpose | Status |
|------|---------|--------|
| http://localhost:8080/ | Base API URL | Protected (requires JWT) |
| http://localhost:8080/swagger-ui.html | Swagger UI (redirects to index.html) | Public ✅ |
| http://localhost:8080/swagger-ui/index.html | Swagger UI Documentation | Public ✅ |
| http://localhost:8080/api-docs | OpenAPI JSON specification | Public ✅ |
| http://localhost:8080/v3/api-docs | OpenAPI v3 specification | Public ✅ |

### If Using 127.0.0.1 Instead of localhost

```
http://127.0.0.1:8080/swagger-ui/index.html
```

### Browser Tips

- **Clear Cache**: If you see old pages, press `Ctrl+Shift+Delete` and clear browsing data
- **Incognito Mode**: Open a private/incognito window to avoid cached responses
- **Refresh**: Press `F5` or `Ctrl+R` to reload

---

## Step 7: Using Swagger UI

1. **Open** http://localhost:8080/swagger-ui/index.html
2. **Authorize** (if needed for protected endpoints):
   - Click the green "Authorize" button
   - Provide your JWT token from login
3. **Explore Endpoints**:
   - Browse through Applications, Jobs, Authentication sections
   - Click on any endpoint to expand and see details
4. **Test Endpoints**:
   - Click "Try it out" button on an endpoint
   - Enter required parameters
   - Click "Execute" to make the request
   - View response code and body

---

## Step 8: Important API Endpoints

### Public Endpoints (No JWT Required)

```
GET  /api/auth/login           - User login
POST /api/auth/register        - User registration
GET  /api/jobs                 - View all jobs
GET  /api/jobs/{id}            - View job details
```

### Protected Endpoints (Require JWT Token)

```
GET  /api/applications/my      - Get my applications (Candidate)
GET  /api/applications/job/{jobId}  - Get job applications (Recruiter)
POST /api/applications/apply/{jobId} - Apply for a job (Candidate)
PUT  /api/applications/{id}/status   - Update application status (Recruiter)
```

---

## Step 9: Viewing Application Logs

### View Recent Logs (Last 200 Lines)

```powershell
Get-Content app.log -Tail 200
```

### View All Logs

```powershell
Get-Content app.log
```

### Tail Logs in Real-Time (If Running in Foreground)

Logs appear in the console window where you started the application.

---

## Step 10: Stop the Application

### Method 1: Stop by Process Name (All Java)

```powershell
Stop-Process -Name java -Force
```

**Warning**: This stops ALL Java processes on your system.

### Method 2: Stop by Process ID (Recommended)

```powershell
# Find the PID
netstat -ano | findstr ":8080"

# Stop by PID (replace <PID> with actual number)
Stop-Process -Id <PID> -Force
```

Example:
```powershell
# If PID is 6704
Stop-Process -Id 6704 -Force
```

### Method 3: From the Application Window

If running in foreground (Step 4 Option B), press `Ctrl+C` in the terminal.

### Verify Application Stopped

```powershell
netstat -ano | findstr ":8080"
```

Should return no results if successfully stopped.

---

## Troubleshooting

### Issue: "Failed to connect to localhost:8080"

**Solution**: 
- Verify Java is running: `tasklist /FI "IMAGENAME eq java.exe"`
- Verify port 8080 is listening: `netstat -ano | findstr ":8080"`
- Check app.log for startup errors: `Get-Content app.log -Tail 50`
- Wait 20 seconds for startup to complete before testing

### Issue: "HTTP 500 - Internal Server Error"

**Solution**:
- Check database connection: `mysql -u root -h localhost`
- Verify database `talentflow_db` exists: `mysql -u root -h localhost -e "SHOW DATABASES;"`
- Check app.log for detailed error messages

### Issue: "Swagger UI shows 403 Forbidden"

**Solution**:
- This has been fixed in the current build
- Clear browser cache: `Ctrl+Shift+Delete`
- Try incognito/private window
- If persists, rebuild: `mvn.cmd package -DskipTests`

### Issue: MySQL Connection Refused

**Solution**:
- Verify MySQL is running: Open MySQL Command Line Client
- Check credentials: User should be `root` with no password
- Verify host: Should be `localhost` or `127.0.0.1`
- Check port: MySQL default is 3306

---

## Quick Reference Commands

### Build Project
```powershell
C:\Users\Randhir\.maven\maven-3.9.12\bin\mvn.cmd package -DskipTests
```

### Run Application (Background)
```powershell
$path = Get-ChildItem "C:\Users\Randhir\OneDrive\Desktop\SPRINGBOOTPROJECTS" -Directory | Where-Object { Test-Path (Join-Path $_.FullName 'pom.xml') } | Select-Object -First 1 -ExpandProperty FullName; Set-Location $path; Start-Process -FilePath powershell -ArgumentList "-NoProfile","-Command","cd '$pwd'; java -jar 'target\talentflow-api-1.0.0.jar' 2>&1 | Tee-Object -FilePath app.log" -WindowStyle Hidden
```

### Check Server Status
```powershell
netstat -ano | findstr ":8080"
```

### View Logs
```powershell
Get-Content app.log -Tail 200
```

### Stop Server
```powershell
Stop-Process -Name java -Force
```

### Test API
```powershell
C:\Windows\System32\curl.exe -I http://localhost:8080/swagger-ui.html
```

---

## Configuration Files

- **Application Config**: `src/main/resources/application.yml`
- **Database Credentials**:
  - Username: `root`
  - Password: (empty/none)
  - Host: `localhost`
  - Port: `3306`
  - Database: `talentflow_db`

---

## Support

For issues or questions:
1. Check app.log for error messages
2. Verify MySQL is running and accessible
3. Ensure all dependencies are installed (Maven, Java, MySQL)
4. Check firewall is not blocking port 8080

---

**Last Updated**: 28 December 2025
**Version**: TalentFlow API 1.0.0
**Status**: ✅ Running and Operational
