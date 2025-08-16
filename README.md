# Planbook Backend - Microservices Architecture

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.1-brightgreen?style=for-the-badge&logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-Latest-red?style=for-the-badge&logo=redis&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-3.7.0-black?style=for-the-badge&logo=apache-kafka&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Latest-blue?style=for-the-badge&logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.6+-red?style=for-the-badge&logo=apache-maven&logoColor=white)

![Microservices](https://img.shields.io/badge/Architecture-Microservices-purple?style=for-the-badge)
![JWT](https://img.shields.io/badge/Auth-JWT-yellow?style=for-the-badge&logo=json-web-tokens&logoColor=white)
![Swagger](https://img.shields.io/badge/API%20Docs-Swagger-green?style=for-the-badge&logo=swagger&logoColor=white)
![Eureka](https://img.shields.io/badge/Discovery-Eureka-orange?style=for-the-badge)

## 📖 Tổng quan

Planbook Backend là một hệ thống microservices được xây dựng bằng Spring Boot, phục vụ cho nền tảng giáo dục trực tuyến. Hệ thống cung cấp các tính năng quản lý giáo án, tài liệu học tập, đề thi, và các công cụ hỗ trợ giảng dạy.

## 🏗️ Kiến trúc hệ thống

### Core Services

- **Discovery Server** (Port: 8761) - Service discovery sử dụng Eureka
- **API Gateway** (Port: 8080) - Gateway chính cho tất cả requests
- **Identity Service** (Port: 8081) - Xác thực và phân quyền người dùng

### Business Services

- **Master Data Service** - Quản lý dữ liệu cơ bản (môn học, sách, chương, bài học)
- **Academic Resource Service** - Quản lý tài liệu học tập
- **Lesson Plan Service** - Quản lý giáo án
- **Exam Service** - Quản lý đề thi và bài kiểm tra
- **Slide Template Service** - Quản lý mẫu slide bài giảng
- **Workspace Service** - Quản lý không gian làm việc và kết quả AI tools
- **Purchase Service** - Xử lý thanh toán và đơn hàng
- **External Tool Service** - Tích hợp các công cụ bên ngoài
- **External Caller Service** - Gọi API từ các partner
- **Tool Log Service** - Ghi log sử dụng công cụ
- **WebSocket Service** - Hỗ trợ real-time communication
- **PlanBook Aggregator** - Tổng hợp dữ liệu từ nhiều services

## 🛠️ Công nghệ sử dụng

### Backend Framework

- **Spring Boot 3.2.5** - Framework chính
- **Spring Cloud 2023.0.1** - Microservices infrastructure
- **Spring Security** - Bảo mật và xác thực
- **Spring Data JPA** - ORM và database access
- **Spring Cloud Gateway** - API Gateway
- **Netflix Eureka** - Service discovery

### Database & Storage

- **MySQL** - Database chính
- **Redis** - Caching và session storage
- **Supabase** - Cloud storage cho files

### Message Queue & Communication

- **Apache Kafka** - Message streaming
- **Zookeeper** - Kafka coordination
- **OpenFeign** - Service-to-service communication
- **WebSocket** - Real-time communication

### Documentation & Monitoring

- **OpenAPI 3.0 (Swagger)** - API documentation
- **Spring Boot Actuator** - Health monitoring
- **Kafdrop** - Kafka UI monitoring

### Development Tools

- **Maven** - Build tool
- **Docker** - Containerization
- **Lombok** - Code generation
- **MapStruct** - Object mapping

## 🚀 Cài đặt và chạy

### Yêu cầu hệ thống

- Java 17+
- Maven 3.6+
- Docker & Docker Compose
- MySQL 8.0+

### 1. Clone repository

```bash
git clone <repository-url>
cd Planbook-BE
```

### 2. Chạy infrastructure services (Development)

```bash
cd deploy
docker-compose -f docker-compose-local.yml up -d
```

Điều này sẽ khởi động:

- Kafka (Port: 9092)
- Kafdrop UI (Port: 9000)
- Debezium Connect (Port: 8083)

### 3. Khởi động services theo thứ tự

#### Bước 1: Discovery Server

```bash
cd discovery-server
./mvnw spring-boot:run
```

#### Bước 2: API Gateway

```bash
cd PlanBook-Gateway
./mvnw spring-boot:run
```

#### Bước 3: Identity Service (Auth)

```bash
cd identity-service
./mvnw spring-boot:run
```

#### Bước 4: Các services khác (có thể chạy song song)

```bash
# Master Data Service
cd master-data-service && ./mvnw spring-boot:run

# Academic Resource Service
cd academic-resource-service && ./mvnw spring-boot:run

# Lesson Plan Service
cd lesson-plan-service && ./mvnw spring-boot:run

# Exam Service
cd exam-service && ./mvnw spring-boot:run

# Workspace Service
cd workspace-service && ./mvnw spring-boot:run

# Các services khác...
```

### 4. Production Deployment

```bash
cd deploy
docker-compose up -d
```

## 📋 API Documentation

Sau khi khởi động các services, bạn có thể truy cập API documentation tại:

- **API Gateway Swagger**: http://localhost:8080/swagger-ui.html
- **Identity Service**: http://localhost:8081/swagger-ui.html
- **Discovery Server**: http://localhost:8761

## 🔧 Cấu hình

### Environment Variables

Tạo file `.env` trong thư mục `deploy/` với các biến sau:

```env
# Database
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/planbook
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password

# JWT Keys
SPRING_JWT_PRIVATE_KEY_PATH=/path/to/private.key
SPRING_JWT_PUBLIC_KEY_PATH=/path/to/public.key
SPRING_APP_SECRET_KEY=your_secret_key

# Supabase
SUPABASE_JWT_SECRET_KEY=your_supabase_secret

# Email
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email
MAIL_PASSWORD=your_password

# Kafka
KAFKA_TOPIC=planbook-events

# Docker
DOCKERHUB_USERNAME=your_dockerhub_username
```

## 🔍 Monitoring

### Health Checks

- **Discovery Server**: http://localhost:8761
- **Kafdrop (Kafka UI)**: http://localhost:9000
- **Service Health**: http://localhost:8080/actuator/health

### Logs

Logs được lưu trong thư mục `logs/` của mỗi service.

## 🧪 Testing

### Chạy tests cho tất cả services

```bash
./mvnw test
```

### Chạy test cho service cụ thể

```bash
cd <service-name>
./mvnw test
```

## 📁 Cấu trúc thư mục

```
Planbook-BE/
├── discovery-server/          # Service discovery
├── PlanBook-Gateway/          # API Gateway
├── identity-service/          # Authentication & Authorization
├── master-data-service/       # Master data management
├── academic-resource-service/ # Academic resources
├── lesson-plan-service/       # Lesson plans
├── exam-service/             # Exams and tests
├── workspace-service/        # Workspaces and AI tools
├── purchase-service/         # Payment processing
├── external-tool-service/    # External tools integration
├── external-caller-service/  # Partner API calls
├── tool-log-service/         # Tool usage logging
├── websocket-service/        # Real-time communication
├── slide-template-service/   # Slide templates
├── PlanBook-Aggregator/      # Data aggregation
├── deploy/                   # Docker compose files
└── logs/                     # Application logs
```

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🔧 Chi tiết Services

### Discovery Server

- **Mục đích**: Service registry và discovery
- **Port**: 8761
- **Technology**: Netflix Eureka Server
- **Endpoints**:
  - `/eureka` - Eureka dashboard

### API Gateway

- **Mục đích**: Single entry point cho tất cả API calls
- **Port**: 8080
- **Technology**: Spring Cloud Gateway
- **Features**:
  - JWT authentication
  - Rate limiting
  - CORS handling
  - Request routing
- **Public Endpoints**: Login, register, forgot password, public resources

### Identity Service

- **Mục đích**: Authentication và authorization
- **Port**: 8081
- **Features**:
  - JWT token generation/validation
  - Google OAuth integration
  - Password reset via email
  - Role-based access control
  - Redis session management
- **Main APIs**:
  - `POST /api/login` - User login
  - `POST /api/register` - User registration
  - `POST /api/login-google` - Google OAuth login
  - `POST /api/forgot-password` - Password reset request
  - `POST /api/refresh` - Token refresh

### Master Data Service

- **Mục đích**: Quản lý dữ liệu cơ bản của hệ thống giáo dục
- **Features**:
  - Quản lý môn học (Subjects)
  - Quản lý sách giáo khoa (Books)
  - Quản lý chương (Chapters)
  - Quản lý bài học (Lessons)
  - Quản lý khối lớp (Grades)
  - Quản lý loại sách (Book Types)
- **Main APIs**:
  - `/api/subjects` - CRUD operations for subjects
  - `/api/books` - CRUD operations for books
  - `/api/chapters` - CRUD operations for chapters
  - `/api/lessons` - CRUD operations for lessons
  - `/api/grades` - CRUD operations for grades

### Academic Resource Service

- **Mục đích**: Quản lý tài liệu học tập
- **Features**:
  - Upload/download tài liệu
  - Phân loại tài liệu theo môn học
  - Tìm kiếm và lọc tài liệu
  - Supabase storage integration
- **Main APIs**:
  - `/api/academic-resources` - CRUD operations for resources
  - `/api/academic-resources/upload` - File upload

### Exam Service

- **Mục đích**: Quản lý đề thi và bài kiểm tra
- **Features**:
  - Tạo đề thi từ template
  - Sinh đề thi ngẫu nhiên
  - Quản lý instance đề thi
  - Hệ thống mã truy cập cho học sinh
  - Tự động chấm điểm
  - Xuất kết quả
- **Main APIs**:
  - `/api/exam-templates` - Template management
  - `/api/exam-instances` - Instance management
  - `/api/exam-generator` - Random exam generation
  - `/api/exam-instances/code/{code}` - Student access (public)
  - `/api/exam-instances/code/{code}/submit` - Student submission (public)

### Workspace Service

- **Mục đích**: Quản lý không gian làm việc và kết quả AI tools
- **Features**:
  - Quản lý workspace của giáo viên
  - Lưu trữ kết quả từ AI tools
  - Phân loại theo type: LESSON_PLAN, SLIDE, EXAM, QUIZ, etc.
  - Soft delete và version control
- **Main APIs**:
  - `/api/tool-results` - CRUD operations for AI tool results
  - Support filtering by user, workspace, type, status
  - Pagination và sorting

### Purchase Service

- **Mục đích**: Xử lý thanh toán và đơn hàng
- **Features**:
  - PayOS integration
  - Webhook handling
  - Order management
  - Payment confirmation
- **Main APIs**:
  - `/api/orders` - Order management
  - `/api/payments` - Payment processing
  - `/api/orders/webhook/payOs` - PayOS webhook (public)

### External Tool Service

- **Mục đích**: Tích hợp các công cụ AI bên ngoài
- **Features**:
  - Quản lý external tools
  - Tool approval workflow
  - Status management (PENDING, APPROVED, ACTIVE, etc.)

### WebSocket Service

- **Mục đích**: Real-time communication
- **Features**:
  - Real-time notifications
  - Live collaboration
  - Chat functionality

### PlanBook Aggregator

- **Mục đích**: Tổng hợp dữ liệu từ nhiều services
- **Features**:
  - Feign client integration
  - Data aggregation from multiple sources
  - Unified API responses

## 🔐 Security

### JWT Authentication

- RSA key pair cho signing/verification
- Access token và refresh token
- Token expiration handling

### API Security

- Bearer token authentication
- Role-based access control
- Public endpoint configuration
- CORS policy

### Data Security

- Input validation
- SQL injection prevention
- XSS protection

## 📊 Database Schema

### Core Entities

- **Users**: User accounts và profiles
- **Subjects**: Môn học
- **Books**: Sách giáo khoa
- **Chapters**: Chương sách
- **Lessons**: Bài học
- **ExamTemplates**: Mẫu đề thi
- **ExamInstances**: Instance đề thi
- **AcademicResources**: Tài liệu học tập
- **ToolResults**: Kết quả AI tools
- **Orders**: Đơn hàng
- **Workspaces**: Không gian làm việc

## 🚀 Deployment

### Development Environment

```bash
# Start infrastructure
docker-compose -f deploy/docker-compose-local.yml up -d

# Start services in order
# 1. Discovery Server
# 2. API Gateway
# 3. Identity Service
# 4. Other services
```

### Production Environment

```bash
# Build all services
./build-all.sh

# Deploy with Docker Compose
docker-compose -f deploy/docker-compose.yml up -d
```

### Docker Images

- Services được build thành Docker images
- Push lên Docker Hub registry
- Environment-specific configuration

## 📞 Support

Nếu bạn gặp vấn đề hoặc có câu hỏi, vui lòng tạo issue trên GitHub repository.
