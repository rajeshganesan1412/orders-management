package com.order.management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.KafkaException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class OrderServiceExceptionHandler {

    @ExceptionHandler(ClientConnectionException.class)
    public ResponseEntity<ErrorResponse> handleClientConnectionException(ClientConnectionException ex, WebRequest request) {
        String path =  request.getDescription(false);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseBuilder(path, ex.getHttpStatus(), ex.getErrorMessage()));
    }

    @ExceptionHandler(EmptyCartItemsException.class)
    public ResponseEntity<ErrorResponse> handleEmptyCartItemsException(EmptyCartItemsException ex, WebRequest request) {
        String path =  request.getDescription(false);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseBuilder(path, ex.getHttpStatus(), ex.getErrorMessage()));
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFoundException(OrderNotFoundException ex, WebRequest request) {
        String path =  request.getDescription(false);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseBuilder(path, ex.getHttpStatus(), ex.getErrorMessage()));
    }

    @ExceptionHandler(KafkaException.class)
    public ResponseEntity<ErrorResponse> handleKafkaException(KafkaException ex, WebRequest request) {
        String path =  request.getDescription(false);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseBuilder(path, null , ex.getMessage()));
    }

    private ErrorResponse errorResponseBuilder(String url, HttpStatusCode httpCode, String errorMessage) {
        return ErrorResponse.builder().httpStatus(httpCode != null ? httpCode.toString() : null).url(url).errorMessage(errorMessage).build();
    }
}
