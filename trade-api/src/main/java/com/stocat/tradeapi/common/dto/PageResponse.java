package com.stocat.tradeapi.common.dto;

import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class PageResponse<T> {

    private final List<T> content;
    private final int currentPage;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean hasNext;

    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.currentPage = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.hasNext = page.hasNext();
    }
}
