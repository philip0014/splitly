package com.example.splitly.helper;

import com.example.splitly.model.response.Response;
import org.springframework.http.HttpStatus;

public class ResponseHelper {

    public static <T> Response<T> ok(T data) {
        return Response.<T>builder()
            .code(HttpStatus.OK.value())
            .status(HttpStatus.OK.getReasonPhrase())
            .data(data)
            .build();
    }

    public static <T> Response<T> errorWithStatus(HttpStatus httpStatus) {
        return errorWithStatus(null, httpStatus);
    }

    public static <T> Response<T> errorWithStatus(T error, HttpStatus httpStatus) {
        return Response.<T>builder()
            .code(httpStatus.value())
            .status(httpStatus.getReasonPhrase())
            .error(error)
            .build();
    }

}
