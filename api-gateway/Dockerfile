# Sử dụng một base image Java Runtime nhẹ
FROM eclipse-temurin:21-jre-jammy

# Đặt thư mục làm việc
WORKDIR /app

# Sao chép file jar đã được build từ trước vào container.
# Tên `app.jar` sẽ được chúng ta đặt cố định trong script CI/CD.
COPY app.jar .

# Cổng mà ứng dụng Spring Boot sẽ chạy bên trong container
EXPOSE 8080

# Lệnh để chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]