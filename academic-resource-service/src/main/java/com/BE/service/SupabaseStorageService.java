package com.BE.service;

import com.BE.config.SupabaseConfig;
import com.BE.exception.FileUploadException;
import com.BE.model.response.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupabaseStorageService {

    private final SupabaseConfig supabaseConfig;

    public FileUploadResponse uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new FileUploadException("File is empty");
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost uploadRequest = new HttpPost(supabaseConfig.getUploadUrl() + "/" + uniqueFilename);

            // Set headers
            uploadRequest.setHeader("Authorization", "Bearer " + supabaseConfig.getSupabaseKey());

            // Determine content type - default to application/octet-stream if null
            String contentType = file.getContentType();
            if (contentType == null || contentType.isEmpty()) {
                // Try to determine content type from file extension
                if (fileExtension.toLowerCase().matches("\\.(jpg|jpeg)$")) {
                    contentType = "image/jpeg";
                } else if (fileExtension.toLowerCase().matches("\\.(png)$")) {
                    contentType = "image/png";
                } else if (fileExtension.toLowerCase().matches("\\.(gif)$")) {
                    contentType = "image/gif";
                } else if (fileExtension.toLowerCase().matches("\\.(pdf)$")) {
                    contentType = "application/pdf";
                } else {
                    contentType = "application/octet-stream";
                }
            }

            uploadRequest.setHeader("Content-Type", contentType);

            // Create binary entity directly (not multipart)
            HttpEntity entity = new InputStreamEntity(
                    file.getInputStream(), file.getSize(), ContentType.create(contentType));

            uploadRequest.setEntity(entity);

            try (ClassicHttpResponse response = httpClient.execute(uploadRequest)) {
                int statusCode = response.getCode();
                String responseBody = EntityUtils.toString(response.getEntity());

                if (statusCode >= 200 && statusCode < 300) {
                    String publicUrl = supabaseConfig.getPublicUrl(uniqueFilename);

                    return new FileUploadResponse(
                            uniqueFilename,
                            publicUrl,
                            file.getContentType(),
                            file.getSize(),
                            "File uploaded successfully");
                } else {
                    log.error("Failed to upload file. Status: {}, Response: {}", statusCode, responseBody);
                    throw new FileUploadException("Failed to upload file to Supabase Storage");
                }
            } catch (ParseException e) {
                log.error("Error uploading file: {}", uniqueFilename, e);
                throw new FileUploadException("Error uploading file to Supabase Storage", e);
            }
        }
    }

    public boolean deleteFile(String fileName) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpDelete deleteRequest = new HttpDelete(supabaseConfig.getDeleteUrl(fileName));

            // Set headers
            deleteRequest.setHeader("Authorization", "Bearer " + supabaseConfig.getSupabaseKey());

            try (ClassicHttpResponse response = httpClient.execute(deleteRequest)) {
                int statusCode = response.getCode();

                if (statusCode >= 200 && statusCode < 300) {
                    log.info("File deleted successfully: {}", fileName);
                    return true;
                } else {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    log.error("Failed to delete file. Status: {}, Response: {}", statusCode, responseBody);
                    return false;
                }
            }
        } catch (IOException | ParseException e) {
            log.error("Error deleting file: {}", fileName, e);
            return false;
        }
    }

    public String extractFileNameFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        String publicUrlPrefix = supabaseConfig.getPublicUrl("");
        if (url.startsWith(publicUrlPrefix)) {
            return url.substring(publicUrlPrefix.length());
        }

        return null;
    }
}
