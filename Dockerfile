# Sử dụng base image là OpenJDK 21 (alpine là một bản phân phối Linux nhỏ gọn và an toàn)
FROM openjdk:21-jdk-slim-bullseye

# Thông tin tác giả (tùy chọn)
LABEL maintainer="Planbook <planbook@gmail.com>"

# Đặt thư mục làm việc bên trong container là /app
WORKDIR /app

# Cài đặt curl trong image LẦN NÀY LÀ CỰC KỲ QUAN TRỌNG
# curl là cần thiết để thực hiện HTTP request cho HEALTHCHECK
# curl là cần thiết để thực hiện HTTP request cho HEALTHCHECK
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# BƯỚC MỚI: Xác minh curl đã được cài đặt
RUN which curl || (echo "ERROR: curl command was not found after installation. Aborting build." && exit 1)
# Copy file JAR đã được build vào thư mục /app trong container và đổi tên thành be.jar
COPY target/*.jar be.jar

# Khai báo cổng mà ứng dụng Spring Boot sẽ lắng nghe
EXPOSE 8080

# Định nghĩa Health Check cho container
HEALTHCHECK --interval=10s --timeout=5s --retries=3 \
  CMD curl --fail http://localhost:8080/actuator/health || exit 1

# Lệnh để chạy ứng dụng khi container được khởi động
ENTRYPOINT ["java", "-jar", "be.jar"]