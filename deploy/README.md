# Planbook Microservices Deployment Guide

## Overview
Hệ thống Planbook bao gồm các microservices sau:

### Infrastructure Services
- **Kafka**: Message broker cho communication giữa các services
- **Redis**: Cache và session storage
- **Debezium**: Change data capture
- **Kafdrop**: Kafka UI management
- **Dozzle**: Container logs viewer
- **Portainer**: Docker container management

### Application Services
- **Discovery Server** (Port 8761): Service registry và discovery
- **API Gateway** (Port 8080): Entry point cho tất cả requests
- **Identity Service** (Port 8081): Authentication và authorization
- **Master Data Service** (Port 8084): Quản lý dữ liệu master
- **Purchase Service** (Port 8094): Xử lý thanh toán
- **Exam Service** (Port 8099): Quản lý bài thi
- **External Caller Service** (Port 8086): Gọi external APIs
- **External Tool Config Service** (Port 8087): Cấu hình external tools
- **Slide Template Service** (Port 8091): Quản lý slide templates
- **Websocket Service** (Port 8085): Real-time communication
- **Academic Resource Service** (Port 8095): Quản lý tài nguyên học tập
- **Lesson Plan Template Service** (Port 8082): Quản lý lesson plan templates
- **Tool Log Service** (Port 8088): Logging cho tools
- **Workspace Service** (Port 8092): Quản lý workspace
- **Aggregator** (Port 8090): Data aggregation service

## Deployment Options

### 1. Production Deployment
Sử dụng cho production environment với container networking:

```bash
# Copy .env file và cập nhật các giá trị production
cp .env.example .env

# Deploy tất cả services
docker compose -f deploy/docker-compose.yml up -d
```

### 2. Local Development
Sử dụng cho local development với localhost networking:

```bash
# Sử dụng .env.local cho development
cp .env.local .env

# Chỉ chạy infrastructure services
docker compose -f deploy/docker-compose-local.yml up -d

# Chạy các application services từ IDE hoặc command line
```

## Environment Variables

### Shared Variables (SHARED_*)
- `SHARED_HIBERNATE_DIALECT`: Hibernate dialect cho MySQL
- `SHARED_EUREKA_DEFAULT_ZONE`: Eureka server URL
- `SHARED_KAFKA_BOOTSTRAP_SERVERS`: Kafka bootstrap servers
- `SHARED_REDIS_HOST`: Redis host
- `SHARED_SUPABASE_*`: Supabase configuration


### Service-specific Variables
Mỗi service có các biến riêng:
- `{SERVICE}_APP_NAME`: Tên application
- `{SERVICE}_PORT`: Port của service
- `{SERVICE}_DB_*`: Database configuration
- `{SERVICE}_JWT_*`: JWT key paths

## File Structure

```
deploy/
├── docker-compose.yml          # Production deployment
├── docker-compose-local.yml    # Local development infrastructure
├── README.md                   # This file
.env                            # Production environment variables
.env.local                      # Local development environment variables
```

## Service Dependencies

### Startup Order
1. **Infrastructure**: Kafka, Redis
2. **Discovery Server**: Service registry
3. **Identity Service**: Authentication
4. **API Gateway**: Routing
5. **Other Services**: Business logic services

### Network Dependencies
- Tất cả services connect tới Discovery Server
- API Gateway route requests tới các services
- Services communicate qua Kafka
- Redis được sử dụng cho caching

## Monitoring & Management

### Access URLs (Production)
- **API Gateway**: http://your-domain:8080
- **Discovery Server**: http://your-domain:8761
- **Kafdrop (Kafka UI)**: http://your-domain:9000
- **Dozzle (Logs)**: http://your-domain:8888
- **Portainer**: http://your-domain:9001

### Access URLs (Local)
- **API Gateway**: http://localhost:8080
- **Discovery Server**: http://localhost:8761
- **Kafdrop**: http://localhost:9000
- **Dozzle**: http://localhost:8888
- **Portainer**: http://localhost:9001

## Troubleshooting

### Common Issues

1. **Service không start được**
   ```bash
   # Check logs
   docker compose logs [service-name]
   
   # Check service status
   docker compose ps
   ```

2. **Database connection issues**
   - Kiểm tra database credentials trong .env
   - Đảm bảo database server accessible

3. **Service discovery issues**
   - Đảm bảo Discovery Server đã start
   - Check Eureka dashboard tại http://localhost:8761

4. **Kafka connection issues**
   - Đảm bảo Kafka đã start
   - Check Kafdrop tại http://localhost:9000

### Useful Commands

```bash
# Start all services
docker compose up -d

# Stop all services
docker compose down

# View logs
docker compose logs -f [service-name]

# Restart specific service
docker compose restart [service-name]

# Pull latest images
docker compose pull

# Remove all containers and volumes
docker compose down -v --remove-orphans
```

## CI/CD

### GitHub Actions
File `.github/workflows/deploy-all.yml` tự động:
1. Build tất cả services khi push lên master branch
2. Push Docker images lên Docker Hub
3. Deploy lên VPS

### Manual Deployment
```bash
# Build và push images manually
docker build -t minhlola28/planbook-[service-name]:latest .
docker push minhlola28/planbook-[service-name]:latest
```

## Security Notes

- Tất cả sensitive data được store trong environment variables
- JWT keys được inject vào containers khi build
- Database passwords nên được encrypt
- Production deployment nên sử dụng HTTPS

## Support

Nếu gặp vấn đề, check:
1. Service logs: `docker compose logs [service-name]`
2. Eureka dashboard: http://localhost:8761
3. Kafka topics: http://localhost:9000
4. Container status: `docker compose ps`
