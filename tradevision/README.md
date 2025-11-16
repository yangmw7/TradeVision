# TradeVision

## 프로젝트 개요

TradeVision은 한국 주식 시장의 초보 투자자를 위한 AI 기반 학습 플랫폼입니다. 실시간 주식 데이터, AI 차트 분석, 그리고 체계적인 기술적 분석 학습 콘텐츠를 제공하여 투자자들이 올바른 투자 습관을 형성할 수 있도록 돕습니다.

### 주요 목표
- 한국투자증권 OpenAPI를 활용한 실시간 주식 데이터 제공
- OpenAI GPT-4 Vision을 활용한 차트 패턴 분석
- 체계적인 기술적 분석 학습 시스템
- 초보자 맞춤형 투자 교육 콘텐츠

### 타겟 사용자
- 주식 투자를 시작하려는 초보 투자자
- 기술적 분석을 배우고 싶은 학습자
- 체계적인 투자 습관을 형성하고 싶은 투자자

## 기술 스택

### Backend
- **Java 17**
- **Spring Boot 3.5.7**
- **Spring Security** - JWT 기반 인증/인가
- **Spring Data JPA** - 데이터 영속성 관리
- **Hibernate** - ORM
- **Flyway** - 데이터베이스 마이그레이션
- **Lombok** - 보일러플레이트 코드 감소

### Database
- **MariaDB 11.7** - 프로덕션 환경
- **H2 Database** - 테스트 환경

### External APIs
- **한국투자증권 OpenAPI (KIS API)** - 실시간 주식 데이터
- **OpenAI GPT-4 Vision API** - 차트 이미지 분석

### Security
- **JWT (JSON Web Token)** - 인증 토큰
- **BCrypt** - 비밀번호 암호화
- **JJWT 0.12.3** - JWT 구현 라이브러리

### Resilience
- **Resilience4j** - Circuit Breaker, Retry 패턴
- **Spring WebClient** - 비동기 HTTP 클라이언트

### Testing
- **JUnit 5** - 단위 테스트 프레임워크
- **Mockito** - 모킹 프레임워크
- **AssertJ** - Fluent assertion 라이브러리
- **Spring Boot Test** - 통합 테스트

### DevOps
- **Docker Compose** - 로컬 개발 환경
- **Gradle** - 빌드 도구

## 구현된 기능

### 1. 사용자 인증 시스템 (Phase 2)
JWT 기반의 안전한 인증 시스템

**주요 기능:**
- 회원가입 (이메일, 비밀번호, 닉네임, 투자 레벨)
- 로그인 (Access Token + Refresh Token)
- 토큰 갱신 (Refresh Token을 통한 Access Token 재발급)
- BCrypt를 통한 안전한 비밀번호 해싱

**API 엔드포인트:**
- `POST /api/auth/signup` - 회원가입
- `POST /api/auth/login` - 로그인
- `POST /api/auth/refresh` - 토큰 갱신

**테스트:** 21/21 tests passing

### 2. AI 차트 분석 (Phase 5)
OpenAI GPT-4 Vision을 활용한 차트 패턴 분석

**주요 기능:**
- 차트 이미지 업로드 및 분석 (JPG/PNG, 최대 5MB)
- AI 기반 패턴 인식 및 매매 추천
- 일일 분석 횟수 제한 (하루 10회)
- 분석 히스토리 조회 및 페이지네이션
- 사용자별 분석 격리

**API 엔드포인트:**
- `POST /api/chart-analysis` - 차트 이미지 분석 요청
- `GET /api/chart-analysis/history` - 분석 히스토리 조회
- `GET /api/chart-analysis/{id}` - 특정 분석 결과 조회

**기술 구현:**
- Base64 인코딩으로 OpenAI API 전송
- UUID 기반 파일 저장
- Resilience4j Circuit Breaker + Retry 패턴
- 구조화된 JSON 응답 파싱

**테스트:** 28/28 tests passing

