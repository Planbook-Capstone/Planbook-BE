# Sử dụng JDK 21 nhẹ
FROM eclipse-temurin:21-jdk-alpine

# Thư mục làm việc
WORKDIR /app

# Copy file jar vào container, không cần hardcode tên
COPY target/*.jar app.jar

# Mở port Eureka mặc định
EXPOSE 8761

# Lệnh chạy app
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]