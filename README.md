# HourGlass — Campus Time-Credit Exchange Platform

> A campus peer-to-peer skill exchange platform where students exchange knowledge, skills, and assistance using time credits.

[![Java](https://img.shields.io/badge/Java-8-orange?logo=openjdk)](https://www.oracle.com/java/)
[![JSP](https://img.shields.io/badge/JSP-Servlets-blue)](https://jakarta.ee/)
[![JDBC](https://img.shields.io/badge/JDBC-MySQL-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-Build-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Apache Tomcat](https://img.shields.io/badge/Apache%20Tomcat-9-F8DC75?logo=apachetomcat&logoColor=black)](https://tomcat.apache.org/)

---

## 📌 Overview

**HourGlass** is a web-based campus skill exchange platform designed to help students share their knowledge, skills, and services without relying on traditional monetary payments.

The platform uses **time credits** as its internal exchange mechanism:

> **1 hour of help = 1 Time Credit**

Students can offer services, discover services offered by other students, request help, manage service requests, complete services, and verify completed services through a temporary QR-based verification process.

The project is developed as a collaborative academic project using Java web technologies, JSP, Servlets, JDBC, MySQL, HTML, CSS, and JavaScript.

---

## 🎯 Problem Statement

Students often have useful academic and practical skills that can help their peers, but there is no simple campus-focused system for exchanging those skills.

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
- Secure password hashing using PBKDF2
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

HourGlass uses a temporary QR verification mechanism to verify completed services.

The workflow is:

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

### 💳 Time-Credit Transactions

Credit transfers are handled using database transactions.

The transfer process validates:

1. Service request status
2. Provider authorization
3. QR token validity
4. QR token expiry
5. QR token reuse
6. Requester's available balance

After successful validation:

- Credits are deducted from the requester
- Credits are added to the provider
- The transaction is recorded
- The QR token is marked as used
- The request is marked as verified

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
                    │        Servlets       │
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
| --- | --- |
| **JSP / HTML** | User interface |
| **CSS** | Styling and presentation |
| **JavaScript** | Client-side interactions |
| **Servlets** | Request handling and application workflow |
| **DAO** | Database operations using JDBC |
| **Model** | Application data objects |
| **Utility** | Database connection and password security |
| **MySQL** | Persistent data storage |

---

## 🗄️ Database

The project uses **MySQL** with the database:

```text
hourglass_db
```

### Main Tables

| Table | Purpose |
| --- | --- |
| `users` | User accounts and time-credit balances |
| `services` | Services offered by students |
| `service_requests` | Service request lifecycle |
| `time_transactions` | Time-credit transaction records |
| `qr_tokens` | Temporary QR verification tokens |

A database export is included in:

```text
hourglass_db.sql
```

---

## 🔒 Security

The application includes multiple backend security measures:

- PBKDF2 password hashing with salt
- Session-based authentication
- Authorization checks
- Self-service prevention
- Duplicate request prevention
- Credit balance validation
- Database row locking during credit transfers
- Atomic database transactions
- Transaction rollback on failure
- Temporary QR tokens
- SHA-256 hashing of QR tokens before storage
- QR token expiration
- Single-use QR verification
- Protection against duplicate credit transfers

Database credentials are loaded through environment variables instead of being stored directly in the source code.

> **Never commit real database passwords or other secrets to the repository.**

---

## 🧰 Technology Stack

### Frontend
- HTML5
- CSS3
- JavaScript

### Backend
- Java 8
- JSP
- Java Servlets
- JDBC

### Database
- MySQL 8

### Build & Server
- Apache Maven
- Apache Tomcat 9

### Development Environment
- Visual Studio Code

---

## 📁 Project Structure

```text
hourglass/
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

## 🚀 Getting Started

### Prerequisites

Make sure the following are installed:

- Java JDK 8
- Apache Maven
- Apache Tomcat 9
- MySQL 8.x
- Git

Check Java:
```bash
java -version
```

Check Maven:
```bash
mvn -version
```

---

### 1. Clone the Repository

```bash
git clone [https://github.com/shreyasinghxvii-creator/hourglass-campus-time-credit.git](https://github.com/shreyasinghxvii-creator/hourglass-campus-time-credit.git)
cd hourglass-campus-time-credit
```

---

### 2. Create the Database

Open MySQL and create the database:

```sql
CREATE DATABASE hourglass_db;
```

Import the provided database dump:

```bash
mysql -u root -p hourglass_db < hourglass_db.sql
```

The SQL file can also be imported using MySQL Workbench.

---

### 3. Configure Database Credentials

HourGlass reads database credentials from environment variables.

Set the following variables:

```text
HOURGLASS_DB_USER
HOURGLASS_DB_PASSWORD
```

Example for Windows PowerShell:

```powershell
[Environment]::SetEnvironmentVariable("HOURGLASS_DB_USER", "root", "User")
[Environment]::SetEnvironmentVariable("HOURGLASS_DB_PASSWORD", "your_mysql_password", "User")
```

Restart the terminal after setting the environment variables if required.

> **Do not commit actual database passwords to GitHub.**

---

### 4. Build the Project

From the project directory:

```bash
mvn clean package
```

The generated WAR file will be:

```text
target/hourglass.war
```

---

### 5. Deploy to Apache Tomcat

Copy `target/hourglass.war` to the Tomcat `webapps/` directory.

Start Apache Tomcat and open:

```text
http://localhost:8080/hourglass/
```

---

## 🔄 Application Workflow

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
Requester Generates QR
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

## 📊 Service Categories

The current platform supports services across categories such as:

- Programming
- Design
- Academics
- Career
- Creative
- Tutoring
- Other

Services can be offered as online or offline services.

---

## 🧪 Validation & Testing

The application has been validated through:

- Maven compilation and WAR packaging
- Database connectivity
- Service request lifecycle
- Provider authorization
- QR token generation
- QR token expiry validation
- Invalid QR token rejection
- QR token reuse prevention
- Credit balance validation
- Atomic credit transfer
- Transaction recording

---

## 🛣️ Future Enhancements

Potential future versions may explore:

- Student notifications
- In-app messaging
- Service reviews and ratings
- Improved service scheduling
- Campus-based service discovery
- Enhanced user profiles
- Additional analytics
- Mobile application support

These are **future enhancements** and are not claimed as part of the current stable implementation.

---

## 👥 Team

HourGlass was developed as a collaborative academic project.

### Contributors

- **Shreya Singh** — UI / Frontend and project development
- **Bhumika Sharma** — Development
- **Shreyash** — Development

---

## 📌 Project Status

- **Version:** `1.0.0`
- **Status:** Working Academic Project

The current version represents the stable implementation developed for academic submission.

---

## 📄 License

This project was created as an academic software project. If you plan to reuse, modify, or distribute the project, please contact the project contributors.

---

## 👩‍💻 Author

### Shreya Singh
**B.Sc. Information Technology**

GitHub: [https://github.com/shreyasinghxvii-creator](https://github.com/shreyasinghxvii-creator)

---

<p align="center">
  <strong>HourGlass</strong><br>
  Share Skills. Exchange Time. Learn Together.
</p>