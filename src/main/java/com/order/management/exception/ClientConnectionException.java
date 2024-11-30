package com.order.management.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClientConnectionException extends RuntimeException {

    private String errorMessage;

    private HttpStatusCode httpStatus;
}
