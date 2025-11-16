# Tasks: TradeVision AI 기반 주식 차트 분석 학습 플랫폼

**Input**: Design documents from `.specify/features/tradevision-platform/`
**Prerequisites**: plan.md, spec.md (user stories with priorities P1, P2, P3)

**Tests**: TDD is NON-NEGOTIABLE per Constitution - all test tasks are REQUIRED before implementation

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- **Backend**: `tradevision/src/main/java/com/tradevision/`
- **Tests**: `tradevision/src/test/java/com/tradevision/`
- **Resources**: `tradevision/src/main/resources/`
- **Database**: `tradevision/src/main/resources/db/migration/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 Update build.gradle with all required dependencies (JWT, WebFlux, Resilience4j, Flyway, Thumbnailator, Springdoc OpenAPI)
- [ ] T002 [P] Create application.yml with database connection, JPA settings, logging, file upload limits in tradevision/src/main/resources/
- [ ] T003 [P] Create application-dev.yml for development environment configuration in tradevision/src/main/resources/
- [ ] T004 [P] Create application-test.yml for test environment configuration in tradevision/src/test/resources/
- [ ] T005 [P] Create Flyway migration V1__create_users_table.sql in tradevision/src/main/resources/db/migration/
- [ ] T006 [P] Create Flyway migration V2__create_chart_analyses_table.sql in tradevision/src/main/resources/db/migration/
- [ ] T007 [P] Create Flyway migration V3__create_learning_progress_table.sql in tradevision/src/main/resources/db/migration/
- [ ] T008 [P] Create Flyway migration V4__create_trading_techniques_table.sql in tradevision/src/main/resources/db/migration/
- [ ] T009 [P] Create Flyway migration V5__create_learning_contents_table.sql in tradevision/src/main/resources/db/migration/
- [ ] T010 [P] Create Flyway migration V6__create_refresh_tokens_table.sql in tradevision/src/main/resources/db/migration/
- [ ] T011 Create Docker Compose configuration for MariaDB in docker/docker-compose.yml with UTF-8mb4 설정

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T012 [P] Create CandleType enum (M1, M5, M15, H1, D, W, M) in tradevision/src/main/java/com/tradevision/constant/CandleType.java
- [ ] T013 [P] Create InvestmentLevel enum in tradevision/src/main/java/com/tradevision/constant/InvestmentLevel.java
- [ ] T014 [P] Create FeedbackType enum in tradevision/src/main/java/com/tradevision/constant/FeedbackType.java
- [ ] T015 [P] Create TechniqueCategory enum in tradevision/src/main/java/com/tradevision/constant/TechniqueCategory.java
- [ ] T016 [P] Create ErrorCode enum with all error codes in tradevision/src/main/java/com/tradevision/exception/ErrorCode.java
- [ ] T017 [P] Create BusinessException class in tradevision/src/main/java/com/tradevision/exception/BusinessException.java
- [ ] T018 [P] Create UnauthorizedException class in tradevision/src/main/java/com/tradevision/exception/UnauthorizedException.java
- [ ] T019 [P] Create ResourceNotFoundException class in tradevision/src/main/java/com/tradevision/exception/ResourceNotFoundException.java
- [ ] T020 [P] Create ExternalApiException class in tradevision/src/main/java/com/tradevision/exception/ExternalApiException.java
- [ ] T021 Create GlobalExceptionHandler with @RestControllerAdvice, 한국어 에러 메시지 in tradevision/src/main/java/com/tradevision/exception/GlobalExceptionHandler.java
- [ ] T022 [P] Create ApiResponse<T> generic wrapper class in tradevision/src/main/java/com/tradevision/dto/response/ApiResponse.java
- [ ] T023 [P] Create BaseTimeEntity with @MappedSuperclass, createdAt, updatedAt in tradevision/src/main/java/com/tradevision/entity/BaseTimeEntity.java
- [ ] T024 Create JpaConfig with @EnableJpaAuditing in tradevision/src/main/java/com/tradevision/config/JpaConfig.java
- [ ] T025 Create AsyncConfig with @EnableAsync, ThreadPoolTaskExecutor in tradevision/src/main/java/com/tradevision/config/AsyncConfig.java
- [ ] T026 Create Resilience4jConfig with Circuit Breaker, Retry policies in tradevision/src/main/java/com/tradevision/config/Resilience4jConfig.java
- [ ] T027 Create CorsConfig with 프론트엔드 도메인 허용 in tradevision/src/main/java/com/tradevision/config/CorsConfig.java
- [ ] T028 Create WebConfig for Web MVC settings in tradevision/src/main/java/com/tradevision/config/WebConfig.java

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - 회원가입 및 인증 시스템 (Priority: P1) 🎯 MVP

**Goal**: JWT 기반 사용자 인증 시스템 구현 - 회원가입, 로그인, 토큰 갱신

**Independent Test**: 회원가입 API 호출 → 계정 생성 확인 → 로그인 API 호출 → JWT 토큰 발급 확인 → 보호된 API 호출 시 토큰 검증 동작 확인

### Tests for User Story 1 (TDD - REQUIRED) ⚠️

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [ ] T029 [P] [US1] Unit test for JwtTokenProvider (token generation, validation, email extraction) in tradevision/src/test/java/com/tradevision/security/JwtTokenProviderTest.java
- [ ] T030 [P] [US1] Unit test for CustomUserDetailsService (loadUserByUsername) in tradevision/src/test/java/com/tradevision/security/CustomUserDetailsServiceTest.java
- [ ] T031 [P] [US1] Unit test for AuthService (signup, login, refresh, duplicate email check, password validation) in tradevision/src/test/java/com/tradevision/service/AuthServiceTest.java
- [ ] T032 [P] [US1] Integration test for UserRepository (save, findByEmail, existsByEmail) in tradevision/src/test/java/com/tradevision/repository/UserRepositoryTest.java
- [ ] T033 [P] [US1] Integration test for RefreshTokenRepository in tradevision/src/test/java/com/tradevision/repository/RefreshTokenRepositoryTest.java
- [ ] T034 [P] [US1] API integration test for POST /api/auth/signup in tradevision/src/test/java/com/tradevision/controller/AuthControllerTest.java
- [ ] T035 [P] [US1] API integration test for POST /api/auth/login in tradevision/src/test/java/com/tradevision/controller/AuthControllerTest.java
- [ ] T036 [P] [US1] API integration test for POST /api/auth/refresh in tradevision/src/test/java/com/tradevision/controller/AuthControllerTest.java
- [ ] T037 [P] [US1] E2E integration test for 회원가입 → 로그인 → 보호된 API 호출 플로우 in tradevision/src/test/java/com/tradevision/integration/AuthIntegrationTest.java

### Implementation for User Story 1

- [ ] T038 [P] [US1] Create User entity with @Entity, BCrypt password field in tradevision/src/main/java/com/tradevision/entity/User.java
- [ ] T039 [P] [US1] Create RefreshToken entity in tradevision/src/main/java/com/tradevision/entity/RefreshToken.java
- [ ] T040 [P] [US1] Create UserRepository interface extending JpaRepository in tradevision/src/main/java/com/tradevision/repository/UserRepository.java
- [ ] T041 [P] [US1] Create RefreshTokenRepository interface in tradevision/src/main/java/com/tradevision/repository/RefreshTokenRepository.java
- [ ] T042 [P] [US1] Create SignupRequest DTO with validation annotations in tradevision/src/main/java/com/tradevision/dto/request/SignupRequest.java
- [ ] T043 [P] [US1] Create LoginRequest DTO in tradevision/src/main/java/com/tradevision/dto/request/LoginRequest.java
- [ ] T044 [P] [US1] Create RefreshTokenRequest DTO in tradevision/src/main/java/com/tradevision/dto/request/RefreshTokenRequest.java
- [ ] T045 [P] [US1] Create AuthResponse DTO (accessToken, refreshToken, user info) in tradevision/src/main/java/com/tradevision/dto/response/AuthResponse.java
- [ ] T046 [P] [US1] Create JwtTokenProvider class (generateAccessToken, generateRefreshToken, validateToken, getEmailFromToken) in tradevision/src/main/java/com/tradevision/security/JwtTokenProvider.java
- [ ] T047 [P] [US1] Create CustomUserDetailsService implementing UserDetailsService in tradevision/src/main/java/com/tradevision/security/CustomUserDetailsService.java
- [ ] T048 [P] [US1] Create JwtAuthenticationFilter extending OncePerRequestFilter in tradevision/src/main/java/com/tradevision/security/JwtAuthenticationFilter.java
- [ ] T049 [P] [US1] Create JwtAuthenticationEntryPoint for 401 errors in tradevision/src/main/java/com/tradevision/security/JwtAuthenticationEntryPoint.java
- [ ] T050 [P] [US1] Create JwtAccessDeniedHandler for 403 errors in tradevision/src/main/java/com/tradevision/security/JwtAccessDeniedHandler.java
- [ ] T051 [US1] Implement AuthService with signup (BCrypt 암호화), login (토큰 발급), refresh in tradevision/src/main/java/com/tradevision/service/AuthService.java (depends on T038-T041, T046-T047)
- [ ] T052 [US1] Implement AuthController with POST /api/auth/signup, /api/auth/login, /api/auth/refresh in tradevision/src/main/java/com/tradevision/controller/AuthController.java (depends on T051)
- [ ] T053 [US1] Create SecurityConfig with JWT filter registration, CSRF disabled, CORS enabled in tradevision/src/main/java/com/tradevision/config/SecurityConfig.java (depends on T048-T050)
- [ ] T054 [US1] Run all US1 tests to verify implementation passes - ensure 70%+ coverage for US1 components

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - 실시간 주식 데이터 조회 (Priority: P1)

**Goal**: 한국투자증권 OpenAPI 연동하여 실시간 주식 데이터 및 7가지 시간봉 차트 데이터 조회

**Independent Test**: 로그인 → 종목 코드 "005930" 입력 → "일봉" 선택 → 120일 차트 데이터 조회 확인 → "1분봉"으로 변경 → 60개 1분봉 데이터 조회 확인

### Tests for User Story 2 (TDD - REQUIRED) ⚠️

- [ ] T055 [P] [US2] Unit test for KisApiClient (API 호출, Retry, Circuit Breaker, Timeout) in tradevision/src/test/java/com/tradevision/external/kis/KisApiClientTest.java
- [ ] T056 [P] [US2] Unit test for KisAuthService (API 인증 토큰 발급 및 갱신) in tradevision/src/test/java/com/tradevision/external/kis/KisAuthServiceTest.java
- [ ] T057 [P] [US2] Unit test for StockService (getStockPrice, getChartData, caching) in tradevision/src/test/java/com/tradevision/service/StockServiceTest.java
- [ ] T058 [P] [US2] API integration test for GET /api/stocks/{code} in tradevision/src/test/java/com/tradevision/controller/StockControllerTest.java
- [ ] T059 [P] [US2] API integration test for GET /api/stocks/{code}/chart?candle={candleType} in tradevision/src/test/java/com/tradevision/controller/StockControllerTest.java
- [ ] T060 [P] [US2] E2E integration test for 주식 조회 플로우 (login → stock query → chart data) in tradevision/src/test/java/com/tradevision/integration/StockIntegrationTest.java

### Implementation for User Story 2

- [ ] T061 [P] [US2] Create KisTokenResponse DTO in tradevision/src/main/java/com/tradevision/external/kis/dto/KisTokenResponse.java
- [ ] T062 [P] [US2] Create KisStockPriceResponse DTO in tradevision/src/main/java/com/tradevision/external/kis/dto/KisStockPriceResponse.java
- [ ] T063 [P] [US2] Create KisChartDataResponse DTO in tradevision/src/main/java/com/tradevision/external/kis/dto/KisChartDataResponse.java
- [ ] T064 [P] [US2] Create StockDataResponse DTO (internal) in tradevision/src/main/java/com/tradevision/dto/response/StockDataResponse.java
- [ ] T065 [P] [US2] Create ChartDataResponse DTO (internal) in tradevision/src/main/java/com/tradevision/dto/response/ChartDataResponse.java
- [ ] T066 [P] [US2] Implement KisAuthService for API 인증 토큰 발급/갱신 in tradevision/src/main/java/com/tradevision/external/kis/KisAuthService.java
- [ ] T067 [US2] Implement KisApiClient with WebClient, @Retry, @CircuitBreaker, timeout settings in tradevision/src/main/java/com/tradevision/external/kis/KisApiClient.java (depends on T066)
- [ ] T068 [US2] Implement StockService with getStockPrice, getChartData, @Cacheable (1분 TTL) in tradevision/src/main/java/com/tradevision/service/StockService.java (depends on T067)
- [ ] T069 [US2] Implement StockController with GET /api/stocks/{code}, GET /api/stocks/{code}/chart in tradevision/src/main/java/com/tradevision/controller/StockController.java (depends on T068)
- [ ] T070 [US2] Run all US2 tests to verify implementation passes

**Checkpoint**: User Story 2 is independently functional - can query stock data without needing other stories

---

## Phase 5: User Story 4 - 차트 이미지 업로드 및 AI 분석 (Priority: P1)

**Goal**: OpenAI GPT-4 Vision을 활용한 차트 이미지 분석 및 결과 저장

**Independent Test**: 로그인 → PNG 차트 이미지 업로드 → 5초 내 AI 분석 결과 수신 → 분석 저장 → 히스토리 조회로 저장 확인

### Tests for User Story 4 (TDD - REQUIRED) ⚠️

- [ ] T071 [P] [US4] Unit test for ImageService (saveImage, optimizeImage, file validation) in tradevision/src/test/java/com/tradevision/service/ImageServiceTest.java
- [ ] T072 [P] [US4] Unit test for ImageProcessor utility (resize, compress) in tradevision/src/test/java/com/tradevision/util/ImageProcessorTest.java
- [ ] T073 [P] [US4] Unit test for FileUploadUtil (file type validation, size check) in tradevision/src/test/java/com/tradevision/util/FileUploadUtilTest.java
- [ ] T074 [P] [US4] Unit test for OpenAIClient (analyzeChart, @Async, timeout) in tradevision/src/test/java/com/tradevision/external/openai/OpenAIClientTest.java
- [ ] T075 [P] [US4] Unit test for ChartAnalysisService (analyzeChart, save result) in tradevision/src/test/java/com/tradevision/service/ChartAnalysisServiceTest.java
- [ ] T076 [P] [US4] Integration test for ChartAnalysisRepository in tradevision/src/test/java/com/tradevision/repository/ChartAnalysisRepositoryTest.java
- [ ] T077 [P] [US4] API integration test for POST /api/charts/upload in tradevision/src/test/java/com/tradevision/controller/ChartControllerTest.java
- [ ] T078 [P] [US4] API integration test for POST /api/charts/analyze in tradevision/src/test/java/com/tradevision/controller/ChartControllerTest.java
- [ ] T079 [P] [US4] E2E integration test for 이미지 업로드 → AI 분석 → 저장 → 조회 플로우 in tradevision/src/test/java/com/tradevision/integration/ChartAnalysisIntegrationTest.java

### Implementation for User Story 4

- [ ] T080 [P] [US4] Create ChartAnalysis entity with JSON field for analysis result in tradevision/src/main/java/com/tradevision/entity/ChartAnalysis.java
- [ ] T081 [P] [US4] Create ChartAnalysisRepository interface in tradevision/src/main/java/com/tradevision/repository/ChartAnalysisRepository.java
- [ ] T082 [P] [US4] Create ChartAnalysisRequest DTO (multipart file) in tradevision/src/main/java/com/tradevision/dto/request/ChartAnalysisRequest.java
- [ ] T083 [P] [US4] Create ChartAnalysisResponse DTO in tradevision/src/main/java/com/tradevision/dto/response/ChartAnalysisResponse.java
- [ ] T084 [P] [US4] Create VisionRequest DTO (OpenAI API) in tradevision/src/main/java/com/tradevision/external/openai/dto/VisionRequest.java
- [ ] T085 [P] [US4] Create VisionResponse DTO (OpenAI API) in tradevision/src/main/java/com/tradevision/external/openai/dto/VisionResponse.java
- [ ] T086 [P] [US4] Implement ImageProcessor utility for resize/compress with Thumbnailator in tradevision/src/main/java/com/tradevision/util/ImageProcessor.java
- [ ] T087 [P] [US4] Implement FileUploadUtil for file validation in tradevision/src/main/java/com/tradevision/util/FileUploadUtil.java
- [ ] T088 [US4] Implement ImageService with saveImage, optimizeImage in tradevision/src/main/java/com/tradevision/service/ImageService.java (depends on T086-T087)
- [ ] T089 [US4] Implement OpenAIClient with WebClient, @Async, GPT-4 Vision prompt engineering (한국어) in tradevision/src/main/java/com/tradevision/external/openai/OpenAIClient.java
- [ ] T090 [US4] Implement ChartAnalysisService with analyzeChart (save image → call OpenAI → parse JSON → save result) in tradevision/src/main/java/com/tradevision/service/ChartAnalysisService.java (depends on T088-T089)
- [ ] T091 [US4] Implement ChartController with POST /api/charts/upload, POST /api/charts/analyze in tradevision/src/main/java/com/tradevision/controller/ChartController.java (depends on T090)
- [ ] T092 [US4] Create sample chart image for testing in tradevision/src/test/resources/test-data/sample-chart.png
- [ ] T093 [US4] Run all US4 tests to verify implementation passes

**Checkpoint**: User Story 4 is independently functional - can upload/analyze charts without needing other stories

---

## Phase 6: User Story 3 - 봉 종류별 최적 매매기법 추천 (Priority: P2)

**Goal**: 시간봉 선택 시 최적 매매기법 자동 추천

**Independent Test**: "1분봉" 선택 → "스캘핑, 단기 RSI, 단기 이동평균선 크로스" 추천 확인 → 기법 클릭 → 상세 설명 조회 확인

### Tests for User Story 3 (TDD - REQUIRED) ⚠️

- [ ] T094 [P] [US3] Unit test for LearningService.getTradingTechniquesByCandleType in tradevision/src/test/java/com/tradevision/service/LearningServiceTest.java
- [ ] T095 [P] [US3] Integration test for TradingTechniqueRepository in tradevision/src/test/java/com/tradevision/repository/TradingTechniqueRepositoryTest.java
- [ ] T096 [P] [US3] API integration test for GET /api/charts/techniques/{candleType} in tradevision/src/test/java/com/tradevision/controller/ChartControllerTest.java

### Implementation for User Story 3

- [ ] T097 [P] [US3] Create TradingTechnique entity in tradevision/src/main/java/com/tradevision/entity/TradingTechnique.java
- [ ] T098 [P] [US3] Create LearningContent entity (basic structure) in tradevision/src/main/java/com/tradevision/entity/LearningContent.java
- [ ] T099 [P] [US3] Create TradingTechniqueRepository interface in tradevision/src/main/java/com/tradevision/repository/TradingTechniqueRepository.java
- [ ] T100 [P] [US3] Create LearningContentRepository interface in tradevision/src/main/java/com/tradevision/repository/LearningContentRepository.java
- [ ] T101 [P] [US3] Create TradingTechniqueResponse DTO in tradevision/src/main/java/com/tradevision/dto/response/TradingTechniqueResponse.java
- [ ] T102 Create Flyway migration V7__insert_initial_data.sql with 봉 종류별 매매기법 INSERT in tradevision/src/main/resources/db/migration/
- [ ] T103 [US3] Implement LearningService with getTradingTechniquesByCandleType in tradevision/src/main/java/com/tradevision/service/LearningService.java (depends on T099-T100)
- [ ] T104 [US3] Add GET /api/charts/techniques/{candleType} endpoint to ChartController (depends on T103)
- [ ] T105 [US3] Run all US3 tests to verify implementation passes

**Checkpoint**: User Story 3 is independently functional

---

## Phase 7: User Story 5 - 분석 히스토리 관리 (Priority: P2)

**Goal**: AI 차트 분석 결과 조회, 필터링, 비교, 피드백

**Independent Test**: 히스토리 페이지 접근 → 30일 분석 목록 확인 → 날짜 필터 적용 → 종목 검색 → 2개 분석 비교

### Tests for User Story 5 (TDD - REQUIRED) ⚠️

- [ ] T106 [P] [US5] Unit test for ChartAnalysisService.getAnalysisHistory with pagination in tradevision/src/test/java/com/tradevision/service/ChartAnalysisServiceTest.java
- [ ] T107 [P] [US5] Unit test for ChartAnalysisService filtering (date range, stock name) in tradevision/src/test/java/com/tradevision/service/ChartAnalysisServiceTest.java
- [ ] T108 [P] [US5] Unit test for ChartAnalysisService.compareAnalyses in tradevision/src/test/java/com/tradevision/service/ChartAnalysisServiceTest.java
- [ ] T109 [P] [US5] Unit test for ChartAnalysisService.updateFeedback in tradevision/src/test/java/com/tradevision/service/ChartAnalysisServiceTest.java
- [ ] T110 [P] [US5] API integration test for GET /api/charts/history with filters in tradevision/src/test/java/com/tradevision/controller/ChartControllerTest.java
- [ ] T111 [P] [US5] API integration test for GET /api/charts/history/{id} in tradevision/src/test/java/com/tradevision/controller/ChartControllerTest.java
- [ ] T112 [P] [US5] API integration test for PUT /api/charts/history/{id}/feedback in tradevision/src/test/java/com/tradevision/controller/ChartControllerTest.java
- [ ] T113 [P] [US5] API integration test for GET /api/charts/compare?id1=X&id2=Y in tradevision/src/test/java/com/tradevision/controller/ChartControllerTest.java
- [ ] T114 [P] [US5] API integration test for DELETE /api/charts/history/{id} in tradevision/src/test/java/com/tradevision/controller/ChartControllerTest.java

### Implementation for User Story 5

- [ ] T115 [P] [US5] Create AnalysisHistoryResponse DTO with pagination support in tradevision/src/main/java/com/tradevision/dto/response/AnalysisHistoryResponse.java
- [ ] T116 [US5] Extend ChartAnalysisService with getAnalysisHistory (pagination, filters), getAnalysisById, updateFeedback, compareAnalyses, deleteAnalysis in tradevision/src/main/java/com/tradevision/service/ChartAnalysisService.java
- [ ] T117 [US5] Add history endpoints to ChartController: GET /api/charts/history, GET /api/charts/history/{id}, PUT /api/charts/history/{id}/feedback, GET /api/charts/compare, DELETE /api/charts/history/{id} (depends on T116)
- [ ] T118 [US5] Run all US5 tests to verify implementation passes

**Checkpoint**: User Story 5 is independently functional

---

## Phase 8: User Story 6 - 차트 분석 학습 페이지 (Priority: P2)

**Goal**: 기술적 분석 기법 학습 콘텐츠 제공 및 진도 추적

**Independent Test**: 학습 페이지 접근 → 8개 카테고리 목록 확인 → "이동평균선" 선택 → 개념, 계산법, 예시 확인 → 학습 완료 체크 → 진도율 업데이트 확인

### Tests for User Story 6 (TDD - REQUIRED) ⚠️

- [ ] T119 [P] [US6] Unit test for LearningService.getAllLearningContents in tradevision/src/test/java/com/tradevision/service/LearningServiceTest.java
- [ ] T120 [P] [US6] Unit test for LearningService.getLearningContentById in tradevision/src/test/java/com/tradevision/service/LearningServiceTest.java
- [ ] T121 [P] [US6] Unit test for LearningService.getUserProgress in tradevision/src/test/java/com/tradevision/service/LearningServiceTest.java
- [ ] T122 [P] [US6] Unit test for LearningService.updateProgress in tradevision/src/test/java/com/tradevision/service/LearningServiceTest.java
- [ ] T123 [P] [US6] Integration test for LearningProgressRepository in tradevision/src/test/java/com/tradevision/repository/LearningProgressRepositoryTest.java
- [ ] T124 [P] [US6] API integration test for GET /api/learning/contents in tradevision/src/test/java/com/tradevision/controller/LearningControllerTest.java
- [ ] T125 [P] [US6] API integration test for GET /api/learning/contents/{id} in tradevision/src/test/java/com/tradevision/controller/LearningControllerTest.java
- [ ] T126 [P] [US6] API integration test for GET /api/learning/progress in tradevision/src/test/java/com/tradevision/controller/LearningControllerTest.java
- [ ] T127 [P] [US6] API integration test for PUT /api/learning/progress in tradevision/src/test/java/com/tradevision/controller/LearningControllerTest.java

### Implementation for User Story 6

- [ ] T128 [P] [US6] Create LearningProgress entity in tradevision/src/main/java/com/tradevision/entity/LearningProgress.java
- [ ] T129 [P] [US6] Create LearningProgressRepository interface in tradevision/src/main/java/com/tradevision/repository/LearningProgressRepository.java
- [ ] T130 [P] [US6] Create LearningContentResponse DTO in tradevision/src/main/java/com/tradevision/dto/response/LearningContentResponse.java
- [ ] T131 [P] [US6] Create LearningProgressResponse DTO in tradevision/src/main/java/com/tradevision/dto/response/LearningProgressResponse.java
- [ ] T132 [P] [US6] Create UpdateProgressRequest DTO in tradevision/src/main/java/com/tradevision/dto/request/UpdateProgressRequest.java
- [ ] T133 Update V7__insert_initial_data.sql with 8+ learning content categories (이동평균선, 볼린저밴드, MACD, RSI, 스토캐스틱, 피보나치 되돌림, 엘리어트 파동, 캔들 패턴) in tradevision/src/main/resources/db/migration/
- [ ] T134 [US6] Extend LearningService with getAllLearningContents, getLearningContentById, getLearningContentsByCategory, getUserProgress, updateProgress, @Cacheable in tradevision/src/main/java/com/tradevision/service/LearningService.java (depends on T129)
- [ ] T135 [US6] Implement LearningController with GET /api/learning/contents, GET /api/learning/contents/{id}, GET /api/learning/contents/category/{category}, GET /api/learning/progress, PUT /api/learning/progress in tradevision/src/main/java/com/tradevision/controller/LearningController.java (depends on T134)
- [ ] T136 [US6] Run all US6 tests to verify implementation passes

**Checkpoint**: User Story 6 is independently functional

---

## Phase 9: User Story 7 - 사용자 프로필 관리 (Priority: P3)

**Goal**: 사용자 프로필 조회/수정, 비밀번호 변경

**Independent Test**: 로그인 → 프로필 페이지 접근 → 닉네임 변경 → 저장 → 대시보드에서 새 닉네임 확인

### Tests for User Story 7 (TDD - REQUIRED) ⚠️

- [ ] T137 [P] [US7] Unit test for UserService.getProfile in tradevision/src/test/java/com/tradevision/service/UserServiceTest.java
- [ ] T138 [P] [US7] Unit test for UserService.updateProfile in tradevision/src/test/java/com/tradevision/service/UserServiceTest.java
- [ ] T139 [P] [US7] Unit test for UserService.changePassword (현재 비밀번호 확인) in tradevision/src/test/java/com/tradevision/service/UserServiceTest.java
- [ ] T140 [P] [US7] API integration test for GET /api/users/profile in tradevision/src/test/java/com/tradevision/controller/UserControllerTest.java
- [ ] T141 [P] [US7] API integration test for PUT /api/users/profile in tradevision/src/test/java/com/tradevision/controller/UserControllerTest.java
- [ ] T142 [P] [US7] API integration test for PUT /api/users/password in tradevision/src/test/java/com/tradevision/controller/UserControllerTest.java
- [ ] T143 [P] [US7] API integration test for DELETE /api/users (회원 탈퇴) in tradevision/src/test/java/com/tradevision/controller/UserControllerTest.java

### Implementation for User Story 7

- [ ] T144 [P] [US7] Create UserProfileResponse DTO in tradevision/src/main/java/com/tradevision/dto/response/UserProfileResponse.java
- [ ] T145 [P] [US7] Create UpdateProfileRequest DTO in tradevision/src/main/java/com/tradevision/dto/request/UpdateProfileRequest.java
- [ ] T146 [P] [US7] Create ChangePasswordRequest DTO in tradevision/src/main/java/com/tradevision/dto/request/ChangePasswordRequest.java
- [ ] T147 [US7] Implement UserService with getProfile, updateProfile, changePassword in tradevision/src/main/java/com/tradevision/service/UserService.java
- [ ] T148 [US7] Implement UserController with GET /api/users/profile, PUT /api/users/profile, PUT /api/users/password, DELETE /api/users in tradevision/src/main/java/com/tradevision/controller/UserController.java (depends on T147)
- [ ] T149 [US7] Run all US7 tests to verify implementation passes

**Checkpoint**: All user stories are now independently functional

---

## Phase 10: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T150 [P] Add @Operation, @ApiResponse Swagger annotations to all controllers for API documentation
- [ ] T151 [P] Add comprehensive 한국어 주석 to all service classes explaining business logic
- [ ] T152 [P] Add 한국어 주석 to all entity classes explaining fields and relationships
- [ ] T153 [P] Review and add 한국어 Javadoc to all public methods in controllers, services, repositories
- [ ] T154 Create logback-spring.xml with structured logging, error level separation in tradevision/src/main/resources/
- [ ] T155 Add database indexes for performance: user_id, stock_code, created_at on chart_analyses, user_id on learning_progress
- [ ] T156 [P] Code review: Verify all magic numbers replaced with named constants
- [ ] T157 [P] Code review: Verify all classes follow SRP (Single Responsibility Principle)
- [ ] T158 [P] Security audit: Verify no sensitive data (passwords, API keys) in logs
- [ ] T159 [P] Security audit: Verify CORS settings are restrictive
- [ ] T160 Run JaCoCo test coverage report - ensure 70%+ coverage achieved
- [ ] T161 Create README.md with project overview, features, tech stack in repository root
- [ ] T162 [P] Create docs/SETUP.md with development environment setup guide
- [ ] T163 [P] Create docs/ARCHITECTURE.md with architecture explanation and diagrams
- [ ] T164 [P] Create docs/API.md complementing Swagger documentation
- [ ] T165 Create Dockerfile for Spring Boot application in docker/Dockerfile
- [ ] T166 Update docker-compose.yml to include both MariaDB and backend services
- [ ] T167 Create .env.example with required environment variables (JWT_SECRET, OPENAI_API_KEY, KIS_API_KEY)
- [ ] T168 Test full application startup with docker-compose up
- [ ] T169 Verify Swagger UI accessible at http://localhost:8080/swagger-ui.html
- [ ] T170 Perform manual API testing for all critical user flows
- [ ] T171 Git commit with message: "feat: TradeVision 플랫폼 완성 - 모든 기능 구현 및 테스트 완료"

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion (T001-T011) - BLOCKS all user stories
- **User Story 1 (Phase 3)**: Depends on Foundational (T012-T028)
- **User Story 2 (Phase 4)**: Depends on Foundational (T012-T028) + User Story 1 (for authentication)
- **User Story 4 (Phase 5)**: Depends on Foundational (T012-T028) + User Story 1 (for authentication)
- **User Story 3 (Phase 6)**: Depends on Foundational (T012-T028)
- **User Story 5 (Phase 7)**: Depends on User Story 4 (T080-T093) - needs ChartAnalysis entity
- **User Story 6 (Phase 8)**: Depends on Foundational (T012-T028) + User Story 3 (for TradingTechnique/LearningContent entities)
- **User Story 7 (Phase 9)**: Depends on User Story 1 (T038-T054) - needs User entity
- **Polish (Phase 10)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: No dependencies on other stories after Foundational
- **User Story 2 (P1)**: Requires US1 for JWT authentication
- **User Story 4 (P1)**: Requires US1 for authentication
- **User Story 3 (P2)**: No dependencies on other stories (can start after Foundational)
- **User Story 5 (P2)**: Requires US4 (ChartAnalysis entity)
- **User Story 6 (P2)**: Requires US3 (TradingTechnique/LearningContent entities)
- **User Story 7 (P3)**: Requires US1 (User entity)

### Within Each User Story (TDD Cycle)

1. **Tests FIRST** (marked [P] can run in parallel) - ALL MUST FAIL initially
2. **Models** (entities, repositories) - marked [P] can run in parallel
3. **DTOs** - marked [P] can run in parallel
4. **Services** - depend on models/DTOs
5. **Controllers** - depend on services
6. **Run all tests** - ALL MUST PASS before moving to next story

### Parallel Opportunities

- **Phase 1 (Setup)**: T002-T010 can all run in parallel (different files)
- **Phase 2 (Foundational)**: T012-T028 can mostly run in parallel except T021, T024-T028 which depend on earlier tasks
- **User Story 1 Tests**: T029-T037 can all run in parallel (write all failing tests first)
- **User Story 1 Entities/Repos**: T038-T041 can run in parallel
- **User Story 1 DTOs**: T042-T045 can run in parallel
- **User Story 1 Security**: T046-T050 can run in parallel
- **User Story 2 Tests**: T055-T060 can run in parallel
- **User Story 2 DTOs**: T061-T065 can run in parallel
- **User Story 4 Tests**: T071-T079 can run in parallel
- **User Story 4 Entities/DTOs**: T080-T087 can run in parallel
- **Polish Phase**: T150-T153, T156-T159, T162-T164 can run in parallel

**After Foundational Phase completes**: User Stories 1, 2, 3, 4 can start in parallel (if team capacity allows) since they have minimal cross-dependencies

---

## Parallel Example: User Story 1 (TDD Workflow)

```bash
# Step 1: Launch all tests for User Story 1 together (MUST FAIL):
Task T029: "Unit test for JwtTokenProvider"
Task T030: "Unit test for CustomUserDetailsService"
Task T031: "Unit test for AuthService"
Task T032: "Integration test for UserRepository"
Task T033: "Integration test for RefreshTokenRepository"
Task T034: "API test for POST /api/auth/signup"
Task T035: "API test for POST /api/auth/login"
Task T036: "API test for POST /api/auth/refresh"
Task T037: "E2E test for auth flow"

