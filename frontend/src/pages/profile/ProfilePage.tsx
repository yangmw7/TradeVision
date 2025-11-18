import React, { useEffect, useState } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { Card, CardHeader, CardBody, Loading, Badge } from '../../components/ui';
import { learningApi } from '../../api';
import type { LearningStats } from '../../types';
import {
  User,
  Mail,
  Calendar,
  Award,
  BookOpen,
  Clock,
  TrendingUp,
  Target,
} from 'lucide-react';
import toast from 'react-hot-toast';

const ProfilePage: React.FC = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState<LearningStats | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      const data = await learningApi.getStats();
      setStats(data);
    } catch (error: any) {
      toast.error('통계를 불러오지 못했습니다.');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <Loading size="lg" text="프로필을 불러오는 중..." />
      </div>
    );
  }

  const completionRate = stats
    ? Math.round((stats.completedContents / stats.totalContents) * 100)
    : 0;

  const moduleCompletionRate = stats
    ? Math.round((stats.completedModules / stats.totalModules) * 100)
    : 0;

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-gray-900 mb-2">프로필</h1>
        <p className="text-gray-600">내 정보와 학습 통계를 확인하세요.</p>
      </div>

      {/* User Info */}
      <Card className="p-8">
        <div className="flex items-center gap-6">
          <div className="w-20 h-20 bg-gradient-to-br from-primary-500 to-primary-600 rounded-full flex items-center justify-center">
            <User className="text-white" size={40} />
          </div>
          <div className="flex-1">
            <h2 className="text-2xl font-bold text-gray-900 mb-2">
              {user?.username}
            </h2>
            <div className="space-y-1 text-gray-600">
              <div className="flex items-center gap-2">
                <Mail size={16} />
                <span>{user?.email}</span>
              </div>
              <div className="flex items-center gap-2">
                <Calendar size={16} />
                <span>
                  가입일:{' '}
                  {user?.createdAt
                    ? new Date(user.createdAt).toLocaleDateString('ko-KR')
                    : '-'}
                </span>
              </div>
            </div>
          </div>
        </div>
      </Card>

      {/* Learning Statistics */}
      <div>
        <h2 className="text-xl font-semibold text-gray-900 mb-4">학습 통계</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <Card className="p-6">
            <div className="flex items-center gap-3 mb-3">
              <div className="w-10 h-10 bg-primary-100 rounded-lg flex items-center justify-center">
                <BookOpen className="text-primary-600" size={20} />
              </div>
              <span className="text-gray-600">완료한 콘텐츠</span>
            </div>
            <p className="text-3xl font-bold text-gray-900">
              {stats?.completedContents || 0}
            </p>
            <p className="text-sm text-gray-500 mt-1">
              전체 {stats?.totalContents || 0}개
            </p>
          </Card>

          <Card className="p-6">
            <div className="flex items-center gap-3 mb-3">
              <div className="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center">
                <Target className="text-green-600" size={20} />
              </div>
              <span className="text-gray-600">완료한 모듈</span>
            </div>
            <p className="text-3xl font-bold text-gray-900">
              {stats?.completedModules || 0}
            </p>
            <p className="text-sm text-gray-500 mt-1">
              전체 {stats?.totalModules || 0}개
            </p>
          </Card>

          <Card className="p-6">
            <div className="flex items-center gap-3 mb-3">
              <div className="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
                <Clock className="text-blue-600" size={20} />
              </div>
              <span className="text-gray-600">총 학습 시간</span>
            </div>
            <p className="text-3xl font-bold text-gray-900">
              {stats?.totalLearningMinutes || 0}
            </p>
            <p className="text-sm text-gray-500 mt-1">분</p>
          </Card>

          <Card className="p-6">
            <div className="flex items-center gap-3 mb-3">
              <div className="w-10 h-10 bg-orange-100 rounded-lg flex items-center justify-center">
                <Award className="text-orange-600" size={20} />
              </div>
              <span className="text-gray-600">연속 학습</span>
            </div>
            <p className="text-3xl font-bold text-gray-900">
              {stats?.currentStreak || 0}
            </p>
            <p className="text-sm text-gray-500 mt-1">일 연속</p>
          </Card>
        </div>
      </div>

      {/* Progress Overview */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <Card>
          <CardHeader>
            <h3 className="text-lg font-semibold text-gray-900">콘텐츠 진행률</h3>
          </CardHeader>
          <CardBody>
            <div className="space-y-4">
              <div className="text-center py-6">
                <div className="relative inline-flex items-center justify-center">
                  <svg className="transform -rotate-90 w-32 h-32">
                    <circle
                      cx="64"
                      cy="64"
                      r="56"
                      stroke="currentColor"
                      strokeWidth="8"
                      fill="transparent"
                      className="text-gray-200"
                    />
                    <circle
                      cx="64"
                      cy="64"
                      r="56"
                      stroke="currentColor"
                      strokeWidth="8"
                      fill="transparent"
                      strokeDasharray={`${2 * Math.PI * 56}`}
                      strokeDashoffset={`${
                        2 * Math.PI * 56 * (1 - completionRate / 100)
                      }`}
                      className="text-primary-500 transition-all duration-500"
                    />
                  </svg>
                  <span className="absolute text-2xl font-bold text-gray-900">
                    {completionRate}%
                  </span>
                </div>
              </div>
              <div className="text-center">
                <p className="text-gray-600 mb-1">
                  {stats?.completedContents || 0} / {stats?.totalContents || 0}{' '}
                  완료
                </p>
                <Badge variant="primary">
                  <TrendingUp size={14} className="mr-1" />
                  꾸준히 학습 중!
                </Badge>
              </div>
            </div>
          </CardBody>
        </Card>

        <Card>
          <CardHeader>
            <h3 className="text-lg font-semibold text-gray-900">모듈 진행률</h3>
          </CardHeader>
          <CardBody>
            <div className="space-y-4">
              <div className="text-center py-6">
                <div className="relative inline-flex items-center justify-center">
                  <svg className="transform -rotate-90 w-32 h-32">
                    <circle
                      cx="64"
                      cy="64"
                      r="56"
                      stroke="currentColor"
                      strokeWidth="8"
                      fill="transparent"
                      className="text-gray-200"
                    />
                    <circle
                      cx="64"
                      cy="64"
                      r="56"
                      stroke="currentColor"
                      strokeWidth="8"
                      fill="transparent"
                      strokeDasharray={`${2 * Math.PI * 56}`}
                      strokeDashoffset={`${
                        2 * Math.PI * 56 * (1 - moduleCompletionRate / 100)
                      }`}
                      className="text-green-500 transition-all duration-500"
                    />
                  </svg>
                  <span className="absolute text-2xl font-bold text-gray-900">
                    {moduleCompletionRate}%
                  </span>
                </div>
              </div>
              <div className="text-center">
                <p className="text-gray-600 mb-1">
                  {stats?.completedModules || 0} / {stats?.totalModules || 0}{' '}
                  완료
                </p>
                <Badge variant="success">
                  <Award size={14} className="mr-1" />
                  계속 진행하세요!
                </Badge>
              </div>
            </div>
          </CardBody>
        </Card>
      </div>

      {/* Achievements */}
      <Card>
        <CardHeader>
          <h3 className="text-lg font-semibold text-gray-900">달성 배지</h3>
        </CardHeader>
        <CardBody>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            {completionRate >= 25 && (
              <div className="text-center p-4 bg-gradient-to-br from-primary-50 to-white rounded-lg">
                <div className="w-16 h-16 bg-primary-500 rounded-full flex items-center justify-center mx-auto mb-2">
                  <Award className="text-white" size={32} />
                </div>
                <p className="font-medium text-gray-900">입문자</p>
                <p className="text-xs text-gray-600">25% 완료</p>
              </div>
            )}

            {completionRate >= 50 && (
              <div className="text-center p-4 bg-gradient-to-br from-green-50 to-white rounded-lg">
                <div className="w-16 h-16 bg-green-500 rounded-full flex items-center justify-center mx-auto mb-2">
                  <Award className="text-white" size={32} />
                </div>
                <p className="font-medium text-gray-900">중급자</p>
                <p className="text-xs text-gray-600">50% 완료</p>
              </div>
            )}

            {completionRate >= 75 && (
              <div className="text-center p-4 bg-gradient-to-br from-blue-50 to-white rounded-lg">
                <div className="w-16 h-16 bg-blue-500 rounded-full flex items-center justify-center mx-auto mb-2">
                  <Award className="text-white" size={32} />
                </div>
                <p className="font-medium text-gray-900">고급자</p>
                <p className="text-xs text-gray-600">75% 완료</p>
              </div>
            )}

            {completionRate >= 100 && (
              <div className="text-center p-4 bg-gradient-to-br from-orange-50 to-white rounded-lg">
                <div className="w-16 h-16 bg-gradient-to-br from-orange-500 to-orange-600 rounded-full flex items-center justify-center mx-auto mb-2">
                  <Award className="text-white" size={32} />
                </div>
                <p className="font-medium text-gray-900">마스터</p>
                <p className="text-xs text-gray-600">100% 완료</p>
              </div>
            )}

            {(stats?.currentStreak || 0) >= 7 && (
              <div className="text-center p-4 bg-gradient-to-br from-purple-50 to-white rounded-lg">
                <div className="w-16 h-16 bg-purple-500 rounded-full flex items-center justify-center mx-auto mb-2">
                  <Award className="text-white" size={32} />
                </div>
                <p className="font-medium text-gray-900">꾸준함</p>
                <p className="text-xs text-gray-600">7일 연속</p>
              </div>
            )}
          </div>

          {completionRate < 25 && (stats?.currentStreak || 0) < 7 && (
            <div className="text-center py-8 text-gray-600">
              <p>아직 획득한 배지가 없습니다.</p>
              <p className="text-sm mt-1">학습을 계속하여 배지를 획득하세요!</p>
            </div>
          )}
        </CardBody>
      </Card>
    </div>
  );
};

export default ProfilePage;
