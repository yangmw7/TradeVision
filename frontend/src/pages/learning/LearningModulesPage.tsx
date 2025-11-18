import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, Button, Loading, Badge } from '../../components/ui';
import { learningApi } from '../../api';
import type { LearningModule } from '../../types';
import { GraduationCap, BookOpen, CheckCircle2 } from 'lucide-react';
import toast from 'react-hot-toast';

const LearningModulesPage: React.FC = () => {
  const [modules, setModules] = useState<LearningModule[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedDifficulty, setSelectedDifficulty] = useState<string>('ALL');
  const navigate = useNavigate();

  const difficulties = ['ALL', 'BEGINNER', 'INTERMEDIATE', 'ADVANCED'];
  const difficultyLabels: Record<string, string> = {
    ALL: '전체',
    BEGINNER: '초급',
    INTERMEDIATE: '중급',
    ADVANCED: '고급',
  };

  useEffect(() => {
    fetchModules();
  }, [selectedDifficulty]);

  const fetchModules = async () => {
    setLoading(true);
    try {
      const data =
        selectedDifficulty === 'ALL'
          ? await learningApi.getModules()
          : await learningApi.getModulesByDifficulty(selectedDifficulty);
      setModules(data);
    } catch (error: any) {
      toast.error('학습 모듈을 불러오지 못했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const getDifficultyVariant = (
    difficulty: string
  ): 'success' | 'warning' | 'danger' => {
    switch (difficulty) {
      case 'BEGINNER':
        return 'success';
      case 'INTERMEDIATE':
        return 'warning';
      case 'ADVANCED':
        return 'danger';
      default:
        return 'success';
    }
  };

  const getProgressPercentage = (module: LearningModule) => {
    if (!module.completedContents || !module.totalContents) return 0;
    return Math.round((module.completedContents / module.totalContents) * 100);
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-gray-900 mb-2">학습 콘텐츠</h1>
        <p className="text-gray-600">체계적인 학습으로 트레이딩 실력을 키워보세요.</p>
      </div>

      {/* Difficulty Filter */}
      <Card className="p-6">
        <label className="block text-sm font-medium text-gray-700 mb-3">
          난이도 선택
        </label>
        <div className="flex flex-wrap gap-2">
          {difficulties.map((difficulty) => (
            <Button
              key={difficulty}
              variant={selectedDifficulty === difficulty ? 'primary' : 'outline'}
              size="sm"
              onClick={() => setSelectedDifficulty(difficulty)}
            >
              {difficultyLabels[difficulty]}
            </Button>
          ))}
        </div>
      </Card>

      {/* Modules Grid */}
      {loading ? (
        <div className="flex items-center justify-center h-96">
          <Loading size="lg" text="학습 모듈을 불러오는 중..." />
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {modules.map((module) => {
            const progress = getProgressPercentage(module);
            const isCompleted = progress === 100;

            return (
              <Card key={module.id} hover className="flex flex-col">
                <div className="p-6 flex-1">
                  {/* Header */}
                  <div className="flex items-start justify-between mb-4">
                    <div className="w-12 h-12 bg-primary-100 rounded-lg flex items-center justify-center">
                      <GraduationCap className="text-primary-600" size={24} />
                    </div>
                    {isCompleted && (
                      <CheckCircle2 className="text-green-500" size={24} />
                    )}
                  </div>

                  {/* Title and Description */}
                  <h3 className="text-lg font-semibold text-gray-900 mb-2">
                    {module.title}
                  </h3>
                  <p className="text-sm text-gray-600 line-clamp-3 mb-4">
                    {module.description}
                  </p>

                  {/* Badges */}
                  <div className="flex items-center gap-2 mb-4">
                    <Badge variant={getDifficultyVariant(module.difficulty)}>
                      {difficultyLabels[module.difficulty]}
                    </Badge>
                    <Badge variant="gray">
                      <BookOpen size={12} className="mr-1" />
                      {module.totalContents}개 콘텐츠
                    </Badge>
                  </div>

                  {/* Progress */}
                  <div className="space-y-2">
                    <div className="flex items-center justify-between text-sm">
                      <span className="text-gray-600">학습 진행률</span>
                      <span className="font-medium text-primary-600">
                        {progress}%
                      </span>
                    </div>
                    <div className="w-full bg-gray-200 rounded-full h-2">
                      <div
                        className="bg-primary-500 h-2 rounded-full transition-all duration-300"
                        style={{ width: `${progress}%` }}
                      />
                    </div>
                    <p className="text-xs text-gray-500">
                      {module.completedContents || 0} / {module.totalContents} 완료
                    </p>
                  </div>
                </div>

                {/* Footer */}
                <div className="p-6 pt-0">
                  <Button
                    variant={isCompleted ? 'secondary' : 'primary'}
                    fullWidth
                    onClick={() => navigate(`/learning/modules/${module.id}`)}
                  >
                    {isCompleted ? '다시 학습하기' : '학습 시작'}
                  </Button>
                </div>
              </Card>
            );
          })}
        </div>
      )}

      {!loading && modules.length === 0 && (
        <div className="text-center py-12">
          <GraduationCap className="mx-auto mb-4 text-gray-400" size={48} />
          <p className="text-gray-600">학습 모듈이 없습니다.</p>
        </div>
      )}
    </div>
  );
};

export default LearningModulesPage;