### 3. 트레이딩 기법 학습 시스템 (Phase 6)
체계적인 기술적 분석 학습 콘텐츠

**주요 기능:**
- 기술적 분석 기법 라이브러리 (이동평균선, 볼린저밴드, MACD, RSI 등)
- 기법별 상세 정보 (설명, 계산식, 활용법, 주의사항)
- 난이도별 기법 분류
- 인기 기법 조회
- 키워드 검색
- 북마크 기능

**API 엔드포인트:**
- `GET /api/trading-techniques` - 모든 기법 조회
- `GET /api/trading-techniques/{id}` - 특정 기법 상세 조회
- `GET /api/trading-techniques/category/{category}` - 카테고리별 조회
- `GET /api/trading-techniques/difficulty/{level}` - 난이도별 조회
- `GET /api/trading-techniques/popular` - 인기 기법 조회
- `GET /api/trading-techniques/search` - 키워드 검색
- `POST /api/trading-techniques/{id}/bookmark` - 북마크 토글

**테스트:** 22/22 tests passing

### 4. 학습 콘텐츠 시스템 (Phase 7)
모듈 기반 학습 콘텐츠 관리 및 진행도 추적

**주요 기능:**
- 학습 모듈 관리 (난이도, 카테고리, 순서)
- 다양한 콘텐츠 타입 지원 (문서, 비디오, 퀴즈, 실습, 사례 분석)
- 사용자별 학습 진행도 추적
- 학습 시간 추적
- 퀴즈 점수 기록
- 좋아요 / 북마크 기능
- 학습 통계 (완료율, 평균 점수, 학습 레벨)
- 학습 레벨 시스템 (초보자 → 학습자 → 숙련자 → 전문가 → 마스터)

**API 엔드포인트:**

**모듈 관리:**
- `GET /api/learning/modules` - 모든 모듈 조회
- `GET /api/learning/modules/{id}` - 특정 모듈 조회
- `GET /api/learning/modules/category/{category}` - 카테고리별 모듈 조회
- `GET /api/learning/modules/difficulty/{level}` - 난이도별 모듈 조회
- `GET /api/learning/modules/required` - 필수 모듈 조회

**콘텐츠 관리:**
- `GET /api/learning/contents/{id}` - 콘텐츠 상세 조회
- `GET /api/learning/modules/{moduleId}/contents` - 모듈별 콘텐츠 조회
- `GET /api/learning/contents/type/{type}` - 타입별 콘텐츠 조회
- `GET /api/learning/contents/free` - 무료 콘텐츠 조회
- `GET /api/learning/contents/popular` - 인기 콘텐츠 조회
- `GET /api/learning/contents/search` - 키워드 검색

**학습 진행도:**
- `POST /api/learning/contents/{id}/progress` - 진행도 업데이트
- `GET /api/learning/progress/recent` - 최근 학습 콘텐츠 조회
- `GET /api/learning/progress/completed` - 완료한 콘텐츠 조회
- `GET /api/learning/progress/in-progress` - 진행중인 콘텐츠 조회
- `GET /api/learning/progress/bookmarked` - 북마크한 콘텐츠 조회
- `GET /api/learning/progress/stats` - 학습 통계 조회
- `POST /api/learning/contents/{id}/like` - 좋아요 토글
- `POST /api/learning/contents/{id}/bookmark` - 북마크 토글

**테스트:** 59/59 tests passing

## 데이터베이스 구조

### ERD 개요

```
users (사용자)
  ↓ 1:N
refresh_tokens (리프레시 토큰)

users
  ↓ 1:N
chart_analyses (차트 분석)

users
  ↓ 1:N
user_technique_bookmarks (기법 북마크)
  ↓ N:1
trading_techniques (트레이딩 기법)

content_modules (학습 모듈)
  ↓ 1:N
learning_contents (학습 콘텐츠)
  ↓ 1:N
user_content_progress (사용자 학습 진행도)
  ↓ N:1
users
```

### 주요 테이블 설명

#### 1. users (사용자)
사용자 계정 정보를 저장합니다.

