# Java Developer Test Project

This project is designed to test **Java developers'** skills in building a backend service that powers a React + Node.js application.

## 📚 Documentation

- **[Getting Started](./GETTING_STARTED.md)** – Java test setup guide
- **[Test Requirements](./TEST_REQUIREMENTS.md)** – Complete Java test requirements and evaluation criteria
- **[Test Summary](./TEST_SUMMARY.md)** – Quick overview of required tasks
- **[Candidate Checklist](./CANDIDATE_CHECKLIST.md)** – Track your progress

## Project Structure

```
java-test/
├── java-backend/     # Java Spring Boot HTTP server (data source)
├── node-backend/     # Node.js Express API server (calls Java backend)
└── react-frontend/   # React frontend (calls Node.js backend)
```

## Architecture Flow

```
React Frontend (port 5173)
    ↓
Node.js Backend (port 3000)
    ↓
Java Backend (port 8080)
```

## Quick Start

**Important:** Start services in this order (inside `java-test/`):

### 1. Start Java Backend (Data Source)

```bash
cd java-backend
mvn spring-boot:run
```

The Java backend will run on `http://localhost:8080` and serves as the data source.

### 2. Start Node.js Backend (API Gateway)

In a new terminal:

```bash
cd node-backend
npm install
npm start
```

The Node.js backend will run on `http://localhost:3000` and proxies requests to the Java backend.

### 3. Start React Frontend

In a new terminal:

```bash
cd react-frontend
npm install
npm run dev
```

The React frontend will run on `http://localhost:5173` and calls the Node.js backend.

## Project Overview

### Java Backend (Data Source)
A Spring Boot HTTP server that:
- Serves as the primary data source
- Stores users and tasks in-memory
- Exposes REST endpoints (`/api/users`, `/api/tasks`, `/api/stats`)
- Handles JSON requests/responses
- Implements thread-safe data access

### Node.js Backend (API Gateway)
A RESTful API server built with Express that:
- Acts as a proxy/gateway between React and Java
- Calls the Java backend for all data operations
- Provides the same API interface to the frontend
- Handles error propagation and status codes

### React Frontend
A modern React application that:
- Consumes the Node.js backend API
- Displays users and tasks
- Provides filtering and search capabilities
- Shows real-time statistics
- Features a modern, responsive UI

## Test Requirements

**📋 See [TEST_REQUIREMENTS.md](./TEST_REQUIREMENTS.md) for detailed Java test requirements and evaluation criteria.**

The test includes:
- **Phase 1**: Setup and understanding (30 min)
- **Phase 2**: Core requirements – Add POST/PUT endpoints, logging in Java backend (2–3 hours)
- **Phase 3**: Advanced features – Persistence, caching, middleware (2–3 hours)
- **Phase 4**: Code quality – Testing, documentation, best practices
- **Phase 5**: Bonus tasks – Authentication, rate limiting, metrics

## Requirements

- Java 11+ and Maven
- Node.js 16+ and npm
