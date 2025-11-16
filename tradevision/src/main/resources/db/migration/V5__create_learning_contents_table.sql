-- TradeVision - 학습 콘텐츠 테이블 생성

CREATE TABLE learning_contents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '학습 콘텐츠 고유 ID',
    category VARCHAR(50) NOT NULL UNIQUE COMMENT '카테고리 (예: 이동평균선, 볼린저밴드)',
    title VARCHAR(200) NOT NULL COMMENT '제목',
    description TEXT NOT NULL COMMENT '개념 설명',
    calculation_method TEXT COMMENT '계산 방법',
    usage_strategy TEXT COMMENT '활용 전략',
    pros_cons TEXT COMMENT '장단점',
    interactive_config JSON COMMENT '인터랙티브 차트 설정',
    related_content_ids JSON COMMENT '관련 기법 ID 배열',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',
    INDEX idx_category (category) COMMENT '카테고리별 조회 성능'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='기술적 분석 기법 학습 콘텐츠';
