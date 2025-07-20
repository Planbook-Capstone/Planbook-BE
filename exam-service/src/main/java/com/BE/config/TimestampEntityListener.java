package com.BE.config;

import com.BE.utils.DateNowUtils;
import jakarta.persistence.PrePersist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

/**
 * Custom entity listener to handle timestamp fields using HCM timezone
 * instead of system default timezone
 */
@Component
public class TimestampEntityListener {

    private static DateNowUtils dateNowUtils;

    @Autowired
    public void setDateNowUtils(DateNowUtils dateNowUtils) {
        TimestampEntityListener.dateNowUtils = dateNowUtils;
    }

    @PrePersist
    public void prePersist(Object entity) {
        if (dateNowUtils == null) {
            return; // Fallback to default behavior if not initialized
        }

        LocalDateTime currentTime = dateNowUtils.getCurrentDateTimeHCM();
        
        // Set createdAt field if exists and is null
        setTimestampField(entity, "createdAt", currentTime);
        
        // Set submittedAt field if exists and is null (for ExamSubmission)
        setTimestampField(entity, "submittedAt", currentTime);
    }

    private void setTimestampField(Object entity, String fieldName, LocalDateTime value) {
        try {
            Field field = entity.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            
            // Only set if field is null (for creation timestamp)
            if (field.get(entity) == null) {
                field.set(entity, value);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Field doesn't exist or can't be accessed, ignore
        }
    }
}
