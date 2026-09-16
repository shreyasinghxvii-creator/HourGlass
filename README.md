# HourGlass — Campus Time-Credit Exchange Platform

> A campus peer-to-peer skill exchange platform where students exchange knowledge, skills, and assistance using time credits.

[![Java](https://img.shields.io/badge/Java-8-orange?logo=openjdk)](https://www.oracle.com/java/)
[![JSP](https://img.shields.io/badge/JSP-Servlets-blue)](https://tomcat.apache.org/)
[![JDBC](https://img.shields.io/badge/JDBC-MySQL-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-Build-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Apache Tomcat](https://img.shields.io/badge/Apache%20Tomcat-9-F8DC75?logo=apachetomcat&logoColor=black)](https://tomcat.apache.org/)

---

## 📌 Overview

**HourGlass** is a web-based campus skill exchange platform designed to help students share knowledge, skills, and assistance without relying on traditional monetary payments.

The platform uses **time credits** as its internal exchange mechanism:

> **1 hour of help = 1 Time Credit**

Students can offer services, discover available services, request assistance, manage service requests, complete services, and verify completed services through a temporary QR-based verification process.

---

## 🎯 Problem Statement

Students often have useful academic and practical skills that can help their peers, but there may not be a simple campus-focused system for exchanging those skills.

HourGlass provides a structured platform where students can:

- Offer skills or academic assistance
- Discover services offered by other students
- Request available services
- Accept or reject service requests
- Complete accepted services
- Verify completed services
- Exchange time credits securely
- View transaction history

---

## ✨ Key Features

### 👤 User Management

- Student registration and login
- Session-based authentication
- Password hashing using PBKDF2
- Time-credit balance management
- Logout functionality

### 🛠️ Service Management

- Add services
- Edit services
- Activate or deactivate services
- Categorize services
- Set time-credit cost
- Support online and offline services

### 📋 Service Requests

- Browse available services
- Request a service
- View personal service requests
- View incoming requests
- Accept or reject requests
- Complete accepted requests
- Prevent duplicate active requests
- Prevent users from requesting their own services

### 🔐 QR-Based Verification

HourGlass uses a temporary QR verification mechanism to verify completed services before transferring time credits.

The verification flow is:

```text
Service Requested
       ↓
    PENDING
       ↓
Provider Accepts
       ↓
   ACCEPTED
       ↓
Service Completed
       ↓
   COMPLETED
       ↓
Requester Generates Temporary QR
       ↓
Provider Scans QR
       ↓
Backend Validates Token
       ↓
    VERIFIED
       ↓
Time Credits Transferred
       ↓
Transaction Recorded
```

QR tokens are:

- Randomly generated
- Hashed before database storage
- Time-limited
- Single-use
- Invalidated after successful verification

Sensitive information is not stored directly inside the QR token.

---

## 💳 Time-Credit Transactions

Credit transfers are handled using database transactions.

Before transferring credits, the system validates:

- Service request status
- Provider authorization
- QR token validity
- QR token expiry
- QR token reuse
- Requester's available balance

After successful validation:

1. Credits are deducted from the requester.
2. Credits are added to the provider.
3. The transaction is recorded.
4. The QR token is marked as used.
5. The request is marked as verified.

If an error occurs during the transfer, the database transaction is rolled back.

---

## 🏗️ System Architecture

HourGlass follows a layered Java web application architecture.

```text
                    ┌───────────────────────┐
                    │      Web Browser      │
                    │   JSP / HTML / CSS    │
                    │      JavaScript       │
                    └───────────┬───────────┘
                                │
                                ▼
                    ┌───────────────────────┐
                    │       Servlets        │
                    │ Request Handling &    │
                    │ Application Workflow  │
                    └───────────┬───────────┘
                                │
                                ▼
                    ┌───────────────────────┐
                    │          DAO          │
                    │ JDBC Database Access  │
                    │ Transactions & Logic  │
                    └───────────┬───────────┘
                                │
                                ▼
                    ┌───────────────────────┐
                    │         MySQL         │
                    │      hourglass_db     │
                    └───────────────────────┘
```

### Application Layers

| Layer | Responsibility |
|---|---|
| JSP / HTML | User interface |
| CSS | Styling and presentation |
| JavaScript | Client-side interactions |
| Servlets | Request handling and application workflow |
| DAO | Database operations using JDBC |
| Model | Application data objects |
| Utility | Database connection and security utilities |
| MySQL | Persistent data storage |

---

## 🗄️ Database

The project uses MySQL with the database:

```text
hourglass_db
```

### Main Tables

| Table | Purpose |
|---|---|
| `users` | User accounts and time-credit balances |
| `services` | Services offered by students |
| `service_requests` | Service request lifecycle |
| `time_transactions` | Time-credit transaction records |
| `qr_tokens` | Temporary QR verification tokens |

A database export is included in the repository:

```text
hourglass_db.sql
```

---

## 🧰 Technology Stack

### Frontend

- HTML5
- CSS3
- JavaScript

### Backend

- Java 8
- Java Servlets
- JSP
- JDBC

### Database

- MySQL 8.x

### Build & Server

- Apache Maven
- Apache Tomcat 9

### Development Environment

- Visual Studio Code

---

## 📁 Project Structure

```text
HourGlass/
│
├── .gitignore
├── README.md
├── pom.xml
├── hourglass_db.sql
│
└── src/
    └── main/
        ├── java/
        │   └── com/hourglass/
        │       ├── controller/
        │       ├── dao/
        │       ├── model/
        │       └── util/
        │
        └── webapp/
            ├── WEB-INF/
            ├── css/
            ├── js/
            ├── *.jsp
            ├── *.css
            └── *.js
```

---

# 🚀 Getting Started

Follow the steps below to set up and run HourGlass on a local Windows development environment.

## 1. Prerequisites

Install the following software:

- Java JDK 8
- Apache Maven
- Apache Tomcat 9
- MySQL 8.x
- Git
- Visual Studio Code

The project is configured for **Java 8** and **Apache Tomcat 9**.

---

## 2. Verify Java Installation

Open PowerShell or Command Prompt and run:

```powershell
java -version
```

The output should indicate Java 8.

Example:

```text
java version "1.8.x"
```

Also verify the Java compiler:

```powershell
javac -version
```

---

## 3. Verify Maven Installation

Run:

```powershell
mvn -version
```

Maven should display its version and the Java version being used.

---

## 4. Verify MySQL Installation

Make sure the MySQL server is installed and running.

The default MySQL port used by the application is:

```text
3306
```

The database connection is configured for:

```text
localhost:3306
```

---

## 5. Clone the Repository

Open PowerShell and navigate to the location where you want to store the project.

Run:

```powershell
git clone https://github.com/shreyasinghxvii-creator/HourGlass.git
```

Move into the repository:

```powershell
cd HourGlass
```

---

## 6. Create the MySQL Database

Open MySQL Workbench or the MySQL command line.

Create the database:

```sql
CREATE DATABASE hourglass_db;
```

Select the database:

```sql
USE hourglass_db;
```

---

## 7. Import the Database

The repository contains:

```text
hourglass_db.sql
```

### Option A — MySQL Command Line

From the project directory, run:

```powershell
mysql -u root -p hourglass_db < hourglass_db.sql
```

Enter the MySQL password when prompted.

### Option B — MySQL Workbench

1. Open MySQL Workbench.
2. Connect to the MySQL server.
3. Open `hourglass_db.sql`.
4. Select the `hourglass_db` database.
5. Execute the SQL script.
6. Refresh the database schemas.
7. Verify that the required tables have been created.

The main tables are:

```text
users
services
service_requests
time_transactions
qr_tokens
```

---

## 8. Configure Database Credentials

Database credentials are **not stored directly in the Java source code**.

HourGlass reads the database username and password from environment variables:

```text
HOURGLASS_DB_USER
HOURGLASS_DB_PASSWORD
```

### Windows PowerShell

Set the MySQL username:

```powershell
[Environment]::SetEnvironmentVariable("HOURGLASS_DB_USER", "root", "User")
```

Set the MySQL password:

```powershell
[Environment]::SetEnvironmentVariable("HOURGLASS_DB_PASSWORD", "your_mysql_password", "User")
```

Replace:

```text
your_mysql_password
```

with the MySQL password configured on the local computer.

### Important

Do **not** replace the password with an actual password inside the README.

The real password should remain only in the local environment variable.

After setting environment variables, restart PowerShell if the current terminal does not recognize the new values.

---

## 9. Configure JAVA_HOME

Make sure `JAVA_HOME` points to a Java 8 JDK installation.

Check the current value:

```powershell
$env:JAVA_HOME
```

If it is not configured, set it according to the Java 8 installation path on the local computer.

Example:

```powershell
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Path\To\Your\Java\jdk8", "User")
```

Restart PowerShell if required.

Verify:

```powershell
$env:JAVA_HOME
```

Then check Java:

```powershell
java -version
```

---

## 10. Build the Project Using Maven

From the HourGlass project directory:

```powershell
mvn clean package
```

Maven will:

- Clean previous build files
- Compile the Java source code
- Process the web application
- Package the application as a WAR file

A successful build should end with:

```text
BUILD SUCCESS
```

The generated WAR file will be located at:

```text
target/hourglass.war
```

---

## 11. Configure Apache Tomcat

Install Apache Tomcat 9 and make sure it is configured to run with Java 8.

The Tomcat installation directory may look similar to:

```text
C:\apache-tomcat-9.x.x
```

or another location selected during installation.

---

## 12. Deploy the WAR File

After successfully running:

```powershell
mvn clean package
```

locate:

```text
target/hourglass.war
```

Copy the WAR file into the Tomcat:

```text
webapps
```

directory.

For example:

```text
C:\apache-tomcat-9.x.x\webapps\hourglass.war
```

Tomcat will deploy the application using the context path:

```text
/hourglass
```

---

## 13. Start Apache Tomcat

On Windows, open the Tomcat `bin` directory and run:

```text
startup.bat
```

Alternatively, Tomcat can be started using the Windows service if it has been configured as a service.

---

## 14. Open HourGlass

Once Tomcat is running, open a browser and visit:

```text
http://localhost:8080/hourglass/
```

The HourGlass application should load.

---

# 🔄 Application Workflow

The main application workflow is:

```text
Register / Login
       ↓
   Dashboard
       ↓
 Browse Services
       ↓
 Request Service
       ↓
Provider Accepts Request
       ↓
 Service Completed
       ↓
Requester Generates Temporary QR
       ↓
 Provider Scans QR
       ↓
 Backend Verification
       ↓
 Credits Transferred
       ↓
 Transaction History
```

---

# 🔐 QR Verification Workflow

The QR verification process works as follows:

```text
Completed Service
       ↓
Requester Generates QR
       ↓
Temporary Token Created
       ↓
Token Stored as SHA-256 Hash
       ↓
Provider Scans QR
       ↓
Backend Validates Token
       ↓
Checks Expiry
       ↓
Checks Single-Use Status
       ↓
Checks Request Status
       ↓
Checks Provider Authorization
       ↓
Checks Requester's Credit Balance
       ↓
Transfer Credits
       ↓
Mark QR as Used
       ↓
Mark Request as VERIFIED
       ↓
Record Transaction
```

The QR token is temporary and cannot be reused after successful verification.

---

# 📊 Service Categories

The platform supports services across categories such as:

- Programming
- Design
- Academics
- Career
- Creative
- Tutoring
- Other

Services can be offered as:

- Online services
- Offline services

---

# 💰 Time-Credit System

HourGlass uses time credits instead of traditional monetary payment.

The basic principle is:

> **1 hour of help = 1 Time Credit**

When a service is completed and successfully verified:

```text
Requester Balance
       ↓
Credit Deducted
       ↓
Provider Balance
       ↓
Credit Added
```

The transfer is performed inside a database transaction.

If the transaction fails, the database changes are rolled back.

---

# 🛡️ Security

The application includes backend protections for important operations.

### Authentication

- Session-based user authentication
- Logout functionality

### Password Security

Passwords are protected using **PBKDF2 hashing with salt** rather than being stored as plain text.

### Authorization

Backend authorization checks ensure that users can perform actions only on resources they are permitted to manage.

### Service Request Protection

The application prevents:

- Requesting one's own service
- Duplicate active requests
- Unauthorized request modifications
- Invalid request state transitions

### Credit Transfer Protection

The application validates:

- Request status
- Provider authorization
- QR token validity
- QR token expiry
- QR token usage
- Requester's available balance

Database transactions and row locking are used during the credit transfer process to help prevent inconsistent balance updates and duplicate transfers.

### Database Credentials

Database credentials are loaded through environment variables:

```text
HOURGLASS_DB_USER
HOURGLASS_DB_PASSWORD
```

Actual database passwords should never be committed to the repository.

---

# 🧪 Validation & Testing

The application has been validated through:

- Maven compilation and WAR packaging
- Database connectivity
- User authentication
- Service request lifecycle
- Provider authorization
- Request status transitions
- QR token generation
- QR token expiry validation
- Invalid QR token rejection
- QR token reuse prevention
- Credit balance validation
- Atomic credit transfer
- Transaction recording

---

# 🛣️ Future Enhancements

Potential future versions may explore:

- Student notifications
- In-app messaging
- Service reviews and ratings
- Improved service scheduling
- Campus-based service discovery
- Enhanced user profiles
- Additional analytics
- Mobile application support

These are future enhancements and are not part of the current stable implementation.

---

# 📌 Project Status

- **Project:** HourGlass
- **Version:** `1.0.0`
- **Status:** Working Academic Project

The current version represents the stable implementation prepared for academic submission.

---

# 👩‍💻 Author

### Shreya Singh

**B.Sc. Information Technology**

GitHub:

https://github.com/shreyasinghxvii-creator

---

# 📄 License

This project was created as an academic project.

No open-source license has been specified for this repository. All rights to the project remain with the author unless otherwise stated.
