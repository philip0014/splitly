package com.example.splitly.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {

    private int code;
    private String status;
    private T data;
    private T error;

}
