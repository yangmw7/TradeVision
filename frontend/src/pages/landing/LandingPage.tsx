import React from 'react';
import { useNavigate } from 'react-router-dom';
import { TrendingUp, BarChart3, Brain, Sparkles, ArrowRight, CheckCircle2 } from 'lucide-react';
import { Button } from '../../components/ui';

const LandingPage: React.FC = () => {
  const navigate = useNavigate();

  const features = [
    {
      icon: <Brain className="w-6 h-6" />,
      title: 'AI 차트 분석',
      description: 'GPT-4 Vision을 활용한 실시간 차트 패턴 분석',
      gradient: 'from-accent-blue to-accent-purple',
    },
    {
      icon: <BarChart3 className="w-6 h-6" />,
      title: '매매 기법 학습',
      description: '검증된 트레이딩 기법과 전략을 체계적으로 학습',
      gradient: 'from-accent-purple to-accent-red',
    },
    {
      icon: <Sparkles className="w-6 h-6" />,
      title: '맞춤형 추천',
      description: '투자 수준과 목표에 맞는 학습 콘텐츠 추천',
      gradient: 'from-accent-green to-accent-blue',
    },
  ];

  const benefits = [
    '무료로 5회 차트 분석 체험',
    '초보자를 위한 체계적인 학습 로드맵',
    '실전에서 바로 적용 가능한 매매 기법',
    '24/7 언제 어디서나 학습 가능',
  ];

  return (
    <div className="min-h-screen bg-gradient-dark">
      {/* Hero Section */}
      <div className="relative overflow-hidden">
        {/* Background decoration */}
        <div className="absolute inset-0 overflow-hidden">
          <div className="absolute -top-40 -right-40 w-80 h-80 bg-primary-500/10 rounded-full blur-3xl animate-pulse-slow"></div>
          <div className="absolute -bottom-40 -left-40 w-80 h-80 bg-accent-purple/10 rounded-full blur-3xl animate-pulse-slow"></div>
        </div>

        <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-20 pb-24">
          {/* Navigation */}
          <nav className="flex items-center justify-between mb-16">
            <div className="flex items-center gap-2">
              <TrendingUp className="w-8 h-8 text-primary-500" />
              <span className="text-2xl font-bold text-gradient">TradeVision</span>
            </div>
            <div className="flex items-center gap-4">
              <Button
                variant="outline"
                onClick={() => navigate('/login')}
                className="border-dark-border-secondary text-dark-text-primary hover:bg-dark-bg-tertiary"
              >
                로그인
              </Button>
              <Button
                onClick={() => navigate('/signup')}
                className="bg-primary-500 hover:bg-primary-600 text-white shadow-glow"
              >
                무료 시작하기
              </Button>
            </div>
          </nav>

          {/* Hero Content */}
          <div className="text-center space-y-8 max-w-4xl mx-auto">
            <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-dark-bg-tertiary border border-dark-border-primary">
              <Sparkles className="w-4 h-4 text-accent-yellow" />
              <span className="text-sm text-dark-text-secondary">AI 기반 차트 분석 플랫폼</span>
            </div>

            <h1 className="text-5xl md:text-6xl font-bold leading-tight">
              <span className="text-dark-text-primary">차트 분석,</span>
              <br />
              <span className="text-gradient glow-text">AI가 도와드립니다</span>
            </h1>

            <p className="text-xl text-dark-text-secondary max-w-2xl mx-auto">
              초보 투자자도 쉽게 배우는 주식 차트 분석.
              <br />
              GPT-4 Vision과 함께 전문가처럼 투자하세요.
            </p>

            <div className="flex flex-col sm:flex-row items-center justify-center gap-4 pt-4">
              <Button
                size="lg"
                onClick={() => navigate('/signup')}
                className="bg-primary-500 hover:bg-primary-600 text-white shadow-glow px-8 py-4 text-lg"
              >
                무료로 시작하기
                <ArrowRight className="w-5 h-5 ml-2" />
              </Button>
              <Button
                size="lg"
                variant="outline"
                onClick={() => navigate('/pricing')}
                className="border-dark-border-secondary text-dark-text-primary hover:bg-dark-bg-tertiary px-8 py-4 text-lg"
              >
                요금제 보기
              </Button>
            </div>

            {/* Stats */}
            <div className="grid grid-cols-3 gap-8 pt-12 max-w-2xl mx-auto">
              <div className="text-center">
                <div className="text-3xl font-bold text-primary-500">5회</div>
                <div className="text-sm text-dark-text-secondary mt-1">무료 체험</div>
              </div>
              <div className="text-center">
                <div className="text-3xl font-bold text-accent-green">100+</div>
                <div className="text-sm text-dark-text-secondary mt-1">학습 콘텐츠</div>
              </div>
              <div className="text-center">
                <div className="text-3xl font-bold text-accent-purple">24/7</div>
                <div className="text-sm text-dark-text-secondary mt-1">언제든 학습</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Features Section */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-24">
        <div className="text-center mb-16">
          <h2 className="text-4xl font-bold text-dark-text-primary mb-4">
            왜 TradeVision인가요?
          </h2>
          <p className="text-lg text-dark-text-secondary max-w-2xl mx-auto">
            AI 기술과 전문가의 노하우를 결합한 차별화된 학습 경험
          </p>
        </div>

        <div className="grid md:grid-cols-3 gap-8">
          {features.map((feature, index) => (
            <div
              key={index}
              className="glass-card rounded-xl p-8 hover:shadow-card-hover transition-all duration-300 animate-slide-up"
              style={{ animationDelay: `${index * 100}ms` }}
            >
              <div className={`inline-flex p-3 rounded-lg bg-gradient-to-r ${feature.gradient} bg-opacity-10 mb-4`}>
                {feature.icon}
              </div>
              <h3 className="text-xl font-semibold text-dark-text-primary mb-2">
                {feature.title}
              </h3>
              <p className="text-dark-text-secondary leading-relaxed">
                {feature.description}
              </p>
            </div>
          ))}
        </div>
      </div>

      {/* Benefits Section */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-24">
        <div className="grid md:grid-cols-2 gap-12 items-center">
          <div>
            <h2 className="text-4xl font-bold text-dark-text-primary mb-6">
              초보자도 쉽게 시작하는
              <br />
              <span className="text-gradient">스마트한 투자</span>
            </h2>
            <p className="text-lg text-dark-text-secondary mb-8">
              복잡한 차트 분석, 이제 AI와 함께라면 쉽습니다.
              체계적인 학습 시스템으로 투자 실력을 키워보세요.
            </p>
            <ul className="space-y-4">
              {benefits.map((benefit, index) => (
                <li key={index} className="flex items-center gap-3">
                  <CheckCircle2 className="w-5 h-5 text-accent-green flex-shrink-0" />
                  <span className="text-dark-text-secondary">{benefit}</span>
                </li>
              ))}
            </ul>
            <div className="mt-8">
              <Button
                size="lg"
                onClick={() => navigate('/signup')}
                className="bg-primary-500 hover:bg-primary-600 text-white shadow-glow"
              >
                지금 무료로 시작하기
                <ArrowRight className="w-5 h-5 ml-2" />
              </Button>
            </div>
          </div>

          <div className="relative">
            <div className="glass-card rounded-2xl p-8 border-2 border-primary-500/20">
              <div className="bg-dark-bg-secondary rounded-lg p-6 mb-4">
                <div className="h-48 bg-gradient-to-br from-primary-500/20 to-accent-purple/20 rounded-lg flex items-center justify-center">
                  <BarChart3 className="w-24 h-24 text-primary-500 opacity-50" />
                </div>
              </div>
              <div className="space-y-3">
                <div className="h-4 bg-dark-bg-secondary rounded w-3/4"></div>
                <div className="h-4 bg-dark-bg-secondary rounded w-1/2"></div>
              </div>
              <div className="absolute -top-4 -right-4 bg-accent-green text-dark-bg-primary rounded-full px-4 py-2 text-sm font-semibold shadow-glow">
                AI 분석 완료!
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* CTA Section */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-24">
        <div className="glass-card rounded-2xl p-12 text-center border-2 border-primary-500/20">
          <h2 className="text-4xl font-bold text-dark-text-primary mb-4">
            지금 바로 시작하세요
          </h2>
          <p className="text-lg text-dark-text-secondary mb-8 max-w-2xl mx-auto">
            무료로 5회 차트 분석을 체험하고, AI와 함께하는 스마트한 투자를 경험해보세요.
          </p>
          <Button
            size="lg"
            onClick={() => navigate('/signup')}
            className="bg-primary-500 hover:bg-primary-600 text-white shadow-glow px-12 py-4 text-lg"
          >
            무료 체험 시작하기
            <ArrowRight className="w-5 h-5 ml-2" />
          </Button>
          <p className="text-sm text-dark-text-muted mt-4">
            신용카드 등록 불필요 • 즉시 사용 가능
          </p>
        </div>
      </div>

      {/* Footer */}
      <footer className="border-t border-dark-border-primary py-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex flex-col md:flex-row justify-between items-center gap-4">
            <div className="flex items-center gap-2">
              <TrendingUp className="w-6 h-6 text-primary-500" />
              <span className="text-lg font-semibold text-dark-text-primary">TradeVision</span>
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
