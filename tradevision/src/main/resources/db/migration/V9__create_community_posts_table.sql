-- 커뮤니티 게시글 테이블 생성
CREATE TABLE IF NOT EXISTS community_posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '게시글 제목',
    content TEXT COMMENT '게시글 내용',
    chart_image_url VARCHAR(500) COMMENT '차트 이미지 URL',
    author VARCHAR(50) NOT NULL COMMENT '작성자 이름',
    category VARCHAR(50) COMMENT '카테고리',
    likes INT NOT NULL DEFAULT 0 COMMENT '좋아요 수',
    views INT NOT NULL DEFAULT 0 COMMENT '조회수',
    is_public BOOLEAN NOT NULL DEFAULT TRUE COMMENT '공개 여부',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    INDEX idx_created_at (created_at),
    INDEX idx_likes (likes),
    INDEX idx_category (category),
    INDEX idx_is_public (is_public)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='커뮤니티 게시글';

-- 샘플 데이터 삽입
INSERT INTO community_posts (title, content, chart_image_url, author, category, likes, views, is_public, created_at) VALUES
('삼성전자 단기 반등 패턴 포착', '삼성전자가 주요 지지선에서 반등하는 패턴을 보이고 있습니다. 거래량 증가와 함께 상승 모멘텀이 형성되고 있어 단기 매수 기회로 판단됩니다.', 'https://via.placeholder.com/800x600?text=Samsung+Chart', '김트레이더', '주식', 24, 156, TRUE, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
('KOSPI 200 지지선 분석', 'KOSPI 200 지수가 2500선에서 강한 지지를 받고 있습니다. 이동평균선 지지와 함께 반등 가능성이 높아 보입니다.', 'https://via.placeholder.com/800x600?text=KOSPI+Chart', '박투자', '지수', 18, 203, TRUE, DATE_SUB(NOW(), INTERVAL 5 HOUR)),
('미국 기술주 상승 추세 지속', 'NASDAQ의 기술주들이 강한 상승세를 이어가고 있습니다. 특히 AI 관련주들의 모멘텀이 강합니다.', 'https://via.placeholder.com/800x600?text=Tech+Stocks', '이분석가', '해외주식', 32, 287, TRUE, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('원달러 환율 1350선 돌파', '원달러 환율이 1350원을 돌파하며 상승세를 보이고 있습니다. 수출 기업에 긍정적인 영향이 예상됩니다.', 'https://via.placeholder.com/800x600?text=USD+KRW', '최마켓', '환율', 15, 178, TRUE, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('비트코인 44,000달러 저항선 테스트', '비트코인이 44,000달러 저항선을 테스트하고 있습니다. 돌파 시 추가 상승 가능성이 있습니다.', 'https://via.placeholder.com/800x600?text=Bitcoin+Chart', '정크립토', '코인', 41, 412, TRUE, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('현대차 신모델 출시 기대감', '현대차의 신모델 출시를 앞두고 주가가 강한 상승세를 보이고 있습니다.', 'https://via.placeholder.com/800x600?text=Hyundai+Chart', '송자동차', '주식', 27, 234, TRUE, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('NAVER 실적 발표 앞둔 차트 분석', 'NAVER가 실적 발표를 앞두고 횡보하고 있습니다. 실적에 따라 방향성이 결정될 것으로 보입니다.', 'https://via.placeholder.com/800x600?text=NAVER+Chart', '강테크', '주식', 19, 189, TRUE, DATE_SUB(NOW(), INTERVAL 3 DAY)),
('골드 안전자산 선호 현상', '금 가격이 지정학적 리스크로 인한 안전자산 선호 현상으로 상승하고 있습니다.', 'https://via.placeholder.com/800x600?text=Gold+Chart', '한골드', '원자재', 22, 145, TRUE, DATE_SUB(NOW(), INTERVAL 3 DAY)),
('S&P 500 신고가 경신', 'S&P 500 지수가 역사적 신고가를 경신하며 강한 상승세를 이어가고 있습니다.', 'https://via.placeholder.com/800x600?text=S&P+500', '윤미국', '해외주식', 35, 298, TRUE, DATE_SUB(NOW(), INTERVAL 4 DAY)),
('SK하이닉스 HBM 수요 증가', 'SK하이닉스가 HBM 수요 증가로 실적 개선이 기대되며 주가도 상승세입니다.', 'https://via.placeholder.com/800x600?text=SK+Hynix', '임반도체', '주식', 29, 267, TRUE, DATE_SUB(NOW(), INTERVAL 4 DAY)),
('이더리움 2.0 업그레이드 기대', '이더리움 2.0 업그레이드를 앞두고 가격이 상승하고 있습니다.', 'https://via.placeholder.com/800x600?text=Ethereum', '오블록', '코인', 38, 356, TRUE, DATE_SUB(NOW(), INTERVAL 5 DAY)),
('엔화 약세로 인한 일본 여행주 수혜', '엔화 약세가 지속되며 일본 여행 관련주들이 강세를 보이고 있습니다.', 'https://via.placeholder.com/800x600?text=JPY+Travel', '서여행', '주식', 16, 124, TRUE, DATE_SUB(NOW(), INTERVAL 5 DAY));
