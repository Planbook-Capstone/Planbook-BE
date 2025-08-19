package com.BE.exception.handler;

import com.BE.exception.exceptions.AcademicResourceException;
import com.BE.exception.exceptions.EnumValidationException;
import com.BE.exception.exceptions.FileUploadException;
import com.BE.exception.exceptions.InvalidRefreshTokenException;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.exception.exceptions.ResourceNotFoundException;
import com.BE.model.response.DataResponseDTO;
import com.BE.utils.EnumUtils;
import com.BE.utils.ResponseHandler;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private final ResponseHandler responseHandler;

    public GlobalExceptionHandler(ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(propertyPath, message);
        }
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex,
            WebRequest request) {
        Map<String, String> errors = new HashMap<>();

        Throwable mostSpecificCause = ex.getMostSpecificCause();
        String message = "Failed to parse JSON";

        if (mostSpecificCause instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) mostSpecificCause;
            List<JsonMappingException.Reference> path = ife.getPath();
            String fieldName = path.get(path.size() - 1).getFieldName();
            Class<?> targetType = ife.getTargetType();
            String value = ife.getValue().toString();

            if (targetType.isEnum()) {
                String validValues = EnumUtils.getValidEnumValues(targetType.asSubclass(Enum.class));
                message = String.format("Field '%s' has invalid value '%s'. Expected one of: %s", fieldName, value,
                        validValues);
            } else {
                message = String.format("Field '%s' has invalid value '%s'. Expected type: %s", fieldName, value,
                        targetType.getSimpleName());
            }
        } else {
            message = mostSpecificCause.getMessage();
        }

        errors.put("message", message);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMostSpecificCause().getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EnumValidationException.class)
    public ResponseEntity<Map<String, String>> handleEnumValidationException(EnumValidationException ex,
            WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<String> handleInvalidRefreshTokenException(InvalidRefreshTokenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    // Academic Resource Exception Handlers
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<DataResponseDTO<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        return responseHandler.response(404, ex.getMessage(), null);
    }

    @ExceptionHandler(AcademicResourceException.class)
    public ResponseEntity<DataResponseDTO<Object>> handleAcademicResourceException(AcademicResourceException ex) {
        log.error("Academic resource error: {}", ex.getMessage());
        return responseHandler.response(400, ex.getMessage(), null);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<DataResponseDTO<Object>> handleMissingServletRequestPartException(
            MissingServletRequestPartException ex) {
        log.error("Missing request part: {}", ex.getMessage());
        return responseHandler.response(400, "Missing required request part: " + ex.getRequestPartName(), null);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<DataResponseDTO<Object>> handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException ex) {
        log.error("Unsupported media type: {}", ex.getMessage());
        return responseHandler.response(415, "Unsupported media type", null);
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<DataResponseDTO<Object>> handleFileUploadException(FileUploadException ex) {
        log.error("File upload error: {}", ex.getMessage());
        return responseHandler.response(400, ex.getMessage(), null);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<DataResponseDTO<Object>> handleIOException(IOException ex) {
        log.error("IO error: {}", ex.getMessage());
        return responseHandler.response(500, "File operation failed", null);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<DataResponseDTO<Object>> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex) {
        log.error("File size exceeded: {}", ex.getMessage());
        return responseHandler.response(400, "File size exceeds maximum allowed limit", null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<DataResponseDTO<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Invalid argument: {}", ex.getMessage());
        return responseHandler.response(400, ex.getMessage(), null);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<DataResponseDTO<Object>> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime error: {}", ex.getMessage(), ex);
        return responseHandler.response(500, "An error occurred: " + ex.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DataResponseDTO<Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return responseHandler.response(500, "An unexpected error occurred", null);
    }

}