**주요 컬럼:**
- `id`: 사용자 고유 ID (PK)
- `email`: 이메일 (UNIQUE)
- `password`: BCrypt 해시된 비밀번호
- `nickname`: 닉네임
- `investment_level`: 투자 레벨 (BEGINNER, INTERMEDIATE, ADVANCED)
- `created_at`, `updated_at`: 타임스탬프

#### 2. refresh_tokens (리프레시 토큰)
JWT Refresh Token을 관리합니다.

**주요 컬럼:**
- `id`: 토큰 고유 ID (PK)
- `user_id`: 사용자 ID (FK)
- `token`: Refresh Token 문자열 (UNIQUE)
- `expires_at`: 만료 시간
- `created_at`: 생성 시간

#### 3. chart_analyses (차트 분석)
AI 차트 분석 결과를 저장합니다.

**주요 컬럼:**
- `id`: 분석 고유 ID (PK)
- `user_id`: 사용자 ID (FK)
- `image_path`: 차트 이미지 저장 경로
- `analysis_result`: AI 분석 결과 (JSON, TEXT)
- `stock_code`: 종목 코드
- `analyzed_at`: 분석 시간

**제약:**
- 일일 분석 횟수 제한 (하루 10회)

#### 4. trading_techniques (트레이딩 기법)
기술적 분석 기법 정보를 저장합니다.

**주요 컬럼:**
- `id`: 기법 고유 ID (PK)
- `name`: 기법 이름
- `category`: 카테고리 (MOVING_AVERAGE, BOLLINGER_BANDS, MACD, RSI 등)
- `difficulty_level`: 난이도 (BEGINNER, INTERMEDIATE, ADVANCED)
- `description`: 기법 설명 (TEXT)
- `how_to_use`: 활용 방법 (TEXT)
- `formula`: 계산식
- `cautions`: 주의사항 (TEXT)
- `view_count`: 조회수
- `bookmark_count`: 북마크 수

#### 5. user_technique_bookmarks (기법 북마크)
사용자가 북마크한 트레이딩 기법을 저장합니다.

**주요 컬럼:**
- `id`: 북마크 고유 ID (PK)
- `user_id`: 사용자 ID (FK)
- `technique_id`: 기법 ID (FK)
- `created_at`: 생성 시간

**제약:**
- UNIQUE(user_id, technique_id) - 중복 북마크 방지

#### 6. content_modules (학습 모듈)
학습 콘텐츠를 그룹화하는 모듈을 저장합니다.

**주요 컬럼:**
- `id`: 모듈 고유 ID (PK)
- `title`: 모듈 제목
- `title_en`: 영어 제목
- `description`: 모듈 설명 (TEXT)
- `difficulty_level`: 난이도 (BEGINNER, INTERMEDIATE, ADVANCED)
- `category`: 카테고리 (MOVING_AVERAGE, BOLLINGER_BANDS 등)
- `display_order`: 표시 순서
- `estimated_duration_minutes`: 예상 소요 시간
- `is_required`: 필수 모듈 여부
- `is_active`: 활성화 여부

#### 7. learning_contents (학습 콘텐츠)
실제 학습 자료를 저장합니다.

**주요 컬럼:**
- `id`: 콘텐츠 고유 ID (PK)
- `module_id`: 모듈 ID (FK)
- `title`: 콘텐츠 제목
- `title_en`: 영어 제목
- `summary`: 요약
- `content_body`: 콘텐츠 본문 (TEXT)
- `content_type`: 콘텐츠 타입 (ARTICLE, VIDEO, QUIZ, INTERACTIVE, CASE_STUDY)
- `display_order`: 표시 순서
- `estimated_duration_minutes`: 예상 소요 시간
- `is_free`: 무료 콘텐츠 여부
- `is_active`: 활성화 여부
- `view_count`: 조회수
- `like_count`: 좋아요 수

#### 8. user_content_progress (사용자 학습 진행도)
사용자별 학습 진행 상황을 추적합니다.

