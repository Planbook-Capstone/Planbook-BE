# Sử dụng base image là OpenJDK 21
FROM openjdk:21-jdk-slim-bullseye

LABEL maintainer="Planbook <planbook@gmail.com>"
WORKDIR /app

# Gom tất cả các bước cài đặt và xác minh curl vào MỘT lệnh RUN
# Điều này giúp loại bỏ mọi vấn đề về caching giữa các layer RUN nếu có
RUN apt-get update && \
    apt-get install -y curl --no-install-recommends && \
    rm -rf /var/lib/apt/lists/* && \
    echo "--- Verifying curl installation ---" && \
    which curl && \
    ls -l /usr/bin/curl && \
    echo "--- End curl verification ---" || \
    (echo "ERROR: curl installation or verification failed. Aborting build." && exit 1)


# Copy file JAR đã được build vào thư mục /app trong container và đổi tên thành be.jar
COPY target/*.jar be.jar

EXPOSE 8080

HEALTHCHECK --interval=10s --timeout=5s --retries=3 \
  CMD curl --fail http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "be.jar"]