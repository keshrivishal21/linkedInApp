# LinkedIn Clone - Microservices Architecture

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=for-the-badge&logo=springboot" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk" alt="Java"/>
  <img src="https://img.shields.io/badge/Apache%20Kafka-4.1-black?style=for-the-badge&logo=apachekafka" alt="Kafka"/>
  <img src="https://img.shields.io/badge/PostgreSQL-17-blue?style=for-the-badge&logo=postgresql" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/Neo4j-Graph%20DB-lightblue?style=for-the-badge&logo=neo4j" alt="Neo4j"/>
  <img src="https://img.shields.io/badge/Docker-Containerized-2496ED?style=for-the-badge&logo=docker" alt="Docker"/>
  <img src="https://img.shields.io/badge/Kubernetes-Orchestrated-326CE5?style=for-the-badge&logo=kubernetes" alt="Kubernetes"/>
</p>

A production-ready **LinkedIn-like social networking platform** built using **Spring Boot Microservices Architecture**. This project demonstrates enterprise-level patterns including service discovery, API gateway, event-driven architecture, and cloud-native deployments.

---

## 📋 Table of Contents

- [Architecture Overview](#-architecture-overview)
- [Services](#-services)
- [Technology Stack](#-technology-stack)
- [Features](#-features)
- [Getting Started](#-getting-started)
- [API Documentation](#-api-documentation)
- [Kubernetes Deployment](#-kubernetes-deployment)
- [Configuration](#-configuration)
- [Project Structure](#-project-structure)
- [Contributing](#-contributing)

---

## 🏗️ Architecture Overview

```
                                    ┌─────────────────────────────────────────────────────────────┐
                                    │                        CLIENTS                               │
                                    │              (Web / Mobile / Third-party)                    │
                                    └─────────────────────────┬───────────────────────────────────┘
                                                              │
                                                              ▼
                                    ┌─────────────────────────────────────────────────────────────┐
                                    │                     API GATEWAY                              │
                                    │         (Spring Cloud Gateway + JWT Auth Filter)             │
                                    │                     Port: 8080                               │
                                    └─────────────────────────┬───────────────────────────────────┘
                                                              │
                                    ┌─────────────────────────┼───────────────────────────────────┐
                                    │                         │                                    │
                                    │              EUREKA DISCOVERY SERVICE                        │
                                    │                     Port: 8761                               │
                                    │                                                              │
                                    └─────────────────────────┬───────────────────────────────────┘
                                                              │
                    ┌─────────────────┬───────────────────────┼───────────────────┬────────────────┐
                    │                 │                       │                   │                │
                    ▼                 ▼                       ▼                   ▼                ▼
            ┌───────────────┐ ┌───────────────┐ ┌───────────────────┐ ┌───────────────┐ ┌──────────────┐
            │ USERS SERVICE │ │ POSTS SERVICE │ │CONNECTIONS SERVICE│ │ NOTIFICATION  │ │   UPLOADER   │
            │   Port: 9010  │ │   Port: 9020  │ │    Port: 9030     │ │    SERVICE    │ │   SERVICE    │
            │               │ │               │ │                   │ │  Port: 9030   │ │  Port: 9040  │
            │  PostgreSQL   │ │  PostgreSQL   │ │      Neo4j        │ │  PostgreSQL   │ │  Cloudinary  │
            └───────────────┘ └───────────────┘ └───────────────────┘ └───────────────┘ └──────────────┘
                    │                 │                       │                   │
                    │                 │                       │                   │
                    │                 └───────────────────────┼───────────────────┘
                    │                                         │
                    │                 ┌───────────────────────┘
                    │                 │
                    │                 ▼
                    │       ┌───────────────────────────────┐
                    └──────►│       APACHE KAFKA            │
                            │   Event-Driven Messaging      │
                            │ Topics: post_created_topic,   │
                            │         post_liked_topic,     │
                            │         user_created_topic    │
                            └───────────────────────────────┘
```

---

## 🔧 Services

### 1. **Discovery Service** (Eureka Server)
- **Port:** 8761
- **Purpose:** Service registry for dynamic service discovery
- **Technology:** Spring Cloud Netflix Eureka Server

### 2. **API Gateway**
- **Port:** 8080
- **Purpose:** Single entry point for all client requests
- **Features:**
  - Request routing to downstream services
  - JWT-based authentication filter
  - Rate limiting and load balancing
  - Request/Response transformation
- **Technology:** Spring Cloud Gateway

### 3. **Users Service**
- **Port:** 9010
- **Context Path:** `/users`
- **Purpose:** User authentication and management
- **Features:**
  - User registration (signup)
  - User authentication (login)
  - JWT token generation
  - Password encryption with BCrypt
- **Database:** PostgreSQL
- **Endpoints:**
  - `POST /users/auth/signup` - Register new user
  - `POST /users/auth/login` - Authenticate user

### 4. **Posts Service**
- **Port:** 9020
- **Context Path:** `/posts`
- **Purpose:** Post creation and management
- **Features:**
  - Create posts with image upload
  - View posts
  - Like/Unlike posts
  - Fetch user's posts
  - Event publishing via Kafka
- **Database:** PostgreSQL
- **Endpoints:**
  - `POST /posts/core` - Create a new post (multipart)
  - `GET /posts/core/{postId}` - Get post by ID
  - `GET /posts/core/users/{userId}/allPosts` - Get all posts by user
  - `POST /posts/likes/{postId}` - Like a post
  - `DELETE /posts/likes/{postId}` - Unlike a post

### 5. **Connections Service**
- **Port:** 9030
- **Context Path:** `/connections`
- **Purpose:** Social networking and connections management
- **Features:**
  - Send connection requests
  - Accept/Reject connection requests
  - View first-degree connections
  - Graph-based relationship management
- **Database:** Neo4j (Graph Database)
- **Endpoints:**
  - `GET /connections/core/{userId}/first-degree` - Get first-degree connections
  - `POST /connections/core/request/{userId}` - Send connection request
  - `POST /connections/core/accept/{userId}` - Accept connection request
  - `POST /connections/core/reject/{userId}` - Reject connection request

### 6. **Notification Service**
- **Port:** 9030
- **Context Path:** `/notifications`
- **Purpose:** Real-time notifications
- **Features:**
  - Kafka consumer for events
  - Notification persistence
  - Event-driven notification generation
- **Database:** PostgreSQL
- **Kafka Topics:**
  - `post_created_topic` - New post notifications
  - `post_liked_topic` - Post like notifications

### 7. **Uploader Service**
- **Port:** 9040
- **Context Path:** `/uploads`
- **Purpose:** File upload management
- **Features:**
  - Image/file upload to cloud storage
  - Support for Cloudinary
  - Support for Google Cloud Storage
- **Endpoints:**
  - `POST /uploads/file` - Upload file

---

## 💻 Technology Stack

### Backend Framework
| Technology | Version | Purpose |
|------------|---------|---------|
| Spring Boot | 3.4.x / 3.5.x | Application framework |
| Spring Cloud | 2023.0.x - 2025.x | Microservices ecosystem |
| Spring Data JPA | 3.x | Database ORM |
| Spring Data Neo4j | 3.x | Graph database support |
| Spring Cloud Gateway | 4.x | API Gateway |
| Spring Cloud Netflix Eureka | 4.x | Service Discovery |
| Spring Kafka | 3.x | Event messaging |
| Spring Cloud OpenFeign | 4.x | Declarative REST clients |

### Databases
| Database | Purpose |
|----------|---------|
| PostgreSQL 17 | Relational data (Users, Posts, Notifications) |
| Neo4j | Graph data (Social connections) |

### Messaging
| Technology | Purpose |
|------------|---------|
| Apache Kafka 4.1 | Event-driven architecture |
| Kafbat UI | Kafka monitoring dashboard |

### Security
| Technology | Purpose |
|------------|---------|
| JWT (jjwt) | Token-based authentication |
| BCrypt | Password hashing |

### Cloud & DevOps
| Technology | Purpose |
|------------|---------|
| Docker | Containerization |
| Kubernetes | Container orchestration |
| Google Cloud Storage | File storage (optional) |
| Cloudinary | Image upload/management |

### Build Tools
| Tool | Purpose |
|------|---------|
| Maven | Build automation |
| Jib | Docker image building |

---

## ✨ Features

### Core Features
- ✅ User Registration & Authentication
- ✅ JWT-based Security
- ✅ Create, View, and Manage Posts
- ✅ Image Upload Support
- ✅ Like/Unlike Posts
- ✅ Social Connections (Send, Accept, Reject Requests)
- ✅ First-degree Connection Viewing
- ✅ Real-time Notifications

### Architecture Features
- ✅ Microservices Architecture
- ✅ Service Discovery (Eureka)
- ✅ API Gateway Pattern
- ✅ Event-Driven Architecture (Kafka)
- ✅ Inter-service Communication (Feign)
- ✅ Centralized Authentication
- ✅ Database per Service Pattern
- ✅ Kubernetes Ready Deployment

---

## 🚀 Getting Started

### Prerequisites

- **Java 21** or higher
- **Maven 3.8+**
- **Docker** & **Docker Compose**
- **PostgreSQL 17**
- **Neo4j** (for connections service)

### 1. Clone the Repository

```bash
git clone https://github.com/keshrivishal21/linkedInApp.git
cd linkedInApp
```

### 2. Start Infrastructure Services

```bash
# Start Kafka and Kafka UI
docker-compose up -d
```

### 3. Start Databases

Start PostgreSQL instances for each service and Neo4j for connections service.

```bash
# Example: Start PostgreSQL with Docker
docker run -d --name users-db -e POSTGRES_DB=userDB -e POSTGRES_USER=user -e POSTGRES_PASSWORD=password -p 5432:5432 postgres:17

docker run -d --name posts-db -e POSTGRES_DB=postsDB -e POSTGRES_USER=user -e POSTGRES_PASSWORD=password -p 5433:5432 postgres:17

docker run -d --name notifications-db -e POSTGRES_DB=notificationsDB -e POSTGRES_USER=user -e POSTGRES_PASSWORD=password -p 5434:5432 postgres:17

# Start Neo4j
docker run -d --name neo4j -e NEO4J_AUTH=neo4j/password -p 7474:7474 -p 7687:7687 neo4j:latest
```

### 4. Configure Application Properties

Each service requires its own `application.properties` or `application.yml`. Configure:
- Database connections
- Kafka bootstrap servers
- Eureka server URL
- JWT secret key
- Cloudinary/GCS credentials (for uploader service)

### 5. Start Services (in order)

```bash
# 1. Start Discovery Service first
cd discoveryService
./mvnw spring-boot:run

# 2. Start other services
cd ../usersService && ./mvnw spring-boot:run
cd ../postsService && ./mvnw spring-boot:run
cd ../connectionsService && ./mvnw spring-boot:run
cd ../notification-service && ./mvnw spring-boot:run
cd ../uploaderService && ./mvnw spring-boot:run

# 3. Start API Gateway last
cd ../APIGateway && ./mvnw spring-boot:run
```

### 6. Access the Application

| Service | URL |
|---------|-----|
| API Gateway | http://localhost:8080 |
| Eureka Dashboard | http://localhost:8761 |
| Kafka UI | http://localhost:8080 (when using docker-compose) |

---

## 📚 API Documentation

### Authentication

All endpoints (except `/users/auth/**`) require JWT authentication.

**Header Format:**
```
Authorization: Bearer <jwt_token>
```

### Sample API Requests

#### 1. User Signup
```bash
POST http://localhost:8080/api/v1/users/auth/signup
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "securePassword123"
}
```

#### 2. User Login
```bash
POST http://localhost:8080/api/v1/users/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "securePassword123"
}
```

#### 3. Create Post
```bash
POST http://localhost:8080/api/v1/posts/core
Content-Type: multipart/form-data
Authorization: Bearer <token>

post: {"content": "My first post!"}
file: <image_file>
```

#### 4. Send Connection Request
```bash
POST http://localhost:8080/api/v1/connections/core/request/{userId}
Authorization: Bearer <token>
```

---

## ☸️ Kubernetes Deployment

The project includes Kubernetes manifests for production deployment.

### Manifests Location
```
k8s/
├── api-gateway.yml
├── user-service.yml
├── posts-service.yml
├── connections-service.yml
├── notification-service.yml
├── uploader-service.yml
├── user-db.yml
├── posts-db.yml
├── connections-db.yml
├── notification-db.yml
├── kafka.yml
└── ingress.yml
```

### Deploy to Kubernetes

```bash
# Apply all manifests
kubectl apply -f k8s/

# Check deployments
kubectl get deployments

# Check services
kubectl get services

# Check pods
kubectl get pods
```

### Resource Limits

Each service is configured with appropriate resource limits:
- **Memory:** 100Mi - 500Mi
- **CPU:** 100m - 200m

---

## ⚙️ Configuration

### Environment Variables

| Variable | Service | Description |
|----------|---------|-------------|
| `SPRING_PROFILES_ACTIVE` | All | Active profile (k8s, dev, prod) |
| `DB_SERVER` | All | Database server host |
| `DB_NAME` | All | Database name |
| `DB_USERNAME` | All | Database username |
| `DB_PASSWORD` | All | Database password |
| `JWT_SECRET_KEY` | Users, Gateway | JWT signing key |
| `KAFKA_BOOTSTRAP_SERVERS` | Posts, Notifications | Kafka broker address |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | All | Eureka server URL |
| `CLOUDINARY_URL` | Uploader | Cloudinary configuration |

### Kafka Topics

| Topic | Producer | Consumer | Purpose |
|-------|----------|----------|---------|
| `post_created_topic` | Posts Service | Notification Service | New post events |
| `post_liked_topic` | Posts Service | Notification Service | Post like events |
| `user_created_topic` | Users Service | Connections Service | New user events |

---

## 📁 Project Structure

```
linkedInProject/
├── APIGateway/                    # API Gateway Service
│   └── src/main/java/.../
│       ├── filter/                # Gateway filters (Auth)
│       ├── JWTService.java        # JWT validation
│       └── ApiGatewayApplication.java
│
├── discoveryService/              # Eureka Server
│   └── src/main/java/.../
│       └── DiscoveryServiceApplication.java
│
├── usersService/                  # Users Microservice
│   └── src/main/java/.../
│       ├── config/                # Configuration classes
│       ├── controller/            # REST controllers
│       ├── dto/                   # Data Transfer Objects
│       ├── entity/                # JPA entities
│       ├── event/                 # Kafka events
│       ├── exception/             # Custom exceptions
│       ├── repository/            # Data repositories
│       ├── service/               # Business logic
│       └── utils/                 # Utility classes
│
├── postsService/                  # Posts Microservice
│   └── src/main/java/.../
│       ├── auth/                  # Auth context
│       ├── client/                # Feign clients
│       ├── config/                # Configuration
│       ├── controller/            # REST controllers
│       ├── dto/                   # DTOs
│       ├── entity/                # JPA entities
│       ├── event/                 # Kafka events
│       ├── exception/             # Exceptions
│       ├── repository/            # Repositories
│       └── service/               # Business logic
│
├── connectionsService/            # Connections Microservice
│   └── src/main/java/.../
│       ├── auth/                  # Auth context
│       ├── consumer/              # Kafka consumers
│       ├── controller/            # REST controllers
│       ├── entity/                # Neo4j entities
│       ├── exception/             # Exceptions
│       ├── repository/            # Neo4j repositories
│       └── service/               # Business logic
│
├── notification-service/          # Notification Microservice
│   └── src/main/java/.../
│       ├── consumer/              # Kafka consumers
│       ├── entity/                # JPA entities
│       ├── exception/             # Exceptions
│       ├── repository/            # Repositories
│       └── service/               # Business logic
│
├── uploaderService/               # File Upload Microservice
│   └── src/main/java/.../
│       ├── config/                # Cloud storage config
│       ├── controller/            # REST controllers
│       └── service/               # Upload services
│           ├── CloudinaryUploaderService.java
│           └── GoogleCloudStorageUploaderService.java
│
├── k8s/                           # Kubernetes manifests
│   ├── api-gateway.yml
│   ├── user-service.yml
│   ├── posts-service.yml
│   ├── connections-service.yml
│   ├── notification-service.yml
│   ├── uploader-service.yml
│   ├── *-db.yml                   # Database deployments
│   ├── kafka.yml
│   └── ingress.yml
│
├── docker-compose.yml             # Docker Compose for local dev
└── README.md                      # This file
```

---

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is for educational purposes and demonstrates microservices architecture patterns.

---

## 👨‍💻 Author

**Vishal Keshri**

- GitHub: [@keshrivishal21](https://github.com/keshrivishal21)

---

## 🙏 Acknowledgments

- Spring Boot Team for the excellent framework
- Netflix OSS for Eureka
- Apache Kafka Team
- The open-source community

---

<p align="center">
  <b>⭐ If you found this project helpful, please give it a star!</b>
</p>
