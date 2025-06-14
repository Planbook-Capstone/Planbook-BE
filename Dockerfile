# Sử dụng base image là OpenJDK 21 (alpine là một bản phân phối Linux nhỏ gọn và an toàn)

FROM openjdk:21-jdk-slim-bullseye
# Thông tin tác giả (tùy chọn)
LABEL maintainer="Planbook <planbook@gmail.com>"

# Đặt thư mục làm việc bên trong container là /app
WORKDIR /app
# Copy file JAR đã được build vào thư mục /app trong container và đổi tên thành be.jar

# 'target/*.jar' sẽ khớp với tên file JAR được tạo ra bởi Maven (ví dụ: planbook-be-0.0.1-SNAPSHOT.jar)
COPY target/*.jar be.jar
# Khai báo cổng mà ứng dụng Spring Boot sẽ lắng nghe (cổng này phải trùng với server.port trong Spring Boot)

EXPOSE 8080
# Lệnh để chạy ứng dụng khi container được khởi động
ENTRYPOINT ["java", "-jar", "be.jar"]