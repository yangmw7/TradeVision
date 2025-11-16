package com.tradevision.controller;

import com.tradevision.constant.CandleType;
import com.tradevision.dto.request.StockSearchRequest;
import com.tradevision.dto.response.ApiResponse;
import com.tradevision.dto.response.StockPriceResponse;
import com.tradevision.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 주식 데이터 API 컨트롤러
 * 실시간 주식 시세 조회 엔드포인트 제공
 */
@Tag(name = "Stock", description = "주식 데이터 관련 API")
@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    /**
     * 주식 현재가 조회 API
     * GET /api/stocks/{stockCode}
     *
     * @param stockCode      종목 코드 (6자리)
     * @param candleType     캔들 타입 (선택, 기본값: 일봉)
     * @param authentication 인증 정보
     * @return 200 OK, 주식 시세 정보
     */
    @Operation(summary = "주식 현재가 조회", description = "종목 코드로 실시간 주식 시세를 조회합니다")
    @GetMapping("/{stockCode}")
    public ResponseEntity<ApiResponse<StockPriceResponse>> getStockPrice(
            @Parameter(description = "종목 코드 (6자리 숫자)", example = "005930")
            @PathVariable String stockCode,
            @Parameter(description = "캔들 타입", example = "D")
            @RequestParam(required = false) CandleType candleType,
            Authentication authentication) {

        StockPriceResponse stockPrice = stockService.getStockPrice(stockCode, candleType);

        return ResponseEntity.ok(
                ApiResponse.success("주식 시세 조회에 성공했습니다", stockPrice)
        );
    }

    /**
     * 주식 검색 API (POST 방식)
     * POST /api/stocks/search
     *
     * @param request        주식 검색 요청
     * @param authentication 인증 정보
     * @return 200 OK, 주식 시세 정보
     */
    @Operation(summary = "주식 검색", description = "종목 코드와 캔들 타입으로 주식 시세를 조회합니다")
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<StockPriceResponse>> searchStock(
            @Valid @RequestBody StockSearchRequest request,
            Authentication authentication) {

        StockPriceResponse stockPrice = stockService.getStockPrice(
                request.getStockCode(),
                request.getCandleType()
        );

        return ResponseEntity.ok(
                ApiResponse.success("주식 시세 조회에 성공했습니다", stockPrice)
        );
    }

    /**
     * 종목 코드 유효성 검증 API
     * GET /api/stocks/validate/{stockCode}
     *
     * @param stockCode      종목 코드 (6자리)
     * @param authentication 인증 정보
     * @return 200 OK, 유효 여부
     */
    @Operation(summary = "종목 코드 검증", description = "종목 코드가 유효한지 확인합니다")
    @GetMapping("/validate/{stockCode}")
    public ResponseEntity<ApiResponse<Boolean>> validateStockCode(
            @Parameter(description = "종목 코드 (6자리 숫자)", example = "005930")
            @PathVariable String stockCode,
            Authentication authentication) {

        boolean isValid = stockService.validateStockCode(stockCode);

        return ResponseEntity.ok(
                ApiResponse.success(
                        isValid ? "유효한 종목 코드입니다" : "유효하지 않은 종목 코드입니다",
                        isValid
                )
        );
    }
}
