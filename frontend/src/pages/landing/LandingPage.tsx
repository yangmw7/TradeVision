import React from 'react';
import { useNavigate } from 'react-router-dom';
import { TrendingUp, BarChart3, Brain, CheckCircle } from 'lucide-react';
import { Button } from '../../components/ui';

const LandingPage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-dark-bg-primary">
      {/* Navigation */}
      <nav className="border-b border-dark-border">
        <div className="max-w-7xl mx-auto px-6 py-4 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <TrendingUp className="w-6 h-6 text-accent-blue" />
            <span className="text-xl font-bold text-dark-text-primary">TradeVision</span>
          </div>
          <div className="flex items-center gap-4">
            <button
              onClick={() => navigate('/login')}
              className="text-dark-text-secondary hover:text-dark-text-primary transition"
            >
              로그인
            </button>
            <Button onClick={() => navigate('/signup')}>
              무료 시작하기
            </Button>
          </div>
        </div>
      </nav>

      {/* Hero Section */}
      <div className="max-w-7xl mx-auto px-6 py-32">
        <div className="max-w-4xl">
          <h1 className="text-6xl md:text-7xl font-bold text-dark-text-primary mb-6 leading-tight">
            먼저 보고,
            <br />
            그다음 투자하세요.
          </h1>
          <p className="text-2xl text-dark-text-secondary mb-12 leading-relaxed">
            AI가 차트를 분석하고, 당신은 결정만 하세요.
            <br />
            초보 투자자를 위한 가장 쉬운 주식 학습 플랫폼.
          </p>
          <div className="flex items-center gap-4">
            <Button
              size="lg"
              onClick={() => navigate('/signup')}
              className="text-lg px-8 py-4"
            >
              무료로 시작하기
            </Button>
            <Button
              size="lg"
              variant="outline"
              onClick={() => navigate('/pricing')}
              className="text-lg px-8 py-4"
            >
              요금제 보기
            </Button>
          </div>
        </div>
      </div>

      {/* Stats */}
      <div className="border-y border-dark-border bg-dark-bg-secondary/50">
        <div className="max-w-7xl mx-auto px-6 py-16">
          <div className="grid grid-cols-3 gap-12">
            <div>
              <div className="text-4xl font-bold text-accent-green mb-2">5회</div>
              <div className="text-dark-text-secondary">무료 차트 분석</div>
            </div>
            <div>
              <div className="text-4xl font-bold text-accent-blue mb-2">100+</div>
              <div className="text-dark-text-secondary">학습 콘텐츠</div>
            </div>
            <div>
              <div className="text-4xl font-bold text-dark-text-primary mb-2">24/7</div>
              <div className="text-dark-text-secondary">언제든 학습</div>
            </div>
          </div>
        </div>
      </div>

      {/* Features */}
      <div className="max-w-7xl mx-auto px-6 py-32">
        <div className="grid md:grid-cols-3 gap-12">
          <div className="bg-dark-bg-card border border-dark-border rounded-lg p-8">
            <Brain className="w-12 h-12 text-accent-blue mb-6" />
            <h3 className="text-2xl font-bold text-dark-text-primary mb-4">
              AI 차트 분석
            </h3>
            <p className="text-dark-text-secondary leading-relaxed">
              GPT-4 Vision이 차트를 분석하고 매매 시그널을 제안합니다.
            </p>
          </div>

          <div className="bg-dark-bg-card border border-dark-border rounded-lg p-8">
            <BarChart3 className="w-12 h-12 text-accent-green mb-6" />
            <h3 className="text-2xl font-bold text-dark-text-primary mb-4">
              체계적 학습
            </h3>
            <p className="text-dark-text-secondary leading-relaxed">
              초보자를 위한 단계별 학습 시스템으로 실력을 키우세요.
            </p>
          </div>

          <div className="bg-dark-bg-card border border-dark-border rounded-lg p-8">
            <CheckCircle className="w-12 h-12 text-dark-text-primary mb-6" />
            <h3 className="text-2xl font-bold text-dark-text-primary mb-4">
              검증된 전략
            </h3>
            <p className="text-dark-text-secondary leading-relaxed">
              전문가들이 사용하는 매매 기법을 배우고 적용하세요.
            </p>
          </div>
        </div>
      </div>

      {/* CTA Section */}
      <div className="border-t border-dark-border bg-dark-bg-secondary/50">
        <div className="max-w-7xl mx-auto px-6 py-32 text-center">
          <h2 className="text-5xl font-bold text-dark-text-primary mb-6">
            지금 시작하세요
          </h2>
          <p className="text-xl text-dark-text-secondary mb-12 max-w-2xl mx-auto">
            무료로 5회 차트 분석을 체험하고, AI와 함께하는 스마트한 투자를 경험해보세요.
          </p>
          <Button
            size="lg"
            onClick={() => navigate('/signup')}
            className="text-lg px-12 py-4"
          >
            무료 체험 시작하기
          </Button>
        </div>
      </div>

      {/* Footer */}
      <footer className="border-t border-dark-border">
        <div className="max-w-7xl mx-auto px-6 py-8">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <TrendingUp className="w-5 h-5 text-accent-blue" />
              <span className="text-dark-text-primary font-semibold">TradeVision</span>
            </div>
            <div className="text-sm text-dark-text-muted">
              © 2024 TradeVision. All rights reserved.
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default LandingPage;
