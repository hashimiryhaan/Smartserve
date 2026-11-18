# SmartServe — Food Surplus Management Platform

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![MySQL / TiDB](https://img.shields.io/badge/Database-MySQL_%7C_TiDB_Cloud-4479A1?style=flat-square&logo=mysql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED?style=flat-square&logo=docker&logoColor=white)
![Render](https://img.shields.io/badge/Hosted_on-Render-46E3B7?style=flat-square&logo=render&logoColor=black)

**SmartServe** is a full-stack community food rescue platform designed to reduce food waste and fight hunger by connecting surplus food donors (restaurants, caterers, event organizers, and individuals) with price sensitive customers , and community receivers in real time in reasonable price.

### Live Deployment
* **Live Application:** [https://smartserve-api-lolg.onrender.com/index.html](https://smartserve-api-lolg.onrender.com/index.html)
* **Note for Reviewers:** The backend runs on Render's free tier and automatically spins down after 15 minutes of inactivity. Please allow **45–50 seconds** on the first request for the Docker container to wake up.

---

## Core Features

* **Role-Based User & Verification System:** Supports **Donors**, **Receivers**, and **Administrators**, featuring document/license upload and admin approval workflows (`verification.html` / `admin.html`) to verify legitimate organizations.
* **Real-Time Surplus Food Marketplace:** Donors publish food listings with category tags, portion quantities, expiry windows, pickup locations, and photos (`donor.html`), while receivers browse and filter available donations (`marketplace.html`, `item-details.html`).
* **OTP-Verified Pickup Handoffs:** Integrated **Spring Mail (`JavaMailSender`)** workflow generates and dispatches One-Time Passwords (OTPs) via SMTP to verify physical pickup handoffs between donors and receivers.
* **End-to-End Claim Lifecycle & History:** Tracks listing and claim states (`AVAILABLE`, `CLAIMED`, `COMPLETED`) with persistent audit logs for both donors and receivers (`history.html`).
* **Cloud-Native Production Setup:** Containerized using a multi-stage `Dockerfile` and connected over SSL to a serverless **TiDB Cloud** MySQL-compatible cluster with environment-externalized secrets.

---

## Tech Stack

| Layer | Technologies |
| :--- | :--- |
| **Backend** | Java, Spring Boot, Spring Web (REST API), Spring Data JPA / Hibernate, Spring Mail |
| **Database** | MySQL 8.0 (Local Development), TiDB Cloud Serverless MySQL (Production) |
| **Frontend** | HTML5, CSS3, Vanilla JavaScript (ES6+ Fetch API) |
| **DevOps & Infrastructure** | Docker, Maven Wrapper (`mvnw`), Render, Git & GitHub |

---

## Project Architecture

```text
SmartServe/
└── SmartServe/
    └── api/
        ├── Dockerfile                      # Multi-stage Maven build + JDK runtime image
        ├── pom.xml                         # Spring Boot dependencies (Web, JPA, MySQL, Mail)
        └── src/main/
            ├── java/com/smartserve/api/
            │   ├── SmartServeApplication.java
            │   ├── category/               # Food categories (Entity, Repo, Service, Controller)
            │   ├── claims/                 # Claim lifecycle, OTP generation & EmailService
            │   ├── config/                 # Global CorsConfig & static upload resource mapping
            │   ├── food/                   # FoodListing CRUD, filtering & image upload handling
            │   └── users/                  # Authentication, roles & license verification
            └── resources/
                ├── application.properties  # Production config mapped to environment variables
                └── static/                 # Frontend pages (index, marketplace, donor, history, etc.)
```

---

## REST API Overview

| Domain | Base Endpoint | Key Operations |
| :--- | :--- | :--- |
| **Users & Auth** | `/api/users` | User registration (`/signup`), login authentication, profile & license verification |
| **Food Listings** | `/api/food` | Create food donation with image upload, list active food items, update status |
| **Categories** | `/api/categories` | Fetch and manage food classification categories |
| **Claims & OTP** | `/api/claims` | Claim food listing, trigger email OTP notification, verify pickup completion, view history |

---

## Local Development Setup

### Prerequisites
* **JDK 17+** (tested on JDK 23)
* **MySQL 8.0+** running on `localhost:3306`

### Step-by-Step Instructions

1. **Clone the repository and navigate to the backend module:**
   ```bash
   git clone https://github.com/hashimiryhaan/Smartserve.git
   cd Smartserve/SmartServe/api
   ```

2. **Create the local MySQL database:**
   ```sql
   CREATE DATABASE smartserve;
   ```

3. **Configure a Git-ignored local profile:**
   Create `src/main/resources/application-local.properties` (already excluded in `.gitignore`) with your local credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/smartserve
   spring.datasource.username=root
   spring.datasource.password=YOUR_LOCAL_MYSQL_PASSWORD
   server.port=8080
   ```

4. **Run the Spring Boot server with the `local` profile:**
   * **Windows (PowerShell):**
     ```powershell
     .\mvnw spring-boot:run "-Dspring-boot.run.profiles=local"
     ```
   * **macOS / Linux:**
     ```bash
     ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
     ```

5. **Open the application:**
   Visit **`http://localhost:8080/index.html`** in your browser.

---

## Production Environment Variables

When deploying via Docker on Render or any cloud container host, configure the following environment variables:

| Variable | Required | Description |
| :--- | :---: | :--- |
| `DB_URL` | Yes | JDBC connection string (e.g., `jdbc:mysql://<host>:4000/smartserve?useSSL=true&requireSSL=true`) |
| `DB_USERNAME` | Yes | Cloud database username |
| `DB_PASSWORD` | Yes | Cloud database password |
| `MAIL_USERNAME` | Optional | Gmail/SMTP address used by `JavaMailSender` for OTP emails |
| `MAIL_PASSWORD` | Optional | 16-character SMTP App Password |
| `PORT` | Optional | Server port injected automatically by Render (defaults to `8080`) |