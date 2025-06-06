package com.BE.exception.handler;

import com.BE.utils.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class PropertyReferenceExceptionHandler {
    @Autowired
    private ResponseHandler responseHandler;

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<?> handlePropertyReferenceException(PropertyReferenceException ex) {
        return responseHandler.response(
                HttpStatus.BAD_REQUEST.value(),
                "Sort field does not exist: " + ex.getPropertyName(),
                null);
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<?> handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException ex) {
        if (ex.getCause() instanceof PropertyReferenceException) {
            PropertyReferenceException pre = (PropertyReferenceException) ex.getCause();
            return handlePropertyReferenceException(pre);
        }
        return responseHandler.response(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid data access: " + ex.getMessage(),
                null);
    }
}