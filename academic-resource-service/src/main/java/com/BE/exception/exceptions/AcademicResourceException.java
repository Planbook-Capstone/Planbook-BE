package com.BE.exception.exceptions;

/**
 * Exception thrown for academic resource related errors
 */
public class AcademicResourceException extends RuntimeException {
    
    public AcademicResourceException(String message) {
        super(message);
    }
    
    public AcademicResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
