-- TradeVision - 학습 진도 테이블 생성

CREATE TABLE learning_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '진도 고유 ID',
    user_id BIGINT NOT NULL COMMENT '사용자 ID (FK)',
    learning_content_id BIGINT NOT NULL COMMENT '학습 콘텐츠 ID (FK)',
    completed BOOLEAN DEFAULT FALSE COMMENT '완료 여부',
    completed_at DATETIME COMMENT '완료 일시',
    last_accessed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '마지막 접근 일시',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (learning_content_id) REFERENCES learning_contents(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_content (user_id, learning_content_id) COMMENT '사용자-콘텐츠 조합 고유성',
    INDEX idx_user_id (user_id) COMMENT '사용자별 진도 조회'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자별 학습 진도';
