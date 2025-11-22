# Lithuanian Word of the Day 🇱🇹

A full-stack web application that generates and displays a new Lithuanian word every day with AI-generated contextual images.

## Features

- 📅 Daily Lithuanian word with definition and usage examples
- 🎨 AI-generated contextual images for each word
- 👤 User authentication and word claim tracking
- 🗄️ PostgreSQL database for persistent storage

## Tech Stack

**Backend:**

- Java 17+
- Spring Boot
- Maven
- PostgreSQL
- OpenAI API
- Cloudinary API

**Frontend:**

- React 18
- TypeScript
- Node.js

## Prerequisites

- Java 17 or higher
- Node.js 16+ and npm
- PostgreSQL database
- Maven (included with `mvnw`)

## Local Setup Guide

### 1. Database Setup

First, set up the PostgreSQL database:

```bash
# Create the database and tables
psql -U postgres -f database_setup.sql
```

### 2. Backend Setup

Navigate to the backend directory and configure environment variables:

```bash
cd backend
cp src/main/resources/application.properties.sample src/main/resources/application.properties
```

Edit `src/main/resources/application.properties` and add your credentials:

- Database connection details
- OpenAI API key
- Cloudinary API credentials

Build and run the backend:

```bash
# Using Maven wrapper
./mvnw clean install
./mvnw spring-boot:run

# Or using Maven directly (if installed globally)
mvn clean install
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### 3. Frontend Setup

Navigate to the frontend directory and install dependencies:

```bash
cd ../frontend
npm install
```

Start the development server:

```bash
npm start
```

The frontend will open automatically at `http://localhost:3000`

## Project Structure

```
dienos-zodis/
├── backend/              # Java Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/wordofday/
│   │   │   │   ├── controller/      # REST API endpoints
│   │   │   │   ├── service/         # Business logic
│   │   │   │   ├── repository/      # Data access
│   │   │   │   ├── model/           # Entity models
│   │   │   │   ├── scheduler/       # Scheduled tasks
│   │   │   │   └── config/          # Configuration classes
│   │   │   └── resources/           # Properties and configs
│   │   └── test/                    # Unit tests
│   └── pom.xml                      # Maven dependencies
│
├── frontend/             # React TypeScript application
│   ├── src/
│   │   ├── components/  # React components
│   │   ├── services/    # API client services
│   │   ├── types/       # TypeScript type definitions
│   │   └── App.tsx      # Main app component
│   └── package.json     # npm dependencies
│
├── database_setup.sql   # Database initialization script
└── README.md            # This file
```

## API Endpoints

- `GET /api/words` - Get today's word
- `GET /api/words/history` - Get word history
- `POST /api/words/generate` - Generate today's word

## Development Tips

- The backend runs on port **8080**
- The frontend runs on port **3000**
- Database runs on port **5432** (default PostgreSQL)
- Hot reload is enabled for both frontend and backend during development
