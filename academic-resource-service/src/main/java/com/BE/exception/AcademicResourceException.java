package com.BE.exception;

public class AcademicResourceException extends RuntimeException {
    
    public AcademicResourceException(String message) {
        super(message);
    }
    
    public AcademicResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