**주요 컬럼:**
- `id`: 진행도 고유 ID (PK)
- `user_id`: 사용자 ID (FK)
- `content_id`: 콘텐츠 ID (FK)
- `is_completed`: 완료 여부
- `progress_percentage`: 진행률 (0-100)
- `total_time_spent_seconds`: 총 학습 시간 (초)
- `quiz_score`: 퀴즈 점수
- `completed_at`: 완료 시간
- `last_accessed_at`: 마지막 접근 시간
- `is_liked`: 좋아요 여부
- `is_bookmarked`: 북마크 여부
- `user_notes`: 사용자 메모 (TEXT)

**제약:**
- UNIQUE(user_id, content_id) - 사용자당 하나의 진행도만 존재

**비즈니스 로직:**
- `progress_percentage`가 100에 도달하면 자동으로 `is_completed = true`
- `completed_at`은 완료 시점에 자동 설정

## 설치 및 실행 방법

### Prerequisites
- **JDK 17** 이상
- **Gradle 7.x** 이상
- **Docker** & **Docker Compose** (로컬 개발용)
- **MariaDB 11.7** (프로덕션 환경)

### 환경 변수 설정

프로젝트 루트에 `.env` 파일을 생성하고 다음 환경 변수를 설정하세요:

```bash
# JWT 설정
JWT_SECRET=your-very-long-and-secure-secret-key-at-least-256-bits
JWT_ACCESS_TOKEN_EXPIRATION=3600000
JWT_REFRESH_TOKEN_EXPIRATION=604800000

# 데이터베이스 설정
DB_HOST=localhost
DB_PORT=3306
DB_NAME=tradevision
DB_USERNAME=tradevision
DB_PASSWORD=your-database-password

# 한국투자증권 OpenAPI 설정
KIS_APP_KEY=your-kis-app-key
KIS_APP_SECRET=your-kis-app-secret
KIS_BASE_URL=https://openapi.koreainvestment.com:9443

# OpenAI API 설정
OPENAI_API_KEY=your-openai-api-key
OPENAI_API_URL=https://api.openai.com/v1

# 파일 업로드 설정
FILE_UPLOAD_DIR=./uploads
```

### 로컬 개발 환경 실행

#### 1. 데이터베이스 실행 (Docker Compose)

```bash
docker-compose up -d
```

이 명령어는 MariaDB 11.7 컨테이너를 실행합니다.

#### 2. 애플리케이션 빌드

```bash
./gradlew clean build
```

#### 3. 애플리케이션 실행

**개발 프로필로 실행:**
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

**또는 JAR 파일로 실행:**
```bash
java -jar build/libs/tradevision-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

애플리케이션은 기본적으로 `http://localhost:8080`에서 실행됩니다.

### 테스트 실행

#### 전체 테스트 실행
```bash
./gradlew test
```

#### 특정 테스트 클래스 실행
```bash
./gradlew test --tests "com.tradevision.service.AuthServiceTest"
```

#### 테스트 커버리지 리포트 생성
```bash
./gradlew test jacocoTestReport
```

리포트는 `build/reports/jacoco/test/html/index.html`에서 확인할 수 있습니다.

### 프로덕션 환경 배포

#### 1. 프로덕션 빌드
```bash
./gradlew clean build -Pprofile=prod
```

#### 2. JAR 파일 실행
```bash
java -jar build/libs/tradevision-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## API 문서

### 인증 (Authentication)

모든 인증 필요 API는 `Authorization` 헤더에 JWT Access Token을 포함해야 합니다:

```
Authorization: Bearer {access_token}
```

### 응답 형식

#### 성공 응답
```json
{
  "status": 200,
  "message": "Success",
  "data": { ... }
}
```

#### 오류 응답
```json
{
  "status": 400,
  "message": "잘못된 요청입니다",
  "error": "INVALID_REQUEST"
}
```

### 주요 API 엔드포인트

#### 사용자 인증

**회원가입**
```
POST /api/auth/signup
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securePassword123!",
  "nickname": "투자초보",
  "investmentLevel": "BEGINNER"
}
```

**로그인**
```
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securePassword123!"
}

