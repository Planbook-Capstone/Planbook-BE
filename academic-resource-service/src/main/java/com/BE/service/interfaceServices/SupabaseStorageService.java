package com.BE.service.interfaceServices;

import com.BE.model.response.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface SupabaseStorageService {
    FileUploadResponse uploadFile(MultipartFile file);
    boolean deleteFile(String fileName);
    String extractFileNameFromUrl(String url);
}
