package com.BE.utils;


import com.BE.exception.exceptions.DateException;
import com.BE.model.request.AcademicYearRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateNowUtils {
    public String dateNow() {
        LocalDateTime localDateTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDateTime();
        return localDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    public LocalDateTime getCurrentDateTimeHCM() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        return zonedDateTime.toLocalDateTime();
    }


    public void validateAcademicYear(AcademicYearRequest request) {
        LocalDateTime now = getCurrentDateTimeHCM();

        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new DateException("Both Date From and Date To must be provided");
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new DateException("Date To must be later than Date From");
        }

        if (request.getStartDate().isBefore(now)) {
            throw new DateException("Date From cannot be in the past");
        }

        if (request.getEndDate().isBefore(now)) {
            throw new DateException("Date To cannot be in the past");
        }

    }
}
