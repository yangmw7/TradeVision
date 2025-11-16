-- TradeVision - Phase 7: Learning Content System Tables

-- Drop old tables if they exist (from V3 and V5)
DROP TABLE IF EXISTS learning_progress;
DROP TABLE IF EXISTS learning_contents;

-- Content Modules Table (학습 모듈)
CREATE TABLE content_modules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '모듈 고유 ID',
    title VARCHAR(200) NOT NULL COMMENT '모듈 제목',
    title_en VARCHAR(200) COMMENT '영문 제목',
    description TEXT COMMENT '모듈 설명',
    difficulty_level VARCHAR(20) NOT NULL COMMENT '난이도 (BEGINNER/INTERMEDIATE/ADVANCED)',
    category VARCHAR(50) NOT NULL COMMENT '카테고리 (MOVING_AVERAGE, BOLLINGER_BANDS, MACD, RSI)',
    display_order INT NOT NULL COMMENT '표시 순서',
    estimated_duration_minutes INT NOT NULL COMMENT '예상 학습 시간(분)',
    thumbnail_url VARCHAR(500) COMMENT '썸네일 이미지 URL',
    is_required BOOLEAN NOT NULL DEFAULT FALSE COMMENT '필수 모듈 여부',
    is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT '활성화 상태',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',

    INDEX idx_difficulty_level (difficulty_level) COMMENT '난이도별 조회',
    INDEX idx_category (category) COMMENT '카테고리별 조회',
    INDEX idx_is_active (is_active) COMMENT '활성 상태 조회',
    INDEX idx_display_order (display_order) COMMENT '정렬용 인덱스'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='학습 콘텐츠 모듈';

-- Learning Contents Table (학습 콘텐츠)
CREATE TABLE learning_contents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '콘텐츠 고유 ID',
    module_id BIGINT NOT NULL COMMENT '소속 모듈 ID',
    title VARCHAR(200) NOT NULL COMMENT '콘텐츠 제목',
    title_en VARCHAR(200) COMMENT '영문 제목',
    summary TEXT COMMENT '요약',
    content_body TEXT NOT NULL COMMENT '콘텐츠 본문(Markdown)',
    content_type VARCHAR(20) NOT NULL COMMENT '콘텐츠 타입 (ARTICLE/VIDEO/QUIZ/INTERACTIVE/CASE_STUDY)',
    display_order INT NOT NULL COMMENT '표시 순서',
    estimated_duration_minutes INT NOT NULL COMMENT '예상 소요 시간(분)',
    video_url VARCHAR(500) COMMENT '비디오 URL',
    image_urls VARCHAR(2000) COMMENT '이미지 URL 목록 (쉼표 구분)',
    is_free BOOLEAN NOT NULL DEFAULT TRUE COMMENT '무료 콘텐츠 여부',
    is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT '활성화 상태',
    view_count BIGINT NOT NULL DEFAULT 0 COMMENT '조회수',
    like_count BIGINT NOT NULL DEFAULT 0 COMMENT '좋아요 수',
    quiz_data TEXT COMMENT '퀴즈 데이터 (JSON)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',

    FOREIGN KEY (module_id) REFERENCES content_modules(id) ON DELETE CASCADE,
    INDEX idx_module_id (module_id) COMMENT '모듈별 조회',
    INDEX idx_content_type (content_type) COMMENT '타입별 조회',
    INDEX idx_is_free (is_free) COMMENT '무료 콘텐츠 조회',
    INDEX idx_is_active (is_active) COMMENT '활성 상태 조회',
    INDEX idx_view_count (view_count) COMMENT '인기 콘텐츠 조회',
    INDEX idx_display_order (display_order) COMMENT '정렬용 인덱스'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='학습 콘텐츠';

-- User Content Progress Table (사용자 학습 진행도)
CREATE TABLE user_content_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '진행도 고유 ID',
    user_id BIGINT NOT NULL COMMENT '사용자 ID',
    content_id BIGINT NOT NULL COMMENT '콘텐츠 ID',
    is_completed BOOLEAN NOT NULL DEFAULT FALSE COMMENT '완료 여부',
    progress_percentage INT NOT NULL DEFAULT 0 COMMENT '진행률 (0-100)',
    total_time_spent_seconds BIGINT NOT NULL DEFAULT 0 COMMENT '총 학습 시간(초)',
    quiz_score INT COMMENT '퀴즈 점수',
    quiz_answers TEXT COMMENT '퀴즈 답안 (JSON)',
    user_notes TEXT COMMENT '사용자 메모',
    is_liked BOOLEAN NOT NULL DEFAULT FALSE COMMENT '좋아요 여부',
    is_bookmarked BOOLEAN NOT NULL DEFAULT FALSE COMMENT '북마크 여부',
    last_accessed_at DATETIME COMMENT '마지막 접근 일시',
    completed_at DATETIME COMMENT '완료 일시',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (content_id) REFERENCES learning_contents(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_content (user_id, content_id) COMMENT '사용자당 콘텐츠별 진행도는 하나',
    INDEX idx_user_id (user_id) COMMENT '사용자별 조회',
    INDEX idx_is_completed (is_completed) COMMENT '완료 상태 조회',
    INDEX idx_is_bookmarked (is_bookmarked) COMMENT '북마크 조회',
    INDEX idx_last_accessed_at (last_accessed_at) COMMENT '최근 학습 조회'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 콘텐츠 학습 진행도';
