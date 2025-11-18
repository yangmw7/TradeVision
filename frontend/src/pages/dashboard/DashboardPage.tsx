import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { Card, CardHeader, CardBody, Button, Loading, Badge } from '../../components/ui';
import { learningApi, chartsApi } from '../../api';
import type { LearningStats, AnalysisHistory } from '../../types';
import {
  LineChart,
  BookOpen,
  GraduationCap,
  TrendingUp,
  Clock,
  Award,
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
          learningApi.getStats(),
          chartsApi.getHistory(),
        ]);
        setStats(statsData);
        setRecentAnalyses(analysesData.slice(0, 5)); // Get latest 5
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
      <div className="flex items-center justify-center h-96">
        <Loading size="lg" text="데이터를 불러오는 중..." />
      </div>
    );
  }

  const completionRate = stats
    ? Math.round((stats.completedContents / stats.totalContents) * 100)
    : 0;

  return (
    <div className="space-y-6">
      {/* Welcome Section */}
      <div>
        <h1 className="text-3xl font-bold text-gray-900 mb-2">
          안녕하세요, {user?.username}님! 👋
        </h1>
        <p className="text-gray-600">오늘도 트레이딩 실력을 키워보세요.</p>
      </div>

      {/* Quick Actions */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Link to="/analysis">
          <Card hover className="p-6">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 bg-primary-100 rounded-lg flex items-center justify-center">
                <LineChart className="text-primary-600" size={24} />
              </div>
              <div>
                <h3 className="font-semibold text-gray-900">차트 분석</h3>
                <p className="text-sm text-gray-600">AI로 차트 분석하기</p>
              </div>
            </div>
          </Card>
        </Link>

        <Link to="/techniques">
          <Card hover className="p-6">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
                <BookOpen className="text-green-600" size={24} />
              </div>
              <div>
                <h3 className="font-semibold text-gray-900">매매 기법</h3>
                <p className="text-sm text-gray-600">기법 둘러보기</p>
              </div>
            </div>
          </Card>
        </Link>

        <Link to="/learning">
          <Card hover className="p-6">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
                <GraduationCap className="text-blue-600" size={24} />
              </div>
              <div>
                <h3 className="font-semibold text-gray-900">학습하기</h3>
                <p className="text-sm text-gray-600">콘텐츠 학습하기</p>
              </div>
            </div>
          </Card>
        </Link>
      </div>

      {/* Statistics */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <Card className="p-6">
          <div className="flex items-center justify-between mb-2">
            <span className="text-gray-600">완료한 콘텐츠</span>
            <Award className="text-primary-500" size={20} />
          </div>
          <p className="text-3xl font-bold text-gray-900">
            {stats?.completedContents || 0}
          </p>
          <p className="text-sm text-gray-500 mt-1">
            전체 {stats?.totalContents || 0}개 중
          </p>
        </Card>

        <Card className="p-6">
          <div className="flex items-center justify-between mb-2">
            <span className="text-gray-600">학습 진행률</span>
            <TrendingUp className="text-green-500" size={20} />
          </div>
          <p className="text-3xl font-bold text-gray-900">{completionRate}%</p>
          <div className="w-full bg-gray-200 rounded-full h-2 mt-3">
            <div
              className="bg-primary-500 h-2 rounded-full transition-all duration-300"
              style={{ width: `${completionRate}%` }}
            />
          </div>
        </Card>

        <Card className="p-6">
          <div className="flex items-center justify-between mb-2">
            <span className="text-gray-600">학습 시간</span>
            <Clock className="text-blue-500" size={20} />
          </div>
          <p className="text-3xl font-bold text-gray-900">
            {stats?.totalLearningMinutes || 0}
          </p>
          <p className="text-sm text-gray-500 mt-1">분</p>
        </Card>

        <Card className="p-6">
          <div className="flex items-center justify-between mb-2">
            <span className="text-gray-600">연속 학습</span>
            <Award className="text-orange-500" size={20} />
          </div>
          <p className="text-3xl font-bold text-gray-900">
            {stats?.currentStreak || 0}일
          </p>
          <p className="text-sm text-gray-500 mt-1">연속 학습 중!</p>
        </Card>
      </div>

      {/* Recent Analyses */}
      <Card>
        <CardHeader>
          <div className="flex items-center justify-between">
            <h2 className="text-xl font-semibold text-gray-900">최근 차트 분석</h2>
            <Link to="/analysis">
              <Button variant="ghost" size="sm">전체 보기</Button>
            </Link>
          </div>
        </CardHeader>
        <CardBody>
          {recentAnalyses.length === 0 ? (
            <div className="text-center py-12">
              <LineChart className="mx-auto mb-4 text-gray-400" size={48} />
              <p className="text-gray-600 mb-4">아직 분석한 차트가 없습니다.</p>
              <Link to="/analysis">
                <Button>첫 차트 분석하기</Button>
              </Link>
            </div>
          ) : (
            <div className="space-y-3">
              {recentAnalyses.map((analysis) => (
                <Link
                  key={analysis.id}
                  to={`/analysis/${analysis.id}`}
                  className="block"
                >
                  <div className="flex items-center gap-4 p-4 rounded-lg hover:bg-gray-50 transition-colors">
                    <img
                      src={`http://localhost:8080${analysis.imagePath}`}
                      alt="Chart"
                      className="w-20 h-20 object-cover rounded-lg"
                    />
                    <div className="flex-1 min-w-0">
                      <p className="text-sm text-gray-900 line-clamp-2">
                        {analysis.analysis}
                      </p>
                      <p className="text-xs text-gray-500 mt-1">
                        {new Date(analysis.createdAt).toLocaleString('ko-KR')}
                      </p>
                    </div>
                    <Badge variant="primary">완료</Badge>
                  </div>
                </Link>
              ))}
            </div>
          )}
        </CardBody>
      </Card>
    </div>
  );
};

export default DashboardPage;
