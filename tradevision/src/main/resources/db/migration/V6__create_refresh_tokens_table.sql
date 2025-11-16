-- TradeVision - Refresh Token 테이블 생성

CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Refresh Token 고유 ID',
    user_id BIGINT NOT NULL COMMENT '사용자 ID (FK)',
    token VARCHAR(500) NOT NULL UNIQUE COMMENT 'Refresh Token 값',
    expires_at DATETIME NOT NULL COMMENT '만료 일시',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id) COMMENT '사용자별 토큰 조회',
    INDEX idx_token (token) COMMENT '토큰 검증 성능'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='JWT Refresh Token 저장';
