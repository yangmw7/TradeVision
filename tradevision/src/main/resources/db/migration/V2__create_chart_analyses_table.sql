-- TradeVision - 차트 분석 히스토리 테이블 생성

CREATE TABLE chart_analyses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '분석 고유 ID',
    user_id BIGINT NOT NULL COMMENT '사용자 ID (FK)',
    stock_code VARCHAR(10) COMMENT '종목 코드',
    stock_name VARCHAR(100) COMMENT '종목명',
    candle_type ENUM('M1', 'M5', 'M15', 'H1', 'D', 'W', 'M') NOT NULL COMMENT '봉 종류',
    image_path VARCHAR(500) NOT NULL COMMENT '업로드된 차트 이미지 경로',
    analysis_result JSON NOT NULL COMMENT 'AI 분석 결과 (패턴, 지지/저항선, 의견 등)',
    feedback ENUM('SUCCESS', 'FAIL', 'NONE') DEFAULT 'NONE' COMMENT '사용자 피드백',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '분석 일시',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id) COMMENT '사용자별 조회 성능',
    INDEX idx_stock_code (stock_code) COMMENT '종목별 조회 성능',
    INDEX idx_created_at (created_at) COMMENT '날짜별 조회 성능'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='차트 분석 히스토리';
