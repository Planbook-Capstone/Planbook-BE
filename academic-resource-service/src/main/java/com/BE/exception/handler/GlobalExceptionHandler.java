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
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();

        Throwable mostSpecificCause = ex.getMostSpecificCause();
        String message = "Không thể phân tích dữ liệu JSON";

        if (mostSpecificCause instanceof InvalidFormatException ife) {
            List<JsonMappingException.Reference> path = ife.getPath();
            String fieldName = path.get(path.size() - 1).getFieldName();
            Class<?> targetType = ife.getTargetType();
            String value = ife.getValue().toString();

            if (targetType.isEnum()) {
                String validValues = EnumUtils.getValidEnumValues(targetType.asSubclass(Enum.class));
                message = String.format("Trường '%s' có giá trị không hợp lệ '%s'. Giá trị hợp lệ: %s", fieldName, value, validValues);
            } else {
                message = String.format("Trường '%s' có giá trị không hợp lệ '%s'. Kiểu dữ liệu mong đợi: %s", fieldName, value, targetType.getSimpleName());
            }
        } else {
            message = mostSpecificCause.getMessage();
        }

        errors.put("message", message);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Tham số không đúng kiểu dữ liệu: " + ex.getMostSpecificCause().getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException exception) {
        return new ResponseEntity<>("Không tìm thấy dữ liệu", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EnumValidationException.class)
    public ResponseEntity<Map<String, String>> handleEnumValidationException(EnumValidationException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Giá trị enum không hợp lệ: " + ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<String> handleInvalidRefreshTokenException(InvalidRefreshTokenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token làm mới không hợp lệ");
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<DataResponseDTO<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return responseHandler.response(404, "Không tìm thấy tài nguyên", null);
    }

    @ExceptionHandler(AcademicResourceException.class)
    public ResponseEntity<DataResponseDTO<Object>> handleAcademicResourceException(AcademicResourceException ex) {
        return responseHandler.response(400, "Tài nguyên học thuật không hợp lệ", null);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<DataResponseDTO<Object>> handleMissingServletRequestPartException(MissingServletRequestPartException ex) {
        return responseHandler.response(400, "Thiếu phần bắt buộc trong yêu cầu: " + ex.getRequestPartName(), null);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<DataResponseDTO<Object>> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        return responseHandler.response(415, "Định dạng dữ liệu không được hỗ trợ", null);
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<DataResponseDTO<Object>> handleFileUploadException(FileUploadException ex) {
        return responseHandler.response(400, "Tải tệp thất bại: " + ex.getMessage(), null);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<DataResponseDTO<Object>> handleIOException(IOException ex) {
        return responseHandler.response(500, "Thao tác với tệp thất bại", null);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<DataResponseDTO<Object>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        return responseHandler.response(400, "Kích thước tệp vượt quá giới hạn cho phép", null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<DataResponseDTO<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return responseHandler.response(400, "Tham số không hợp lệ: " + ex.getMessage(), null);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<DataResponseDTO<Object>> handleRuntimeException(RuntimeException ex) {
        return responseHandler.response(500, "Đã xảy ra lỗi hệ thống: " + ex.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DataResponseDTO<Object>> handleGenericException(Exception ex) {
        return responseHandler.response(500, "Đã xảy ra lỗi không xác định", null);
    }
}
