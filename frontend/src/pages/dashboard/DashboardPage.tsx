import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { learningApi, chartsApi } from '../../api';
import type { LearningStats, AnalysisHistory } from '../../types';
import {
  BookOpen,
  GraduationCap,
  TrendingUp,
  Clock,
  Award,
  BarChart3,
  ArrowRight,
} from 'lucide-react';

const DashboardPage: React.FC = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState<LearningStats | null>(null);
  const [recentAnalyses, setRecentAnalyses] = useState<AnalysisHistory[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [statsData, analysesData] = await Promise.all([
          learningApi.getStats().catch(() => null),
          chartsApi.getHistory().catch(() => []),
        ]);
        setStats(statsData);
        setRecentAnalyses(Array.isArray(analysesData) ? analysesData.slice(0, 5) : []);
      } catch (error) {
        console.error('Failed to fetch dashboard data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) {
    return (
      <div className="min-h-screen bg-[#0a0e17] pt-20 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  const completionRate = stats
    ? Math.round((stats.completedContents / stats.totalContents) * 100)
    : 0;

  return (
    <div className="min-h-screen bg-[#0a0e17] pt-20">
      <main className="px-6 pb-12">
        <div className="max-w-7xl mx-auto">
          {/* Welcome Section */}
          <div className="mb-12">
            <h1 className="text-4xl font-bold text-white mb-2">
              안녕하세요, {user?.nickname || '사용자'}님! 👋
            </h1>
            <p className="text-xl text-gray-400">
              오늘도 트레이딩 실력을 키워보세요.
            </p>
          </div>

          {/* Quick Actions */}
          <div className="grid md:grid-cols-3 gap-6 mb-12">
            <Link
              to="/analysis"
              className="bg-[#1a1f2e] rounded-xl p-6 border border-[#2a2e39] hover:border-blue-500 transition-all group"
            >
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-gray-400 text-sm font-medium">차트 분석</h3>
                <div className="w-10 h-10 bg-blue-500/10 rounded-lg flex items-center justify-center group-hover:bg-blue-500/20 transition-all">
                  <TrendingUp className="w-5 h-5 text-blue-500" />
                </div>
              </div>
              <p className="text-3xl font-bold text-white mb-2">
                {recentAnalyses.length}
              </p>
              <div className="flex items-center text-blue-500 text-sm font-medium mt-4">
                새로운 분석하기 <ArrowRight className="w-4 h-4 ml-1" />
              </div>
            </Link>

            <Link
              to="/techniques"
              className="bg-[#1a1f2e] rounded-xl p-6 border border-[#2a2e39] hover:border-green-500 transition-all group"
            >
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-gray-400 text-sm font-medium">매매 기법</h3>
                <div className="w-10 h-10 bg-green-500/10 rounded-lg flex items-center justify-center group-hover:bg-green-500/20 transition-all">
                  <BookOpen className="w-5 h-5 text-green-500" />
                </div>
              </div>
              <p className="text-3xl font-bold text-white mb-2">30+</p>
              <div className="flex items-center text-green-500 text-sm font-medium mt-4">
                기법 둘러보기 <ArrowRight className="w-4 h-4 ml-1" />
              </div>
            </Link>

            <Link
              to="/learning"
              className="bg-[#1a1f2e] rounded-xl p-6 border border-[#2a2e39] hover:border-purple-500 transition-all group"
            >
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-gray-400 text-sm font-medium">학습 콘텐츠</h3>
                <div className="w-10 h-10 bg-purple-500/10 rounded-lg flex items-center justify-center group-hover:bg-purple-500/20 transition-all">
                  <GraduationCap className="w-5 h-5 text-purple-500" />
                </div>
              </div>
              <p className="text-3xl font-bold text-white mb-2">
                {stats?.completedContents || 0}/{stats?.totalContents || 0}
              </p>
              <div className="flex items-center text-purple-500 text-sm font-medium mt-4">
                학습 계속하기 <ArrowRight className="w-4 h-4 ml-1" />
              </div>
            </Link>
          </div>

          {/* Statistics */}
          <div className="grid md:grid-cols-4 gap-6 mb-12">
            <div className="bg-[#1a1f2e] rounded-xl p-6 border border-[#2a2e39]">
              <div className="flex items-center justify-between mb-4">
                <span className="text-gray-400 text-sm">완료한 콘텐츠</span>
                <Award className="text-blue-500" size={20} />
              </div>
              <p className="text-3xl font-bold text-white">
                {stats?.completedContents || 0}
              </p>
              <p className="text-sm text-gray-500 mt-1">
                전체 {stats?.totalContents || 0}개 중
              </p>
            </div>

            <div className="bg-[#1a1f2e] rounded-xl p-6 border border-[#2a2e39]">
              <div className="flex items-center justify-between mb-4">
                <span className="text-gray-400 text-sm">학습 진행률</span>
                <TrendingUp className="text-green-500" size={20} />
              </div>
              <p className="text-3xl font-bold text-white">{completionRate}%</p>
              <div className="w-full bg-[#2a2e39] rounded-full h-2 mt-3">
                <div
                  className="bg-green-500 h-2 rounded-full transition-all duration-300"
                  style={{ width: `${completionRate}%` }}
                />
              </div>
            </div>

            <div className="bg-[#1a1f2e] rounded-xl p-6 border border-[#2a2e39]">
              <div className="flex items-center justify-between mb-4">
                <span className="text-gray-400 text-sm">학습 시간</span>
                <Clock className="text-purple-500" size={20} />
              </div>
              <p className="text-3xl font-bold text-white">
                {stats?.totalLearningMinutes || 0}
              </p>
              <p className="text-sm text-gray-500 mt-1">분</p>
            </div>

            <div className="bg-[#1a1f2e] rounded-xl p-6 border border-[#2a2e39]">
              <div className="flex items-center justify-between mb-4">
                <span className="text-gray-400 text-sm">연속 학습</span>
                <Award className="text-orange-500" size={20} />
              </div>
              <p className="text-3xl font-bold text-white">
                {stats?.currentStreak || 0}일
              </p>
              <p className="text-sm text-gray-500 mt-1">연속 학습 중!</p>
            </div>
          </div>

          {/* Recent Activity */}
          <div className="bg-[#1a1f2e] rounded-xl p-6 border border-[#2a2e39]">
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-2xl font-bold text-white">최근 활동</h2>
              {recentAnalyses.length > 0 && (
                <Link
                  to="/analysis"
                  className="text-blue-500 hover:text-blue-400 text-sm font-medium flex items-center gap-1"
                >
                  전체 보기 <ArrowRight className="w-4 h-4" />
                </Link>
              )}
            </div>

            {recentAnalyses.length > 0 ? (
              <div className="space-y-4">
                {recentAnalyses.map((analysis) => (
                  <Link
                    key={analysis.id}
                    to={`/analysis/${analysis.id}`}
                    className="flex items-center justify-between p-4 bg-[#131824] rounded-lg hover:bg-[#1e2330] transition-colors group"
                  >
                    <div className="flex items-center gap-4 flex-1 min-w-0">
                      {analysis.imagePath && (
                        <div className="w-16 h-16 bg-[#2a2e39] rounded-lg flex-shrink-0 overflow-hidden">
                          <img
                            src={`http://localhost:8080${analysis.imagePath}`}
                            alt="Chart"
                            className="w-full h-full object-cover"
                          />
                        </div>
                      )}
                      <div className="flex-1 min-w-0">
                        <h3 className="text-white font-medium line-clamp-1 group-hover:text-blue-500 transition">
                          차트 분석 #{analysis.id}
                        </h3>
                        <p className="text-gray-400 text-sm line-clamp-1">
                          {analysis.analysis || '분석 내용'}
                        </p>
                        <p className="text-gray-500 text-xs mt-1">
                          {new Date(analysis.createdAt).toLocaleDateString('ko-KR')}
                        </p>
                      </div>
                    </div>
                    <ArrowRight className="w-5 h-5 text-gray-600 group-hover:text-blue-500 transition flex-shrink-0 ml-4" />
                  </Link>
                ))}
              </div>
            ) : (
              <div className="text-center py-16">
                <div className="w-16 h-16 bg-[#2a2e39] rounded-full flex items-center justify-center mx-auto mb-4">
                  <BarChart3 className="w-8 h-8 text-gray-600" />
                </div>
                <p className="text-gray-400 mb-6">아직 분석한 차트가 없습니다</p>
                <Link
                  to="/analysis"
                  className="inline-flex items-center px-6 py-3 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors"
                >
                  첫 차트 분석하기 <ArrowRight className="w-4 h-4 ml-2" />
                </Link>
              </div>
            )}
          </div>
        </div>
      </main>
    </div>
  );
};

export default DashboardPage;
