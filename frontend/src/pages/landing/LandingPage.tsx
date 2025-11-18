import React from 'react';
import { useNavigate } from 'react-router-dom';
import { TrendingUp, TrendingDown, ArrowRight, ChevronDown, BarChart3, Brain, CheckCircle } from 'lucide-react';

const LandingPage: React.FC = () => {
  const navigate = useNavigate();

  // Sample community posts
  const communityPosts = [
    {
      id: 1,
      title: "삼성전자 단기 반등 패턴 포착",
      author: "김트레이더",
      date: "2024-11-18",
      likes: 24,
      thumbnail: "https://via.placeholder.com/300x200/1a1f2e/00d4ff?text=Chart+Analysis"
    },
    {
      id: 2,
      title: "KOSPI 200 지지선 분석",
      author: "박투자",
      date: "2024-11-18",
      likes: 18,
      thumbnail: "https://via.placeholder.com/300x200/1a1f2e/00d4ff?text=Chart+Analysis"
    },
    {
      id: 3,
      title: "미국 기술주 상승 추세 지속",
      author: "이분석가",
      date: "2024-11-17",
      likes: 32,
      thumbnail: "https://via.placeholder.com/300x200/1a1f2e/00d4ff?text=Chart+Analysis"
    },
    {
      id: 4,
      title: "원달러 환율 1350선 돌파",
      author: "최마켓",
      date: "2024-11-17",
      likes: 15,
      thumbnail: "https://via.placeholder.com/300x200/1a1f2e/00d4ff?text=Chart+Analysis"
    }
  ];

  // Sample market data
  const marketData = [
    { name: "KOSPI", value: "2,547.28", change: "+12.45", percent: "+0.49%", isUp: true },
    { name: "KOSDAQ", value: "721.45", change: "-3.21", percent: "-0.44%", isUp: false },
    { name: "S&P 500", value: "4,567.89", change: "+23.45", percent: "+0.52%", isUp: true },
    { name: "NASDAQ", value: "14,234.56", change: "+87.23", percent: "+0.62%", isUp: true },
    { name: "원/달러", value: "1,348.50", change: "+5.20", percent: "+0.39%", isUp: true },
    { name: "비트코인", value: "$43,567", change: "+$234", percent: "+0.54%", isUp: true }
  ];

  return (
    <div className="min-h-screen">
      {/* Hero Section - Dark Background with Centered Content */}
      <section className="relative min-h-screen flex items-center justify-center bg-[#0a0e17] overflow-hidden">
        {/* Background gradient */}
        <div className="absolute inset-0 bg-gradient-to-b from-[#0a0e17] via-[#131824] to-[#0a0e17]" />

        {/* Animated background pattern */}
        <div className="absolute inset-0 opacity-10">
          <div className="absolute inset-0" style={{
            backgroundImage: 'linear-gradient(rgba(59, 130, 246, 0.1) 1px, transparent 1px), linear-gradient(90deg, rgba(59, 130, 246, 0.1) 1px, transparent 1px)',
            backgroundSize: '50px 50px'
          }} />
        </div>

        {/* Content - CENTERED */}
        <div className="relative z-10 text-center px-6 max-w-5xl mx-auto pt-20">
          <h1 className="text-7xl md:text-8xl lg:text-9xl font-bold text-white mb-8 leading-tight">
            Look first / Then leap.
          </h1>
          <p className="text-2xl md:text-3xl text-gray-300 mb-12 leading-relaxed">
            AI 기반 차트 분석으로 현명한 투자 결정을 내리세요
          </p>

          <div className="flex flex-col sm:flex-row items-center justify-center gap-4 mb-8">
            <button
              onClick={() => navigate('/signup')}
              className="px-10 py-4 bg-white text-black text-lg font-semibold rounded-lg hover:bg-gray-100 transition-all transform hover:scale-105 shadow-lg"
            >
              무료로 시작하기
            </button>
            <button
              onClick={() => navigate('/pricing')}
              className="px-10 py-4 bg-transparent border-2 border-white text-white text-lg font-semibold rounded-lg hover:bg-white hover:text-black transition-all"
            >
              요금제 보기
            </button>
          </div>

          <p className="text-gray-400 text-sm">
            영원히 $0, 신용카드 필요 없음
          </p>
        </div>

        {/* Scroll indicator */}
        <div className="absolute bottom-8 left-1/2 transform -translate-x-1/2 animate-bounce">
          <ChevronDown className="w-8 h-8 text-gray-400" />
        </div>
      </section>

      {/* Features Section - Dark */}
      <section className="py-20 bg-[#0a0e17]">
        <div className="max-w-7xl mx-auto px-6">
          <h2 className="text-4xl font-bold text-white text-center mb-4">
            강력한 기능으로 트레이딩 실력 향상
          </h2>
          <p className="text-xl text-gray-400 text-center mb-16">
            AI와 함께 전문가 수준의 차트 분석을 경험하세요
          </p>

          <div className="grid md:grid-cols-3 gap-8">
            <div className="bg-[#1a1f2e] rounded-xl p-8 border border-[#2a2e39] hover:border-blue-500 transition-all group">
              <div className="w-12 h-12 bg-blue-500/10 rounded-lg flex items-center justify-center mb-6 group-hover:bg-blue-500/20 transition-all">
                <Brain className="w-6 h-6 text-blue-500" />
              </div>
              <h3 className="text-2xl font-bold text-white mb-4">
                AI 차트 분석
              </h3>
              <p className="text-gray-400 leading-relaxed">
                GPT-4 Vision이 차트를 분석하고 매매 시그널을 제안합니다. 초보자도 전문가처럼 차트를 읽을 수 있습니다.
              </p>
            </div>

            <div className="bg-[#1a1f2e] rounded-xl p-8 border border-[#2a2e39] hover:border-blue-500 transition-all group">
              <div className="w-12 h-12 bg-green-500/10 rounded-lg flex items-center justify-center mb-6 group-hover:bg-green-500/20 transition-all">
                <BarChart3 className="w-6 h-6 text-green-500" />
              </div>
              <h3 className="text-2xl font-bold text-white mb-4">
                체계적 학습
              </h3>
              <p className="text-gray-400 leading-relaxed">
                초보자를 위한 단계별 학습 시스템으로 실력을 키우세요. 이론부터 실전까지 모두 학습할 수 있습니다.
              </p>
            </div>

            <div className="bg-[#1a1f2e] rounded-xl p-8 border border-[#2a2e39] hover:border-blue-500 transition-all group">
              <div className="w-12 h-12 bg-purple-500/10 rounded-lg flex items-center justify-center mb-6 group-hover:bg-purple-500/20 transition-all">
                <CheckCircle className="w-6 h-6 text-purple-500" />
              </div>
              <h3 className="text-2xl font-bold text-white mb-4">
                검증된 전략
              </h3>
              <p className="text-gray-400 leading-relaxed">
                전문가들이 사용하는 매매 기법을 배우고 적용하세요. 실전에서 바로 활용 가능한 전략들입니다.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Smooth Gradient Transition */}
      <div className="h-48 bg-gradient-to-b from-[#0a0e17] to-white" />

      {/* Community Section - White Background */}
      <section className="bg-white py-20">
        <div className="max-w-7xl mx-auto px-6">
          {/* Section Header */}
          <div className="flex items-center justify-between mb-12">
            <div>
              <h2 className="text-4xl font-bold text-gray-900 mb-2">커뮤니티 아이디어</h2>
              <p className="text-lg text-gray-600">
                전문 트레이더들의 최신 차트 분석을 확인해보세요
              </p>
            </div>
            <button className="text-blue-600 hover:text-blue-700 font-medium flex items-center gap-2">
              더 보기 <ArrowRight size={18} />
            </button>
          </div>

          {/* Community Posts Grid */}
          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
            {communityPosts.map((post) => (
              <div
                key={post.id}
                className="bg-white border border-gray-200 rounded-lg overflow-hidden hover:shadow-lg transition-shadow cursor-pointer group"
              >
                <div className="aspect-[3/2] bg-gray-100 relative overflow-hidden">
                  <img
                    src={post.thumbnail}
                    alt={post.title}
                    className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                  />
                </div>
                <div className="p-4">
                  <h3 className="font-semibold text-gray-900 mb-2 line-clamp-2 group-hover:text-blue-600 transition">
                    {post.title}
                  </h3>
                  <div className="flex items-center justify-between text-sm text-gray-500">
                    <span>{post.author}</span>
                    <div className="flex items-center gap-3">
                      <span>{post.date}</span>
                      <span className="text-red-500">❤ {post.likes}</span>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Market Overview Section - White Background */}
      <section className="bg-white py-20 border-t border-gray-200">
        <div className="max-w-7xl mx-auto px-6">
          <h2 className="text-4xl font-bold text-gray-900 mb-12">시장 요약</h2>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            {marketData.map((market, index) => (
              <div
                key={index}
                className="bg-white border border-gray-200 rounded-lg p-6 hover:shadow-md transition-shadow"
              >
                <div className="flex items-start justify-between mb-2">
                  <h3 className="text-lg font-semibold text-gray-900">{market.name}</h3>
                  {market.isUp ? (
                    <TrendingUp className="w-5 h-5 text-green-500" />
                  ) : (
                    <TrendingDown className="w-5 h-5 text-red-500" />
                  )}
                </div>
                <div className="text-3xl font-bold text-gray-900 mb-2">
                  {market.value}
                </div>
                <div className={`flex items-center gap-2 text-sm font-medium ${
                  market.isUp ? 'text-green-600' : 'text-red-600'
                }`}>
                  <span>{market.change}</span>
                  <span>{market.percent}</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section - White Background */}
      <section className="bg-white py-20 border-t border-gray-200">
        <div className="max-w-7xl mx-auto px-6 text-center">
          <h2 className="text-5xl font-bold text-gray-900 mb-6">
            지금 시작하세요
          </h2>
          <p className="text-xl text-gray-600 mb-12 max-w-2xl mx-auto">
            무료로 5회 차트 분석을 체험하고, AI와 함께하는 스마트한 투자를 경험해보세요.
          </p>
          <button
            onClick={() => navigate('/signup')}
            className="px-12 py-4 bg-blue-600 hover:bg-blue-700 text-white text-lg font-semibold rounded-lg transition-all transform hover:scale-105 shadow-lg"
          >
            무료 체험 시작하기
          </button>
        </div>
      </section>

      {/* Footer - Dark Background */}
      <footer className="bg-[#0a0e17] border-t border-[#2a2e39]">
        <div className="max-w-7xl mx-auto px-6 py-12">
          <div className="grid md:grid-cols-4 gap-8 mb-8">
            <div>
              <div className="flex items-center gap-2 mb-4">
                <TrendingUp className="w-5 h-5 text-blue-500" />
                <span className="text-white font-bold text-lg">TradeVision</span>
              </div>
              <p className="text-gray-400 text-sm">
                AI 기반 차트 분석 플랫폼
              </p>
            </div>

            <div>
              <h4 className="text-white font-semibold mb-4">서비스</h4>
              <ul className="space-y-2 text-gray-400 text-sm">
                <li><button className="hover:text-white transition" onClick={() => navigate('/analysis')}>차트 분석</button></li>
                <li><button className="hover:text-white transition" onClick={() => navigate('/learning')}>학습 콘텐츠</button></li>
                <li><button className="hover:text-white transition" onClick={() => navigate('/techniques')}>매매 기법</button></li>
              </ul>
            </div>

            <div>
              <h4 className="text-white font-semibold mb-4">회사</h4>
              <ul className="space-y-2 text-gray-400 text-sm">
                <li><button className="hover:text-white transition">소개</button></li>
                <li><button className="hover:text-white transition" onClick={() => navigate('/pricing')}>요금제</button></li>
                <li><button className="hover:text-white transition">문의</button></li>
              </ul>
            </div>

            <div>
              <h4 className="text-white font-semibold mb-4">법적 고지</h4>
              <ul className="space-y-2 text-gray-400 text-sm">
                <li><button className="hover:text-white transition">이용약관</button></li>
                <li><button className="hover:text-white transition">개인정보처리방침</button></li>
              </ul>
            </div>
          </div>

          <div className="border-t border-[#2a2e39] pt-8">
            <p className="text-sm text-gray-500 text-center">
              © 2024 TradeVision. All rights reserved.
            </p>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default LandingPage;
