package com.example.splitly.web.handler;

import com.example.splitly.exception.BaseErrorException;
import com.example.splitly.helper.ResponseHelper;
import com.example.splitly.model.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(BaseErrorException.class)
    public Response baseErrorException(BaseErrorException ex) {
        log.info("Handling BaseErrorException with message: {}", ex.getMessage());
        return ResponseHelper.errorWithStatus(ex.getMessage(), ex.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public Response internalServerError(Exception ex) {
        log.info("Handling Exception with message: {}", ex.getMessage());
        return ResponseHelper.errorWithStatus(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response validationError(MethodArgumentNotValidException ex) {
        log.info("Handling MethodArgumentNotValidException with message: {}", ex.getMessage());
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<String> errors = fieldErrors.stream().map(fieldError -> String
            .format("{\"%s\": \"%s\"}", fieldError.getField(), fieldError.getDefaultMessage()))
            .collect(Collectors.toList());

        return ResponseHelper.errorWithStatus(errors.toString(), HttpStatus.BAD_REQUEST);
    }

}
