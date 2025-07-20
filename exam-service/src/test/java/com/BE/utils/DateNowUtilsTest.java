package com.BE.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DateNowUtilsTest {

    @Autowired
    private DateNowUtils dateNowUtils;

    @Test
    void testGetCurrentDateTimeHCM() {
        // Get current time using DateNowUtils
        LocalDateTime hcmTime = dateNowUtils.getCurrentDateTimeHCM();
        
        // Get current time directly using HCM timezone
        LocalDateTime expectedHcmTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDateTime();
        
        // They should be very close (within 1 second)
        assertTrue(Math.abs(hcmTime.toLocalTime().toSecondOfDay() - expectedHcmTime.toLocalTime().toSecondOfDay()) <= 1,
                "HCM time should be within 1 second of expected time");
        
        // Should be same date
        assertEquals(expectedHcmTime.toLocalDate(), hcmTime.toLocalDate(),
                "Should return current date in HCM timezone");
        
        assertNotNull(hcmTime, "Should not return null");
    }

    @Test
    void testDateNow() {
        String dateString = dateNowUtils.dateNow();
        
        assertNotNull(dateString, "Should not return null");
        assertFalse(dateString.isEmpty(), "Should not return empty string");
        
        // Should match the expected format: dd/MM/yyyy HH:mm:ss
        assertTrue(dateString.matches("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}"),
                "Should match format dd/MM/yyyy HH:mm:ss");
        
        // Parse the string and verify it's close to current HCM time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime parsedTime = LocalDateTime.parse(dateString, formatter);
        LocalDateTime expectedHcmTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDateTime();
        
        // Should be within 1 second
        assertTrue(Math.abs(parsedTime.toLocalTime().toSecondOfDay() - expectedHcmTime.toLocalTime().toSecondOfDay()) <= 1,
                "Parsed time should be within 1 second of expected HCM time");
    }

    @Test
    void testTimezoneConsistency() {
        // Test multiple calls to ensure consistency
        LocalDateTime time1 = dateNowUtils.getCurrentDateTimeHCM();
        LocalDateTime time2 = dateNowUtils.getCurrentDateTimeHCM();
        
        // Should be very close (within 1 second)
        assertTrue(Math.abs(time1.toLocalTime().toSecondOfDay() - time2.toLocalTime().toSecondOfDay()) <= 1,
                "Multiple calls should return consistent times");
    }
}
