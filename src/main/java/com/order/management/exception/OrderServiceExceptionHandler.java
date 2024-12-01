package com.order.management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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
    private ErrorResponse errorResponseBuilder(String url, HttpStatusCode httpCode, String errorMessage) {
        return ErrorResponse.builder().httpStatus(httpCode.toString()).url(url).errorMessage(errorMessage).build();
    }
}
