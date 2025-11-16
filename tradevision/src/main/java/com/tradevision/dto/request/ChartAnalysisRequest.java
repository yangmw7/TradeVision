package com.tradevision.dto.request;

import com.tradevision.constant.CandleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * 차트 분석 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChartAnalysisRequest {

    /**
     * 차트 이미지 파일
     */
    @NotNull(message = "차트 이미지는 필수입니다")
    private MultipartFile chartImage;

    /**
     * 종목 코드 (6자리, 선택)
     */
    @Pattern(regexp = "^\\d{6}$", message = "종목 코드는 6자리 숫자여야 합니다")
    private String stockCode;

    /**
     * 종목명 (선택)
     */
    private String stockName;

    /**
     * 캔들 타입 (필수)
     */
    @NotNull(message = "캔들 타입은 필수입니다")
    private CandleType candleType;
}
