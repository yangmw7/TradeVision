package com.tradevision.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 매매기법 목록 응답 DTO (페이징)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechniqueListResponse {

    /**
     * 기법 목록
     */
    private List<TechniqueResponse> techniques;

    /**
     * 현재 페이지 번호 (0부터 시작)
     */
    private int currentPage;

    /**
     * 전체 페이지 수
     */
    private int totalPages;

    /**
     * 전체 기법 수
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
