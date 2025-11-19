# 💇‍♀️ Hair Salon Management API (Backend)

> **A robust backend system for managing a hair salon, handling appointments, staff, client data, and security.**
> This application serves as the core API for a full-stack salon management platform.

## 📝 Table of Contents

* [Project Overview](#project-overview)
* [Architecture and Features](#architecture-and-features)
* [System Diagrams](#system-diagrams)
* [Technologies (Full-Stack)](#technologies-full-stack)
* [Frontend Features](#frontend-features)
* [Security (JWT)](#security-jwt)
* [How to Run Locally](#how-to-run-locally)
* [Testing Status](#testing-status)
* [API Reference](#api-reference)
* [Configuration and Secrets](#configuration-and-secrets)

---

##  Project Overview

This project is a high-quality **RESTful API** designed to manage all operational aspects of a modern hair salon. It covers complex business logic, including **appointment scheduling**, **staff (hairdresser) management**, user authentication via **JWT**, and providing detailed **statistics** on service usage and appointment status.

The application is built with a focus on clean architecture, security, and testability.

##  Architecture and Features

The application structure follows a clean, **layered architecture** (Controller -> Facade/Service -> Repository) and includes several key features:

* **Appointment Management:** Full CRUD operations, filtering by user/status/date, and complex scheduling logic to find **available time slots** and **available hairdressers**.
* **User/Role Management:** Different user roles (Clients, Hairdressers, Admins) with dedicated endpoints for retrieving user lists and profile details.
* **Authentication & Security:** Robust security layer using **Spring Security** and **JWT (JSON Web Tokens)** for authentication, token expiration, and refresh token mechanism.
* **Business Statistics:** Dedicated endpoints for generating statistics on appointment status counts and the popularity of specific hair offers.
* **Offer Management:** CRUD operations for defining available hair services (`HairOfferController`).
* **Database Management:** Schema versioning and migration handled by **Liquibase**.

---

##  System Diagrams

The following diagrams illustrate the design, structure, and relationships within the system.

* **Architectural Overview:** The architecture follows a **Monolithic** (or **Layered**) design using the MVC pattern. 
* **Database Schema (ERD):** Shows the relationships between key entities like Appointments, Users, Hairdressers, and Hair Offers.
    
* **UML Class Diagram:** Details the structure of core classes and their methods, demonstrating the clean segregation into Facades, Services, and Repositories. 
* **UML Use Case Diagram:** Illustrates the functional scope of the system from the perspective of different actors (Client, Hairdresser, Admin).

<img width="1166" height="686" alt="Image" src="https://github.com/user-attachments/assets/0637f33a-54cd-4b99-abd3-e0e1b20220d0" />

<img width="677" height="715" alt="Image" src="https://github.com/user-attachments/assets/d235de2d-f931-402f-8872-87dfe67172ae" />

<img width="609" height="256" alt="Image" src="https://github.com/user-attachments/assets/5ff537b9-56a1-4fc6-9f8a-2cfb98b248ae" />

<img width="644" height="807" alt="Image" src="https://github.com/user-attachments/assets/9824380d-7da8-476d-a11b-2e3fd717c148" />

---

##  Technologies (Full-Stack)

The project utilizes a powerful **Full-Stack** approach:

### Backend Technologies (Java/Spring)

| Technology | Version | Usage and Importance |
| :--- | :--- | :--- |
| **Spring Boot** | 3.4.4 | Application setup and dependency management. |
| **Programming Language** | **Java** | 21 | Long-Term Support (LTS) version. |
| **Persistence** | **Spring Data JPA** & **Hibernate** | - | ORM and database interaction. |
| **Security** | **Spring Security**, **JWT** | 0.12.6 | Authentication, authorization, and token management. |
| **Database** | **PostgreSQL**, **Liquibase** | - | Production-ready DB and schema version control. |
| **API Docs** | **SpringDoc OpenAPI (Swagger)** | 2.8.8 | Automatic generation of API documentation. |

### Frontend Technologies (Angular)

The User Interface (UI) is built using a rich set of modern tools:

| Technology | Version | Usage and Importance |
| :--- | :--- | :--- |
| **Framework** | **Angular** | 19.2.0 | Modern SPA framework. |
| **UI Components** | **Angular Material** | 19.2.10 | Professional and accessible UI components. |
| **Internationalization** | **ngx-translate** | 16.0.4 | Support for **multiple languages** (i18n). |
| **Data Visualization** | **Chart.js**, **ng2-charts** | v4+ | Displaying **statistics and reports** (e.g., appointment status counts). |
| **Date/Time Handling** | **Luxon** | 3.6.1 | Advanced handling and formatting of dates and time slots. |
| **JWT Decoding** | **jwt-decode** | 4.0.0 | Client-side decoding of JWT for accessing user data. |

---

##  Frontend Features

The Angular frontend offers a professional and highly functional user experience:

* **Internationalization (i18n):** Full support for switching between multiple languages using `ngx-translate`.
* **Dynamic Reporting:** Real-time generation of **charts and graphs** (using Chart.js) for business statistics, such as completed services and current appointment status breakdown.
* **Advanced Scheduling UI:** Intuitive interface for selecting available time slots based on complex backend calculations (Luxon for precise time handling).
* **Role-Based Views:** Presentation of different dashboards and menus depending on the user's role (Client, Hairdresser, Admin).

---

##  Security (JWT)

The API utilizes a modern, stateless security approach based on **JSON Web Tokens (JWT)**:

* Users log in via the `/login` endpoint, receiving an access token and a refresh token.
* The **Access Token** is used to authorize subsequent requests to secured endpoints (e.g., `/api/appointments`).
* The **Refresh Token** allows users to obtain a new access token without re-authenticating, managed via the `/refresh-token` endpoint.

---

##  How to Run Locally

### 1. Prerequisites

* **JDK 21+**
* **Maven**
* **Node.js & npm**
* **PostgreSQL** instance running locally (or via Docker Compose).

### 2. Setup

1.  **Clone the repository:**
    ```bash
    git clone [(https://github.com/R3venge1337/hairdresser-app.git)]
    cd [Your Project Directory Name]
    ```
2.  **Configure Database:**
    * Ensure your local PostgreSQL instance is running.
    * Set up a database matching the configuration in `secret.properties`.

3.  **Set Secret Properties:**
    * Create or update the `secret.properties` file with your configuration (see [Configuration and Secrets](#configuration-and-secrets) below).

### 3. Build and Run

1.  **Build and Run Backend (Java):**
    ```bash
    ./mvnw clean install
    java -jar target/backend-0.0.1-SNAPSHOT.jar
    ```
2.  **Run Frontend (Angular):**
    ```bash
    cd [Your Frontend Directory Name]
    npm install
    npm start
    ```

### Access Points

* **Backend API:** `http://localhost:8080` (or configured port)
* **Frontend UI:** `http://localhost:4200` (default for Angular)
* **API Documentation (Swagger):** `http://localhost:8080/swagger-ui/index.html`

---

##  Testing Status

The project utilizes **JUnit 5** and **Spring Security Test** for verifying application logic and security rules.

* **Tested Layers:** Tests cover core business logic within the service/facade layer and integration tests to verify security constraints on controllers.
* **Running Tests:**
    ```bash
    ./mvnw test
    ```

---

##  API Reference

The application is logically divided into several functional domains:

### Authentication and User Management (`AuthorizationController`, `UserController`)

| Method | Endpoint (Path) | Description |
| :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Authenticates a user and returns **JWT tokens**. |
| `POST` | `/api/auth/registration` | Registers a new user (client). |
| `POST` | `/api/auth/logout` | Invalidates the current user session. |
| `POST` | `/api/auth/refresh-token` | Obtains a new Access Token using the Refresh Token. |
| `GET` | `/api/my-profile` | Retrieves detailed information about the authenticated user's profile. |
| `GET` | `/api/all-profiles` | Retrieves all user profiles (Requires Admin/Staff role). |

### Appointment Scheduling and Management (`AppointmentController`)

| Method | Endpoint (Path) | Description |
| :--- | :--- | :--- |
| `POST` | `/api/appointments` | **Creates (Makes) a new appointment.** |
| `POST` | `/api/appointments/all` | Retrieves a **paged, filtered** list of all appointments. |
| `GET` | `/api/appointments/slots` | Calculates and returns **available time slots** for a given date and duration. |
| `POST` | `/api/available-hairdressers` | Returns a list of **available hairdressers** for a specific time slot. |
| `GET` | `/api/appointments/{uuid}` | Retrieves a single appointment by UUID. |
| `PATCH` | `/api/appointments/{uuid}/change-status` | Changes the appointment status (e.g., Booked, Completed, Cancelled). |
| `PATCH` | `/api/appointments/{uuid}/reschedule` | Updates the date/time of an existing appointment. |
| `POST` | `/api/my-appointments` | Retrieves a **paged, filtered** list of appointments for the currently authenticated client. |

### Service and Staff Management (`HairOfferController`, `HairdresserController`)

| Method | Endpoint (Path) | Description |
| :--- | :--- | :--- |
| `POST` | `/api/hair-offers/filter` | Retrieves a **paged, filtered** list of hair services/offers. |
| `POST` | `/api/hair-offers` | **Creates** a new hair service offer. |
| `PUT` | `/api/hair-offers/{id}` | **Updates** an existing hair service offer. |
| `POST` | `/api/hairdressers` | Registers a new hairdresser (staff member). |
| `GET` | `/api/users?roleName={role}` | Retrieves all users matching a specific role (e.g., 'HAIRDRESSER'). |

### Statistics and Reporting (`AppointmentController`)

| Method | Endpoint (Path) | Description |
| :--- | :--- | :--- |
| `GET` | `/api/appointments/statistics-by-status` | Retrieves counts of appointments grouped by their current status. |
| `GET` | `/statistics/statistics-by-hairoffer-name` | Retrieves counts of **completed** appointments grouped by the type of hair offer. |

---

##  Configuration and Secrets

The following properties should be placed in the `secret.properties` file for secure configuration. **These values must not be committed to the repository.**

```properties
jwt.secret.key=[Your 512-bit secret key]
jwt.expirationTime=86400
jwt.refreshExpirationTime=604800
DB_PASS=postgres
```

## Application View
<img width="1918" height="922" alt="Image" src="https://github.com/user-attachments/assets/92066edf-021d-4042-a02d-0032845fdc40" />

<img width="1919" height="922" alt="Image" src="https://github.com/user-attachments/assets/485ccffc-d3b3-4bf9-bd59-841d60ad8550" />

<img width="1919" height="921" alt="Image" src="https://github.com/user-attachments/assets/7ff07808-9dff-4e7d-9466-f218f686b30e" />

<img width="1919" height="917" alt="Image" src="https://github.com/user-attachments/assets/1165dfb4-ec51-4def-a7e3-88ab7bcedbb6" />

<img width="1919" height="923" alt="Image" src="https://github.com/user-attachments/assets/97a7e343-9ef7-4df1-9a70-c014f4bf62a7" />

<img width="1919" height="922" alt="Image" src="https://github.com/user-attachments/assets/622d2d62-e45d-4a2d-93ad-5ef6c8829d1b" />

<img width="1919" height="919" alt="Image" src="https://github.com/user-attachments/assets/082f81ab-563b-4fbb-a5b4-867085c17545" />

<img width="1919" height="922" alt="Image" src="https://github.com/user-attachments/assets/1ba4093a-52e7-4ab1-b4da-9443ee9e3743" />

<img width="1919" height="925" alt="Image" src="https://github.com/user-attachments/assets/b00fc63e-6d5b-4a92-b6fc-71e73386b6f2" />

<img width="1919" height="921" alt="Image" src="https://github.com/user-attachments/assets/b9e99ce3-f28c-4020-92ca-1fbd71da3608" />

<img width="1919" height="916" alt="Image" src="https://github.com/user-attachments/assets/8ad58168-bd01-4f3f-9c22-40d204c4c51e" />

<img width="1919" height="922" alt="Image" src="https://github.com/user-attachments/assets/1c5a04af-9b2d-4286-a43d-ba04b0a289eb" />

<img width="1919" height="918" alt="Image" src="https://github.com/user-attachments/assets/5d420d4d-f2d5-4b35-b520-16f1fcb5cd43" />

<img width="1919" height="920" alt="Image" src="https://github.com/user-attachments/assets/97558726-9aa0-431e-bfdd-6fc47d7a3604" />