# Verify ALL tests FAIL ✅

# Step 2: Launch all models/repos for User Story 1 together:
Task T038: "Create User entity"
Task T039: "Create RefreshToken entity"
Task T040: "Create UserRepository"
Task T041: "Create RefreshTokenRepository"

# Step 3: Launch all DTOs together:
Task T042: "Create SignupRequest DTO"
Task T043: "Create LoginRequest DTO"
Task T044: "Create RefreshTokenRequest DTO"
Task T045: "Create AuthResponse DTO"

# Step 4: Launch all security components together:
Task T046: "Create JwtTokenProvider"
Task T047: "Create CustomUserDetailsService"
Task T048: "Create JwtAuthenticationFilter"
Task T049: "Create JwtAuthenticationEntryPoint"
Task T050: "Create JwtAccessDeniedHandler"

# Step 5: Sequential implementation:
Task T051: "Implement AuthService" (depends on models, security)
Task T052: "Implement AuthController" (depends on service)
Task T053: "Create SecurityConfig" (depends on security components)

# Step 6: Run ALL tests again - MUST PASS ✅
Task T054: "Run all US1 tests"
```

---

## Implementation Strategy

### MVP First (P1 User Stories Only)

1. Complete Phase 1: Setup (T001-T011)
2. Complete Phase 2: Foundational (T012-T028) - CRITICAL
3. Complete Phase 3: User Story 1 - 인증 시스템 (T029-T054)
4. Complete Phase 4: User Story 2 - 주식 데이터 조회 (T055-T070)
5. Complete Phase 5: User Story 4 - AI 차트 분석 (T071-T093)
6. **STOP and VALIDATE**: Test all P1 stories independently
7. Deploy/demo MVP

**MVP includes**:
- User registration, login, JWT authentication
- Real-time stock data query (7 candle types)
- AI-powered chart image analysis
- Analysis results storage

### Full Product (All User Stories)

1. MVP (Phases 1-5) → Validate
2. Add Phase 6: User Story 3 - 매매기법 추천 (T094-T105)
3. Add Phase 7: User Story 5 - 히스토리 관리 (T106-T118)
4. Add Phase 8: User Story 6 - 학습 페이지 (T119-T136)
5. Add Phase 9: User Story 7 - 프로필 관리 (T137-T149)
6. Complete Phase 10: Polish (T150-T171)
7. Final validation and deployment

### Parallel Team Strategy

With 3 developers after Foundational phase:

- **Developer A**: User Story 1 (Auth) → User Story 7 (Profile)
- **Developer B**: User Story 2 (Stock Data) → User Story 5 (History)
- **Developer C**: User Story 4 (AI Analysis) → User Story 3 (Recommendations) → User Story 6 (Learning)

All converge at Phase 10 for Polish.

---

## TDD Compliance Checklist (Constitution Requirement)

✅ **Every user story has test tasks BEFORE implementation tasks**
✅ **Test tasks explicitly state "ensure they FAIL before implementation"**
✅ **Test coverage goal: 70%+ per Constitution**
✅ **Test types included**: Unit tests, Integration tests, E2E tests
✅ **Test-first workflow enforced**: Tests → Verify failure → Implement → Verify pass

---

## Notes

- **[P] tasks** = different files, no dependencies, can run in parallel
- **[Story] label** maps task to specific user story for traceability
- **TDD is NON-NEGOTIABLE**: All tests MUST be written before implementation per Constitution
- Each user story is independently completable and testable
- Commit after each logical group of tasks or at checkpoints
- Stop at any checkpoint to validate story independently
- All code MUST have 한국어 주석 per Constitution
- All API responses MUST use ApiResponse<T> wrapper
- All errors MUST have 한국어 messages

---

**Total Tasks**: 171
**Test Tasks**: 71 (TDD required)
**Implementation Tasks**: 100
**Estimated Timeline**: ~33 days per plan.md
**MVP Scope**: Phases 1-5 (T001-T093) = User Stories 1, 2, 4
**Test Coverage Goal**: 70%+ (Constitution requirement)