package com.BE.config;

import com.BE.utils.DateNowUtils;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;


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
        setTimestampFieldIfNull(entity, "createdAt", currentTime);

        // Set submittedAt field if exists and is null (for ExamSubmission)
        setTimestampFieldIfNull(entity, "submittedAt", currentTime);

        // Set updatedAt field if exists
        setTimestampField(entity, "updatedAt", currentTime);
    }

    @PreUpdate
    public void preUpdate(Object entity) {
        if (dateNowUtils == null) {
            return; // Fallback to default behavior if not initialized
        }

        LocalDateTime currentTime = dateNowUtils.getCurrentDateTimeHCM();

        // Always update updatedAt field if exists
        setTimestampField(entity, "updatedAt", currentTime);
    }

    private void setTimestampFieldIfNull(Object entity, String fieldName, LocalDateTime value) {
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

    private void setTimestampField(Object entity, String fieldName, LocalDateTime value) {
        try {
            Field field = entity.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            // Always set the field value
            field.set(entity, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Field doesn't exist or can't be accessed, ignore
        }
    }
}
