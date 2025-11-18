import React from 'react';
import { useNavigate } from 'react-router-dom';
import { TrendingUp, TrendingDown, ArrowRight } from 'lucide-react';
import { Button } from '../../components/ui';

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
      thumbnail: "https://via.placeholder.com/300x200?text=Chart+1"
    },
    {
      id: 2,
      title: "KOSPI 200 지지선 분석",
      author: "박투자",
      date: "2024-11-18",
      likes: 18,
      thumbnail: "https://via.placeholder.com/300x200?text=Chart+2"
    },
    {
      id: 3,
      title: "미국 기술주 상승 추세 지속",
      author: "이분석가",
      date: "2024-11-17",
      likes: 32,
      thumbnail: "https://via.placeholder.com/300x200?text=Chart+3"
    },
    {
      id: 4,
      title: "원달러 환율 1350선 돌파",
      author: "최마켓",
      date: "2024-11-17",
      likes: 15,
      thumbnail: "https://via.placeholder.com/300x200?text=Chart+4"
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
      <div className="relative bg-[#0a0e17] min-h-screen flex flex-col">
        {/* Navigation */}
        <nav className="border-b border-gray-800">
          <div className="max-w-7xl mx-auto px-6 py-4 flex items-center justify-between">
            <div className="flex items-center gap-2">
              <TrendingUp className="w-6 h-6 text-[#00d4ff]" />
              <span className="text-xl font-bold text-white">TradeVision</span>
            </div>
            <div className="flex items-center gap-4">
              <button
                onClick={() => navigate('/login')}
                className="text-gray-300 hover:text-white transition px-4 py-2"
              >
                로그인
              </button>
              <Button onClick={() => navigate('/signup')} className="bg-white text-[#0a0e17] hover:bg-gray-100">
                무료 시작하기
              </Button>
            </div>
          </div>
        </nav>

        {/* Hero Content - Centered */}
        <div className="flex-1 flex items-center justify-center px-6 py-20">
          <div className="text-center max-w-5xl">
            <h1 className="text-7xl md:text-8xl font-bold text-white mb-8 leading-tight">
              Look first / Then leap.
            </h1>
            <p className="text-2xl md:text-3xl text-gray-300 mb-12 leading-relaxed">
              최고의 트레이딩을 위해서는 연구와 노력이 필요합니다
            </p>
            <div className="flex flex-col items-center gap-4">
              <Button
                size="lg"
                onClick={() => navigate('/signup')}
                className="text-lg px-12 py-6 bg-white text-[#0a0e17] hover:bg-gray-100 font-semibold"
              >
                무료로 시작하기
              </Button>
              <p className="text-sm text-gray-400">
                영원히 $0, 신용카드 필요 없음
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Smooth Gradient Transition */}
      <div className="h-[200px] bg-gradient-to-b from-[#0a0e17] to-white" />

      {/* Community Section - White Background */}
      <div className="bg-white py-20">
        <div className="max-w-7xl mx-auto px-6">
          {/* Section Header */}
          <div className="flex items-center justify-between mb-12">
            <div>
              <h2 className="text-4xl font-bold text-gray-900 mb-2">커뮤니티 아이디어</h2>
              <p className="text-lg text-gray-600">
                전문 트레이더들의 최신 차트 분석을 확인해보세요
              </p>
            </div>
            <button className="text-[#00d4ff] hover:text-[#00b8e6] font-medium flex items-center gap-2">
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
                  <h3 className="font-semibold text-gray-900 mb-2 line-clamp-2 group-hover:text-[#00d4ff] transition">
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
      </div>

      {/* Market Overview Section - White Background */}
      <div className="bg-white py-20 border-t border-gray-200">
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
      </div>

      {/* CTA Section - White Background */}
      <div className="bg-white py-20 border-t border-gray-200">
        <div className="max-w-7xl mx-auto px-6 text-center">
          <h2 className="text-5xl font-bold text-gray-900 mb-6">
            지금 시작하세요
          </h2>
          <p className="text-xl text-gray-600 mb-12 max-w-2xl mx-auto">
            무료로 5회 차트 분석을 체험하고, AI와 함께하는 스마트한 투자를 경험해보세요.
          </p>
          <Button
            size="lg"
            onClick={() => navigate('/signup')}
            className="text-lg px-12 py-4 bg-[#00d4ff] text-white hover:bg-[#00b8e6]"
          >
            무료 체험 시작하기
          </Button>
        </div>
      </div>

      {/* Footer - Dark Background */}
      <footer className="bg-[#0a0e17] border-t border-gray-800">
        <div className="max-w-7xl mx-auto px-6 py-12">
          <div className="grid md:grid-cols-4 gap-8 mb-8">
            <div>
              <div className="flex items-center gap-2 mb-4">
                <TrendingUp className="w-5 h-5 text-[#00d4ff]" />
                <span className="text-white font-bold text-lg">TradeVision</span>
              </div>
              <p className="text-gray-400 text-sm">
                AI 기반 차트 분석 플랫폼
              </p>
            </div>

            <div>
              <h4 className="text-white font-semibold mb-4">서비스</h4>
              <ul className="space-y-2 text-gray-400 text-sm">
                <li><button className="hover:text-white transition">차트 분석</button></li>
                <li><button className="hover:text-white transition">학습 콘텐츠</button></li>
                <li><button className="hover:text-white transition">매매 기법</button></li>
              </ul>
            </div>

            <div>
              <h4 className="text-white font-semibold mb-4">회사</h4>
              <ul className="space-y-2 text-gray-400 text-sm">
                <li><button className="hover:text-white transition">소개</button></li>
                <li><button className="hover:text-white transition">요금제</button></li>
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

          <div className="border-t border-gray-800 pt-8">
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
