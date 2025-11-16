package com.tradevision.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 콘텐츠 목록 응답 DTO (페이징)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentListResponse {

    /**
     * 콘텐츠 목록
     */
    private List<ContentResponse> contents;

    /**
     * 현재 페이지 번호 (0부터 시작)
     */
    private int currentPage;

    /**
     * 전체 페이지 수
     */
    private int totalPages;

    /**
     * 전체 콘텐츠 수
     */
    private long totalElements;

    /**
     * 페이지 크기
     */
    private int pageSize;

    /**
     * 마지막 페이지 여부
     */
    private boolean last;
}
