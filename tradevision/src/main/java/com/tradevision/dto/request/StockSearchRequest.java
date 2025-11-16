package com.tradevision.dto.request;

import com.tradevision.constant.CandleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주식 조회 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockSearchRequest {

    /**
     * 종목 코드 (6자리 숫자, 예: 005930)
     */
    @NotBlank(message = "종목 코드는 필수입니다")
    @Pattern(regexp = "^\\d{6}$", message = "종목 코드는 6자리 숫자여야 합니다")
    private String stockCode;

    /**
     * 캔들 타입 (기본값: 일봉)
     */
    private CandleType candleType;
}
