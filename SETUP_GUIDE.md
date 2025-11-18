# TradeVision Platform - Complete Setup Guide

## Project Overview

TradeVision is a comprehensive AI-powered stock chart analysis and learning platform designed for Korean beginner investors. The platform features:

- **Frontend**: React 19 + TypeScript + Tailwind CSS (Professional Dark Theme)
- **Backend**: Spring Boot 3.x + MariaDB
- **AI Integration**: OpenAI GPT-4 Vision for chart analysis
- **External APIs**: Korea Investment Securities API

## ✨ New Features Implemented

### 1. Freemium Subscription System
- **FREE Tier**: 5 chart analyses (guest users)
- **PRO Tier**: ₩15,000/month - Unlimited analyses
- **PLATINUM Tier**: ₩50,000/month - Premium features + mentoring
- Usage tracking for guest and authenticated users
- Payment infrastructure ready (Stripe/Toss Payments)

### 2. Professional Dark Theme UI
- Modern dark trading platform aesthetic
- Electric blue (#00d4ff) accents for bullish indicators
- Glassmorphism effects and gradient animations
- Smooth transitions and micro-interactions
- High-contrast accessibility

### 3. Landing & Pricing Pages
- Modern landing page with hero section and feature showcase
- Comprehensive pricing page with tier comparison
- FAQ section and clear CTAs
- Guest user access (no login required)

### 4. Updated Routing
- **/** → Landing Page (public)
- **/pricing** → Pricing & Plans (public)
- **/login** → Authentication (public)
- **/signup** → Registration (public)
- **/dashboard** → User Dashboard (protected)
- **/analysis** → Chart Analysis (protected)
- **/learning** → Learning Content (protected)
- **/techniques** → Trading Techniques (protected)

## 🚀 Quick Start

### Prerequisites

- **Java**: 17 or higher
- **Node.js**: 18 or higher
- **MariaDB**: 10.6 or higher
- **Docker** (optional, for database)
- **Git**

### Database Setup

#### Option 1: Using Docker

```bash
cd tradevision/docker
docker-compose up -d
```

This will start MariaDB on port **3308** (not default 3306).

#### Option 2: Manual MariaDB Installation

1. Install MariaDB 10.6+
2. Create database:

```sql
CREATE DATABASE tradevision CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'tradevision'@'localhost' IDENTIFIED BY 'tradevision123';
GRANT ALL PRIVILEGES ON tradevision.* TO 'tradevision'@'localhost';
FLUSH PRIVILEGES;
```

3. Update `application-dev.yml` if using different port/credentials.

### Backend Setup

1. **Navigate to backend directory**:
   ```bash
   cd tradevision
   ```

2. **Set environment variables**:

   Create `.env` file or set in your IDE:
   ```properties
   JWT_SECRET=your-secret-key-min-256-bits-long
   OPENAI_API_KEY=your-openai-api-key
   KIS_APP_KEY=your-korea-investment-api-key
   KIS_APP_SECRET=your-korea-investment-secret
   ```

3. **Run Flyway migrations**:
   ```bash
   ./gradlew flywayMigrate
   ```

4. **Seed sample data**:
   ```bash
   # Using MariaDB client
   mysql -u tradevision -p tradevision < src/main/resources/db/sample-data-v7.sql

   # Or using Docker
   docker exec -i tradevision-db mysql -utradevision -ptradevision123 tradevision < src/main/resources/db/sample-data-v7.sql
   ```

5. **Run the application**:
   ```bash
   ./gradlew bootRun
   ```

Backend will start on `http://localhost:8080`

### Frontend Setup

1. **Navigate to frontend directory**:
   ```bash
   cd frontend
   ```

2. **Install dependencies**:
   ```bash
   npm install
   ```

3. **Create `.env` file**:
   ```env
   VITE_API_BASE_URL=http://localhost:8080
   ```

4. **Run development server**:
   ```bash
   npm run dev
   ```

Frontend will start on `http://localhost:5173`

### Access the Application

1. **Landing Page**: http://localhost:5173
2. **API Documentation**: http://localhost:8080/swagger-ui.html
3. **Database** (if using Docker): `localhost:3308`

## 📊 Database Migrations

The project includes 8 Flyway migrations:

1. **V1**: Users table
2. **V2**: Chart analyses table
3. **V4**: Trading techniques table
4. **V6**: Refresh tokens table
5. **V7**: Learning content system (modules + contents + progress)
6. **V8**: **NEW** - Subscription system (plans, user subscriptions, usage tracking, payment history)

**Note**: V3 and V5 were replaced by V7.

## 🔑 API Endpoints

### Public Endpoints (No Auth Required)

- `POST /api/auth/signup` - User registration
- `POST /api/auth/login` - User login
- `GET /api/subscriptions/plans` - Get pricing plans
- `GET /api/learning/modules` - Preview learning modules
- `GET /api/techniques` - Preview trading techniques

### Protected Endpoints (Auth Required)

#### Subscriptions
- `GET /api/subscriptions/current` - Get active subscription
- `POST /api/subscriptions/subscribe` - Subscribe to plan
- `POST /api/subscriptions/cancel` - Cancel subscription
- `GET /api/subscriptions/usage/{actionType}` - Get usage stats

#### Chart Analysis
- `POST /api/charts/upload` - Upload chart image
- `POST /api/charts/analyze/{chartId}` - Analyze chart with AI
- `GET /api/charts/history` - Get analysis history

#### Learning Content
- `GET /api/learning/modules` - Get all modules
- `GET /api/learning/modules/{id}/contents` - Get module contents
- `POST /api/learning/contents/{id}/progress` - Update progress
- `POST /api/learning/contents/{id}/like` - Like content
- `GET /api/learning/progress/stats` - Get learning statistics

#### Trading Techniques
- `GET /api/techniques` - Get all techniques
- `GET /api/techniques/{id}` - Get technique details
- `POST /api/techniques/{id}/bookmark` - Bookmark technique

## 🎨 Dark Theme Colors

### Primary Colors
- **Background Primary**: `#0a0e17` - Deep dark blue-black
- **Background Secondary**: `#151924` - Dark slate
- **Background Tertiary**: `#1a1f2e` - Card backgrounds
- **Primary Blue**: `#00d4ff` - Electric blue (accents)
- **Success Green**: `#00ff9d` - Neon green (bullish)
- **Danger Red**: `#ff3366` - Hot pink red (bearish)

### Usage
All Tailwind classes use the `dark-` prefix:
- `bg-dark-bg-primary` - Main background
- `text-dark-text-primary` - Primary text
- `border-dark-border-primary` - Borders

## 🧪 Testing

### Run Backend Tests
```bash
cd tradevision
./gradlew test
```

### Manual Testing Checklist

#### Guest User Flow
- [ ] Visit landing page at /
- [ ] Click "무료로 시작하기" → Should go to signup
- [ ] Click "요금제 보기" → Should go to pricing page
- [ ] View all three pricing tiers
- [ ] Click "무료로 시작하기" on FREE tier → Should go to signup

#### Registered User Flow
- [ ] Sign up with email/password
- [ ] Login successfully
- [ ] Redirected to /dashboard
- [ ] Upload and analyze a chart (counts toward quota)
- [ ] View learning modules
- [ ] Browse trading techniques
- [ ] Check usage stats shows correct remaining analyses

#### Subscription Flow
- [ ] User with FREE plan sees 5 analysis limit
- [ ] After 5 analyses, paywall appears
- [ ] Click upgrade → View pricing page
- [ ] Select PRO/PLATINUM → Login/signup required
- [ ] After payment simulation, unlimited analyses enabled

## 🐛 Common Issues

### Port 3308 Already in Use
If you get a port conflict:
```bash
# Stop existing MariaDB Docker container
docker stop tradevision-db

# Or change port in application-dev.yml and docker-compose.yml
```

### Flyway Migration Errors
If migrations fail:
```bash
# Clean and re-migrate
./gradlew flywayClean flywayMigrate

# Or manually drop database and recreate
```

### Frontend Build Errors
```bash
# Clear node_modules and reinstall
rm -rf node_modules package-lock.json
npm install
```

### CORS Errors
Ensure backend `application-dev.yml` includes your frontend URL:
```yaml
cors:
  allowed-origins: http://localhost:5173
```

## 📝 Sample Data

The `sample-data-v7.sql` includes:

- **5 Trading Techniques**:
  - 이동평균선 크로스 전략 (BEGINNER)
  - 볼린저 밴드 반등 매매 (INTERMEDIATE)
  - RSI 과매도/과매수 전략 (BEGINNER)
  - 지지선 반등 매매 (BEGINNER)
  - 박스권 돌파 매매 (INTERMEDIATE)

- **3 Learning Modules**:
  - 주식 투자 기초 (BEGINNER) - 3 contents
  - 기술적 분석 마스터 (INTERMEDIATE) - Multiple contents
  - 리스크 관리와 투자 심리 (ADVANCED)

- **3 Subscription Plans**:
  - FREE - ₩0 (5 analyses)
  - PRO - ₩15,000/month (Unlimited)
  - PLATINUM - ₩50,000/month (Premium)

## 🔄 Git Workflow

Recent commits:
1. ✅ `feat: implement freemium subscription system with usage tracking`
2. ✅ `feat: implement dark theme UI and landing/pricing pages`
3. ✅ `feat: update UI components for dark theme compatibility`
4. ✅ `feat: add complete frontend React application with all features`

## 📚 Next Steps

### Immediate Tasks
1. **Test the complete user flow** (guest → signup → analyze → upgrade)
2. **Load sample data** into the database
3. **Update existing pages** (Dashboard, Analysis, etc.) with dark theme
4. **Implement paywall modal** for when free tier is exhausted

### Future Enhancements
1. **Payment Integration**:
   - Integrate Stripe or Toss Payments
   - Webhook handlers for payment events
   - Subscription renewal automation

2. **Advanced Features**:
   - Real-time WebSocket for chart updates
   - Push notifications for pattern alerts
   - Trading performance tracking & analytics
   - Social features (community, sharing)

3. **Admin Panel**:
   - Content management system
   - User management
   - Analytics dashboard
   - Subscription management

4. **Mobile App**:
   - React Native app using existing APIs
   - Push notifications
   - Offline mode for learning content

## 🆘 Support

For issues or questions:
- Check existing documentation in `/docs`
- Review `INTEGRATION_TESTING_GUIDE.md`
- Check API documentation at `/swagger-ui.html`
- Review git commit history for implementation details

## 📄 License

© 2024 TradeVision. All rights reserved.

---

**Built with** ❤️ **and AI assistance from Claude Code**
