package com.tradevision.controller;

import com.tradevision.dto.request.ChartAnalysisRequest;
import com.tradevision.dto.response.ApiResponse;
import com.tradevision.dto.response.ChartAnalysisResponse;
import com.tradevision.service.ChartAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * 차트 분석 API 컨트롤러
 * AI 차트 분석 및 히스토리 관리 엔드포인트 제공
 */
@Tag(name = "Chart Analysis", description = "AI 차트 분석 관련 API")
@RestController
@RequestMapping("/api/chart-analysis")
@RequiredArgsConstructor
public class ChartAnalysisController {

    private final ChartAnalysisService chartAnalysisService;

    /**
     * 차트 이미지 분석 요청 API
     * POST /api/chart-analysis
     *
     * @param request        분석 요청 (이미지, 종목 정보)
     * @param authentication 인증 정보
     * @return 201 Created, AI 분석 결과
     */
    @Operation(summary = "차트 이미지 분석", description = "업로드된 차트 이미지를 AI로 분석합니다")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ChartAnalysisResponse>> analyzeChart(
            @ModelAttribute ChartAnalysisRequest request,
            Authentication authentication) {

        // 사용자 ID 추출 (실제 구현에서는 UserDetails에서 추출)
        Long userId = extractUserIdFromAuth(authentication);

        ChartAnalysisResponse result = chartAnalysisService.analyzeChart(request, userId);

        return ResponseEntity.ok(
                ApiResponse.success("차트 분석이 완료되었습니다", result)
        );
    }

    /**
     * 분석 히스토리 조회 API
     * GET /api/chart-analysis/history
     *
     * @param pageable       페이징 정보
     * @param authentication 인증 정보
     * @return 200 OK, 분석 히스토리 목록
     */
    @Operation(summary = "분석 히스토리 조회", description = "사용자의 차트 분석 히스토리를 조회합니다")
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<ChartAnalysisResponse>>> getAnalysisHistory(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            Authentication authentication) {

        Long userId = extractUserIdFromAuth(authentication);

        Page<ChartAnalysisResponse> history = chartAnalysisService.getAnalysisHistory(userId, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("분석 히스토리 조회에 성공했습니다", history)
        );
    }

    /**
     * 특정 분석 결과 상세 조회 API
     * GET /api/chart-analysis/{analysisId}
     *
     * @param analysisId     분석 ID
     * @param authentication 인증 정보
     * @return 200 OK, 분석 결과 상세
     */
    @Operation(summary = "분석 결과 상세 조회", description = "특정 분석 결과의 상세 정보를 조회합니다")
    @GetMapping("/{analysisId}")
    public ResponseEntity<ApiResponse<ChartAnalysisResponse>> getAnalysisById(
            @Parameter(description = "분석 ID", example = "1")
            @PathVariable Long analysisId,
            Authentication authentication) {

        Long userId = extractUserIdFromAuth(authentication);

        ChartAnalysisResponse analysis = chartAnalysisService.getAnalysisById(analysisId, userId);

        return ResponseEntity.ok(
                ApiResponse.success("분석 결과 조회에 성공했습니다", analysis)
        );
    }

    /**
     * Authentication 객체에서 사용자 ID 추출
     * TODO: 실제 UserDetails 구현에 맞게 수정 필요
     *
     * @param authentication 인증 정보
     * @return 사용자 ID
     */
    private Long extractUserIdFromAuth(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("인증 정보가 없습니다");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            // UserDetails에서 username(email)을 가져와서 DB에서 조회
            // 간단하게 하드코딩 (실제로는 UserRepository로 조회 필요)
            return 1L; // TODO: 실제 사용자 ID 조회 로직 구현
        }

        throw new IllegalStateException("유효하지 않은 인증 정보입니다");
    }
}
