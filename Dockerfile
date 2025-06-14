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

# Cài đặt curl trong image
# curl là cần thiết để thực hiện HTTP request cho HEALTHCHECK
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Định nghĩa Health Check cho container
# --interval=30s: Kiểm tra mỗi 30 giây
# --timeout=10s: Nếu kiểm tra mất hơn 10 giây thì coi như thất bại
# --retries=5: Thử lại 5 lần liên tiếp nếu thất bại trước khi đánh dấu container là "unhealthy"
# CMD curl --fail http://localhost:8080/actuator/health || exit 1: Lệnh thực hiện kiểm tra.
#    Nó sẽ gọi endpoint '/actuator/health' của Spring Boot.
#    Nếu curl trả về mã lỗi (do kết nối thất bại hoặc HTTP status code >= 400), thì lệnh 'exit 1' sẽ được chạy,
#    đánh dấu health check thất bại.
# Giảm interval và retries để phát hiện trạng thái khỏe mạnh nhanh hơn
HEALTHCHECK --interval=10s --timeout=5s --retries=3 \
  CMD curl --fail http://localhost:8080/actuator/health || exit 1
# Lệnh để chạy ứng dụng khi container được khởi động
ENTRYPOINT ["java", "-jar", "be.jar"]