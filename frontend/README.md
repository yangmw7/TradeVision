# TradeVision Frontend

AI 기반 차트 분석 플랫폼의 React 프론트엔드 애플리케이션

## 기술 스택

- **React 18** - UI 라이브러리
- **TypeScript** - 타입 안전성
- **Vite** - 빠른 빌드 도구
- **React Router v6** - 라우팅
- **Axios** - HTTP 클라이언트
- **Tailwind CSS** - 스타일링
- **React Hot Toast** - 알림
- **Lucide React** - 아이콘

## 디자인 철학

Toss(토스) 스타일의 깔끔하고 현대적인 디자인:
- 화이트 배경과 미묘한 그레이 액센트
- 명확한 타이포그래피 (Pretendard 폰트)
- 부드러운 애니메이션과 전환효과
- 카드 기반 레이아웃
- 메인 컬러: 토스 블루 (#3182F6)
- 모바일 우선 반응형 디자인

## 시작하기

### 필수 요구사항

- Node.js 16.x 이상
- npm 또는 yarn

### 설치

1. 의존성 설치:
```bash
npm install
```

2. 환경 변수 설정:
```bash
cp .env.example .env
```

`.env` 파일에서 API URL을 설정하세요:
```
VITE_API_BASE_URL=http://localhost:8080
```

### 개발 서버 실행

```bash
npm run dev
```

애플리케이션이 http://localhost:5173 에서 실행됩니다.

### 빌드

프로덕션 빌드:
```bash
npm run build
```

## 주요 기능

### 1. 인증
- 로그인 / 회원가입
- JWT 토큰 기반 인증
- Protected Routes로 페이지 보호

### 2. 대시보드
- 사용자 학습 통계
- 최근 차트 분석 내역
- 빠른 액션 버튼

### 3. 차트 분석
- 드래그 앤 드롭 이미지 업로드
- AI 기반 차트 분석
- 분석 내역 관리

### 4. 매매 기법
- 카테고리별/난이도별 필터링
- 검색 기능
- 북마크 기능

### 5. 학습 콘텐츠
- 모듈 기반 학습 구조
- 진행률 추적
- 콘텐츠 좋아요/북마크

### 6. 프로필
- 사용자 정보
- 학습 통계
- 달성 배지

## 라우팅

| 경로 | 컴포넌트 | 설명 |
|------|---------|------|
| `/login` | LoginPage | 로그인 |
| `/signup` | SignupPage | 회원가입 |
| `/` | DashboardPage | 대시보드 |
| `/analysis` | AnalysisPage | 차트 분석 |
| `/techniques` | TechniquesPage | 매매 기법 |
| `/learning` | LearningModulesPage | 학습 모듈 |
| `/learning/modules/:id` | ModuleContentsPage | 모듈 콘텐츠 |
| `/learning/contents/:id` | ContentViewerPage | 콘텐츠 뷰어 |
| `/profile` | ProfilePage | 프로필 |

## 환경 변수

| 변수 | 설명 | 기본값 |
|------|------|--------|
| `VITE_API_BASE_URL` | 백엔드 API URL | `http://localhost:8080` |
