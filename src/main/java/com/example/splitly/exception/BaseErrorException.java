package com.example.splitly.exception;

import org.springframework.http.HttpStatus;

public class BaseErrorException extends RuntimeException {

    private HttpStatus httpStatus;

    public BaseErrorException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

}