Response:
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "tokenType": "Bearer"
}
```

**토큰 갱신**
```
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGc..."
}
```

#### 차트 분석

**차트 이미지 분석**
```
POST /api/chart-analysis
Authorization: Bearer {access_token}
Content-Type: multipart/form-data

chartImage: (binary)
stockCode: 005930
```

**분석 히스토리 조회**
```
GET /api/chart-analysis/history?page=0&size=10
Authorization: Bearer {access_token}
```

#### 학습 콘텐츠

**모든 모듈 조회**
```
GET /api/learning/modules?page=0&size=10
```

**난이도별 모듈 조회**
```
GET /api/learning/modules/difficulty/BEGINNER?page=0&size=10
```

**모듈별 콘텐츠 조회**
```
GET /api/learning/modules/{moduleId}/contents?page=0&size=10
```

**콘텐츠 상세 조회 (조회수 증가)**
```
GET /api/learning/contents/{contentId}
Authorization: Bearer {access_token} (선택)
```

**학습 진행도 업데이트**
```
POST /api/learning/contents/{contentId}/progress
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "progressPercentage": 75,
  "timeSpentSeconds": 120,
  "quizScore": 85,
  "userNotes": "이동평균선의 골든크로스 개념을 이해했습니다."
}
```

**학습 통계 조회**
```
GET /api/learning/progress/stats
Authorization: Bearer {access_token}

Response:
{
  "completedContentCount": 15,
  "totalTimeSpentSeconds": 7200,
  "averageProgress": 68.5,
  "averageQuizScore": 85.2,
  "learningLevel": "숙련자",
  "contentsToNextLevel": 15
}
```

**좋아요 토글**
```
POST /api/learning/contents/{contentId}/like
Authorization: Bearer {access_token}
```

**북마크 토글**
```
POST /api/learning/contents/{contentId}/bookmark
Authorization: Bearer {access_token}
```

#### 트레이딩 기법

**모든 기법 조회**
```
GET /api/trading-techniques?page=0&size=10
```

**카테고리별 기법 조회**
```
GET /api/trading-techniques/category/MOVING_AVERAGE?page=0&size=10
```

**인기 기법 조회**
```
GET /api/trading-techniques/popular?limit=5
```

**키워드 검색**
```
GET /api/trading-techniques/search?keyword=이동평균&page=0&size=10
```

**기법 북마크 토글**
```
POST /api/trading-techniques/{techniqueId}/bookmark
Authorization: Bearer {access_token}
```

## 테스트

### 테스트 커버리지

**전체 테스트 통계:**
- **총 테스트 수:** 130+ tests
- **성공률:** 100%

**Phase별 테스트 커버리지:**

1. **Phase 2: 사용자 인증 시스템**
   - 21/21 tests passing
   - AuthService, AuthController 단위 테스트
   - JWT 토큰 생성/검증 테스트

2. **Phase 5: AI 차트 분석**
   - 28/28 tests passing
   - ChartAnalysisService 테스트 (13 tests)
   - ChartAnalysisRepository 테스트 (15 tests)
   - OpenAI API 모킹 테스트

3. **Phase 6: 트레이딩 기법 학습**
   - 22/22 tests passing
   - TradingTechniqueService 테스트 (7 tests)
   - TradingTechniqueRepository 테스트 (15 tests)

4. **Phase 7: 학습 콘텐츠 시스템**
   - 59/59 tests passing
   - LearningContentService 테스트 (7 tests)
   - ContentProgressService 테스트 (13 tests)
   - ContentModuleRepository 테스트 (8 tests)
   - LearningContentRepository 테스트 (14 tests)
   - UserContentProgressRepository 테스트 (17 tests)

### 테스트 실행 방법

**전체 테스트 실행:**
```bash
./gradlew test
```

**특정 Phase 테스트만 실행:**
```bash
# Phase 2: 인증 테스트
./gradlew test --tests "com.tradevision.service.AuthServiceTest"

