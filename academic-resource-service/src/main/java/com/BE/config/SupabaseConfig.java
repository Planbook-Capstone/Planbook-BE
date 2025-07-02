package com.BE.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class SupabaseConfig {
    
    @Value("${supabase.url}")
    private String supabaseUrl;
    
    @Value("${supabase.key}")
    private String supabaseKey;
    
    @Value("${supabase.storage.bucket}")
    private String bucketName;
    
    public String getStorageUrl() {
        return supabaseUrl + "/storage/v1/object/" + bucketName;
    }
    
    public String getPublicUrl(String fileName) {
        return supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + fileName;
    }
    
    public String getUploadUrl() {
        return supabaseUrl + "/storage/v1/object/" + bucketName;
    }
    
    public String getDeleteUrl(String fileName) {
        return supabaseUrl + "/storage/v1/object/" + bucketName + "/" + fileName;
    }
}
