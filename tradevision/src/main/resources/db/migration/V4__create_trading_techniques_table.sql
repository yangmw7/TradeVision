-- TradeVision - 매매기법 마스터 데이터 테이블 생성

CREATE TABLE trading_techniques (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '매매기법 고유 ID',
    name VARCHAR(100) NOT NULL COMMENT '매매기법 이름',
    candle_type ENUM('M1', 'M5', 'M15', 'H1', 'D', 'W', 'M') NOT NULL COMMENT '적합한 봉 종류',
    description TEXT NOT NULL COMMENT '매매기법 설명',
    learning_content_id BIGINT COMMENT '관련 학습 콘텐츠 ID (선택)',
    priority INT NOT NULL DEFAULT 0 COMMENT '표시 우선순위 (낮을수록 먼저 표시)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    INDEX idx_candle_type (candle_type) COMMENT '봉 종류별 조회 성능'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='봉 종류별 추천 매매기법';