# Phase 5: 차트 분석 테스트
./gradlew test --tests "com.tradevision.service.ChartAnalysisServiceTest"
./gradlew test --tests "com.tradevision.repository.ChartAnalysisRepositoryTest"

# Phase 6: 트레이딩 기법 테스트
./gradlew test --tests "com.tradevision.service.TradingTechniqueServiceTest"
./gradlew test --tests "com.tradevision.repository.TradingTechniqueRepositoryTest"

# Phase 7: 학습 콘텐츠 테스트
./gradlew test --tests "com.tradevision.service.LearningContentServiceTest"
./gradlew test --tests "com.tradevision.service.ContentProgressServiceTest"
./gradlew test --tests "com.tradevision.repository.*RepositoryTest"
```

### 테스트 환경 설정

테스트는 **H2 in-memory 데이터베이스**를 사용하며, 다음과 같이 설정됩니다:

**`src/test/resources/application-test.properties`:**
```properties
spring.datasource.url=jdbc:h2:mem:testdb;MODE=MariaDB
spring.jpa.hibernate.ddl-auto=create-drop
spring.flyway.enabled=false
```

**JPA Auditing 활성화:**
- `TestJpaConfig.java`에서 테스트용 Auditing 설정
- `created_at`, `updated_at` 자동 설정

### 테스트 커버리지 리포트

**JaCoCo 리포트 생성:**
```bash
./gradlew test jacocoTestReport
```

**리포트 확인:**
- HTML 리포트: `build/reports/jacoco/test/html/index.html`
- XML 리포트: `build/reports/jacoco/test/jacocoTestReport.xml`

## 프로젝트 구조

```
tradevision/
├── src/
│   ├── main/
│   │   ├── java/com/tradevision/
│   │   │   ├── config/           # 설정 클래스
│   │   │   ├── constant/         # 상수 및 Enum
│   │   │   ├── controller/       # REST 컨트롤러
│   │   │   ├── dto/              # 데이터 전송 객체
│   │   │   ├── entity/           # JPA 엔티티
│   │   │   ├── exception/        # 커스텀 예외
│   │   │   ├── repository/       # JPA 리포지토리
│   │   │   ├── security/         # Spring Security 설정
│   │   │   ├── service/          # 비즈니스 로직
│   │   │   └── util/             # 유틸리티 클래스
│   │   └── resources/
│   │       ├── db/migration/     # Flyway 마이그레이션
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       └── application-prod.properties
│   └── test/
│       ├── java/com/tradevision/
│       │   ├── config/           # 테스트 설정
│       │   ├── repository/       # Repository 테스트
│       │   └── service/          # Service 테스트
│       └── resources/
│           └── application-test.properties
├── docker-compose.yml
├── build.gradle
├── .env.example
└── README.md
```

## 개발 진행 상황

### 완료된 Phase

- **Phase 1:** 프로젝트 초기 설정 및 기본 구조 구축
- **Phase 2:** 사용자 인증 시스템 (JWT) - 21 tests passing
- **Phase 5:** AI 차트 분석 기능 - 28 tests passing
- **Phase 6:** 트레이딩 기법 학습 시스템 - 22 tests passing
- **Phase 7:** 학습 콘텐츠 시스템 - 59 tests passing

### 향후 계획

- **Phase 3:** 한국투자증권 OpenAPI 연동
- **Phase 4:** 실시간 주식 데이터 조회 및 알림
- **Phase 8:** 모의 투자 시뮬레이션
- **Phase 9:** 투자 일지 및 분석
- **Phase 10:** 커뮤니티 기능

## 라이선스

이 프로젝트는 학습 목적으로 개발되었습니다.

## 문의

프로젝트 관련 문의사항이 있으시면 이슈를 등록해주세요.

---

**Generated with Claude Code**

Co-Authored-By: Claude <noreply@anthropic.com>
