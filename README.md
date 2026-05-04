# API Billing System

A comprehensive Java-based solution for tracking API usage, managing subscription plans, and automating billing processes. This project is built with a decoupled architecture, separating core business logic from the user interface.

## 🏗 Project Architecture

The project is structured as a **Maven Multi-Module Project** to ensure a clean separation of concerns:

*   **`api-usage-core` (API usage and billing)**: The engine of the system. Contains JPA entities, business rules for billing, usage recording logic, and database persistence.
*   **`web-ui` (Vaadin Web Layer)**: The administrative dashboard. Built with Vaadin 24, it provides a modern web interface to manage clients, plans, and invoices.

---

## 🚀 Features

### Core Logic
- **API Tracking**: Record usage summaries and individual usage records.
- **Subscription Management**: Define flexible plans with multiple pricing tiers.
- **Automated Billing**: Generate detailed invoices and invoice lines based on usage.
- **Payment Processing**: Support for multiple payment methods (Card, Bank Transfer, Wallet).
- **Rate Limiting**: Define and enforce rate limit rules for API keys.

### Administrative UI
- **Client Dashboard**: View and manage client accounts and their API keys.
- **Plan Manager**: Create and modify subscription tiers and pricing.
- **Invoice Explorer**: Search and manage generated invoices and their statuses.

---

## 🛠 Tech Stack

- **Language**: Java 21
- **Web Framework**: Vaadin 24
- **Persistence**: JPA (EclipseLink)
- **Database**: PostgreSQL
- **Build Tool**: Maven
- **Server**: Jetty (embedded for development)

---

## 📋 Prerequisites

- **Java 21** or higher
- **Maven 3.9+**
- **PostgreSQL** running locally

---

## ⚙️ Getting Started

### 1. Database Setup
Ensure you have a PostgreSQL database running. By default, the system looks for:
- **URL**: `jdbc:postgresql://localhost:5432/postgres`
- **User**: `postgres`
- **Password**: `postgres`

*Note: You can update these settings in `API usage and billing/src/main/resources/META-INF/persistence.xml`.*

### 2. Build the Project
Run the following command from the root directory to compile both modules:
```bash
mvn clean install
```

### 3. Run the Web Application
Navigate to the web layer and start the Jetty server:
```bash
cd "Vaadin Web Layer"
mvn jetty:run
```
Once started, the dashboard will be available at: `http://localhost:8080`

---

## 📂 Directory Structure

```text
API Billing System/
├── pom.xml                        # Parent POM (Dependency Management)
├── API usage and billing/         # Core Domain & Logic (Module)
│   └── src/main/java/org/example/ # Entities, Services, Logic
└── Vaadin Web Layer/              # Web Interface (Module)
    └── src/main/java/org/example/ # Vaadin Views & UI Components
```
