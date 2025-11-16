# TradeVision Constitution
<!-- AI 기반 주식 차트 분석 학습 플랫폼 -->

## 프로젝트 미션

초보 투자자들이 차트 분석 없이 감으로 매매하는 문제를 해결하기 위한 학습 플랫폼입니다.
5일선 터치, 피보나치 되돌림 등 기술적 분석의 중요성을 교육하고,
주요 지지선/저항선에서 발생하는 자동 매매 패턴을 초보자도 활용할 수 있도록 돕습니다.

## Core Principles

### I. Clean Code & Korean Documentation
- Clean Code 원칙을 철저히 준수
- **한국어 주석 필수**: 초보자도 코드를 이해할 수 있도록 작성
- 변수명, 클래스명은 영어로 작성하되 명확하고 의미있는 이름 사용
- 함수는 단일 책임 원칙(SRP)을 따르며, 한 함수당 한 가지 역할만 수행
- 매직 넘버 금지: 상수는 명명된 상수로 정의

### II. REST API Design Excellence
- RESTful 설계 원칙 엄격히 준수
- 패키지 구조: `controller → service → repository` 계층 분리
- Controller: HTTP 요청/응답 처리, 데이터 검증
- Service: 비즈니스 로직 구현
- Repository: 데이터 접근 계층
- DTO를 활용한 계층 간 데이터 전달
- 일관된 응답 형식 (성공/에러 응답 표준화)

### III. Test-Driven Development (NON-NEGOTIABLE)
- **단위 테스트 필수**: JUnit 5 기반
- API 통합 테스트 작성 필수
- **테스트 커버리지 70% 이상 목표**
- 테스트 작성 → 실패 확인 → 구현 → 통과 사이클 준수
- 테스트 코드도 한국어 주석으로 의도 명확히 표현
- Mock 객체를 활용한 독립적인 테스트 환경 구성

### IV. Performance Standards
- **API 응답 시간 1초 이하** (95th percentile 기준)
- **OpenAI Vision API 분석 5초 이하**
- 한국투자증권 OpenAPI 호출 최적화:
  - Rate limiting 준수
  - 재시도 로직 구현 (exponential backoff)
  - Circuit breaker 패턴 적용
  - 타임아웃 설정 (connection: 3초, read: 10초)
- 데이터베이스 쿼리 최적화 (N+1 문제 방지, 적절한 인덱스 사용)

### V. User-First Design
- **초보 투자자 친화적 UI/UX**
- 복잡한 차트 분석 개념을 직관적으로 전달
- 인터랙티브한 학습 경험 제공
- 한글 지원 최우선 (UTF-8mb4 인코딩 사용)
- 에러 메시지도 한국어로 사용자 친화적으로 제공
- 접근성(Accessibility) 고려

## Technology Stack

### Backend
- **Framework**: Spring Boot 3.5.7
- **Security**: Spring Security + JWT Token 기반 인증
- **Data Access**: Spring Data JPA
- **Build Tool**: Gradle
- **Java Version**: Java 17 이상

### Database
- **RDBMS**: MariaDB
- **Encoding**: UTF-8mb4 (한글 완벽 지원)
- **Connection Pool**: HikariCP
- **Migration**: Flyway 또는 Liquibase

### Frontend
- **Framework**: React (SPA 구조)
- **State Management**: Redux 또는 Context API
- **HTTP Client**: Axios
- **Charting**: Chart.js 또는 TradingView Widget

### External APIs
- **AI Analysis**: OpenAI GPT-4 Vision API
- **Stock Data**: 한국투자증권 OpenAPI
- **API Key 관리**: 환경 변수로 관리, 절대 코드에 하드코딩 금지

## Git Workflow

### Repository
- **GitHub Repository**: https://github.com/yangmw7/TradeVision
- **Main Branch**: 항상 배포 가능한 상태 유지

### Commit Strategy
- **자동 커밋 및 푸시**: 각 기능 구현 완료 시
- **커밋 메시지 형식**:
  - `feat: 새로운 기능 추가`
  - `fix: 버그 수정`
  - `docs: 문서 수정`
  - `test: 테스트 코드 추가/수정`
  - `refactor: 코드 리팩토링`
  - `style: 코드 포맷팅`
  - `chore: 빌드 설정, 패키지 매니저 설정 등`

### Branch Strategy
- **Feature Branch**: `feature/기능명` (예: `feature/user-auth`)
- **Bugfix Branch**: `fix/버그명` (예: `fix/login-error`)
- **Main Branch Protection**: 직접 푸시 금지, Pull Request 필수
- **Merge Strategy**: Squash and Merge 권장

## Security Requirements

- **인증/인가**: JWT Token 기반, Refresh Token 구현
- **비밀번호**: BCrypt 해싱 (strength: 10 이상)
- **SQL Injection 방지**: Prepared Statement 사용
- **XSS 방지**: 사용자 입력 sanitization
- **CORS**: 명시적 허용 도메인 설정
- **API Rate Limiting**: 사용자별, IP별 요청 제한
- **민감 정보 로깅 금지**: 비밀번호, API Key 등

## Code Review Standards

- **모든 PR은 코드 리뷰 필수**
- **리뷰 체크리스트**:
  - Constitution 준수 여부
  - 테스트 코드 작성 및 통과 여부
  - 한국어 주석 작성 여부
  - 성능 기준 충족 여부
  - 보안 취약점 검토
  - Clean Code 원칙 준수 여부

## Error Handling

- **일관된 에러 응답 형식**:
```json
{
  "success": false,
  "errorCode": "ERROR_CODE",
  "message": "사용자 친화적 한국어 에러 메시지",
  "details": "개발자용 상세 정보 (production에서는 제외)"
}
```
- **Global Exception Handler** 구현
- **외부 API 에러 핸들링**: Fallback 전략 구현
- **로깅**: SLF4J + Logback, 에러 레벨별 분류

## Governance

이 Constitution은 TradeVision 프로젝트의 모든 개발 활동에 우선합니다.
모든 코드, 설계, 의사결정은 이 원칙들을 따라야 합니다.

- **수정 절차**: 팀 논의 → 문서화 → 승인 → 마이그레이션 계획
- **준수 검증**: 모든 PR/리뷰에서 Constitution 준수 여부 확인
- **복잡성 정당화**: 복잡한 설계/구현은 명확한 근거 필요
- **지속적 개선**: Constitution도 프로젝트와 함께 진화

**Version**: 1.0.0 | **Ratified**: 2025-11-16 | **Last Amended**: 2025-11-16
