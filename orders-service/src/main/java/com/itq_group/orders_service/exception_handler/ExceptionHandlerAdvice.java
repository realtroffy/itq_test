package com.itq_group.orders_service.exception_handler;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.itq_group.orders_service.exception.DateException;
import com.itq_group.orders_service.exception.OrderDetailsNotFoundException;
import com.itq_group.orders_service.exception.OrderNumberGeneratorServiceException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    public static final String REQUIRED_FIELD = "Field is required";
    public static final String INVALID_JSON = "The provided JSON contains invalid data";
    public static final String UNSUPPORTED_CONTENT_TYPE = "Unsupported Content-Type";
    public static final String SUPPORTED_CONTENT_TYPES = "Supported Content-Types: ";
    public static final String ARGUMENT_TYPE_MISMATCH = "Argument type mismatch: ";
    public static final String ILLEGAL_ARGUMENT = "Illegal argument: ";
    public static final String DATA_INTEGRITY_VIOLATION = "Data integrity violation occurred";

    @ExceptionHandler(JsonMappingException.class)
    public ResponseEntity<Map<String, String>> handleJsonMappingException(JsonMappingException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getPath().forEach(reference -> {
            String fieldName = reference.getFieldName();
            errors.put(fieldName, REQUIRED_FIELD);
        });
        return new ResponseEntity<>(errors, BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleNotValidJsonException(HttpMessageNotReadableException exception) {
        return new ResponseEntity<>(INVALID_JSON, BAD_REQUEST);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException ex) {

        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("error", UNSUPPORTED_CONTENT_TYPE);
        errorDetails.put("message", ex.getMessage());

        String supportedMediaTypes = ex.getSupportedMediaTypes().toString();
        errorDetails.put("supportedTypes", SUPPORTED_CONTENT_TYPES + supportedMediaTypes);

        return new ResponseEntity<>(errorDetails, UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.status(BAD_REQUEST).body(ARGUMENT_TYPE_MISMATCH + e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(BAD_REQUEST).body(ILLEGAL_ARGUMENT + e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return new ResponseEntity<>(DATA_INTEGRITY_VIOLATION, BAD_REQUEST);
    }

    @ExceptionHandler(DateException.class)
    public ResponseEntity<String> handleDateException(DateException ex) {
        return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler(OrderDetailsNotFoundException.class)
    public ResponseEntity<String> handleOrderDetailsNotFoundException(OrderDetailsNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler(OrderNumberGeneratorServiceException.class)
    public ResponseEntity<String> handleOrderNumberGeneratorServiceException(OrderNumberGeneratorServiceException ex) {
        return new ResponseEntity<>(ex.getMessage(), INTERNAL_SERVER_ERROR);
    }
}
