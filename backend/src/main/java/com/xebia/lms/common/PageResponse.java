/**
 * IAM Service — Enterprise LMS.
 * Xebia LMS Platform.
 *
 * @author Akash Sivanandan (akashsivaleena@gmail.com)
 */
package com.xebia.lms.common;

import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Page;

/**
 * Common wrapper for paginated API responses.
 *
 * Encapsulates the content list along with standard pagination metadata
 * to ensure consistency across all LMS APIs.
 *
 * @param <T> type of items in the page content list
 */
@Getter
public class PageResponse<T> {

    private final List<T> content;
    private final int pageNumber;
    private final int pageSize;
    private final long totalElements;
    private final int totalPages;
    private final boolean isLast;

    /**
     * Constructs a PageResponse wrapping a Spring Data {@link Page} object.
     *
     * @param page standard Spring Data Page instance
     */
    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.isLast = page.isLast();
    }

    /**
     * Constructs a PageResponse with manual pagination metadata.
     *
     * @param content content list
     * @param pageNumber page index (0-indexed)
     * @param pageSize size of page
     * @param totalElements total records matching query
     * @param totalPages total calculated pages
     * @param isLast whether this is the final page
     */
    public PageResponse(List<T> content, int pageNumber, int pageSize, long totalElements, int totalPages, boolean isLast) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.isLast = isLast;
    }
}

