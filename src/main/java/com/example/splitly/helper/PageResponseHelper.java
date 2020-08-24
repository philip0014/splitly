package com.example.splitly.helper;

import com.example.splitly.model.response.PageResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public class PageResponseHelper {

    public static <T> PageResponse<T> create(Page<T> page) {
        return PageResponse.<T>builder()
            .page(page.getNumber())
            .totalPage(page.getTotalPages())
            .size(page.getNumberOfElements())
            .totalSize((int) page.getTotalElements())
            .data(page.getContent())
            .build();
    }

    public static <T> PageResponse<T> create(List<T> content, int page, int totalPages, int size,
        int totalSize) {
        return PageResponse.<T>builder()
            .page(page)
            .totalPage(totalPages)
            .size(size)
            .totalSize(totalSize)
            .data(content)
            .build();
    }

}
