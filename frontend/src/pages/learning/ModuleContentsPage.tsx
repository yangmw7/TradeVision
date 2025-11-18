import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Button, Loading, Badge } from '../../components/ui';
import { learningApi } from '../../api';
import type { LearningContent, LearningModule } from '../../types';
import { FileText, Video, HelpCircle, CheckCircle2, Clock, ArrowLeft } from 'lucide-react';
import toast from 'react-hot-toast';

const ModuleContentsPage: React.FC = () => {
  const { moduleId } = useParams<{ moduleId: string }>();
  const navigate = useNavigate();
  const [module, setModule] = useState<LearningModule | null>(null);
  const [contents, setContents] = useState<LearningContent[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
  }, [moduleId]);

  const fetchData = async () => {
    if (!moduleId) return;

    try {
      const [moduleData, contentsData] = await Promise.all([
        learningApi.getModuleById(Number(moduleId)),
        learningApi.getContentsByModule(Number(moduleId)),
      ]);
      setModule(moduleData);
      setContents(contentsData);
    } catch (error: any) {
      toast.error('콘텐츠를 불러오지 못했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const getContentIcon = (type: string) => {
    switch (type) {
      case 'VIDEO':
        return <Video size={20} />;
      case 'QUIZ':
        return <HelpCircle size={20} />;
      default:
        return <FileText size={20} />;
    }
  };

  const getContentTypeLabel = (type: string) => {
    switch (type) {
      case 'VIDEO':
        return '동영상';
      case 'QUIZ':
        return '퀴즈';
      default:
        return '텍스트';
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <Loading size="lg" text="콘텐츠를 불러오는 중..." />
      </div>
    );
  }

  if (!module) {
    return (
      <div className="text-center py-12">
        <p className="text-gray-600">모듈을 찾을 수 없습니다.</p>
        <Button onClick={() => navigate('/learning')} className="mt-4">
          목록으로 돌아가기
        </Button>
      </div>
    );
  }

  const completedCount = contents.filter((c) => c.isCompleted).length;
  const progress = Math.round((completedCount / contents.length) * 100);

  return (
    <div className="space-y-6">
      {/* Back Button */}
      <Button variant="ghost" onClick={() => navigate('/learning')}>
        <ArrowLeft size={20} />
        목록으로
      </Button>

      {/* Module Header */}
      <Card className="p-8">
        <div className="flex items-start justify-between mb-4">
          <div className="flex-1">
            <h1 className="text-3xl font-bold text-gray-900 mb-2">
              {module.title}
            </h1>
            <p className="text-gray-600 mb-4">{module.description}</p>

            <div className="flex items-center gap-2">
              <Badge variant="primary">
                {module.difficulty === 'BEGINNER'
                  ? '초급'
                  : module.difficulty === 'INTERMEDIATE'
                  ? '중급'
                  : '고급'}
              </Badge>
              <Badge variant="gray">{contents.length}개 콘텐츠</Badge>
            </div>
          </div>
        </div>

        {/* Progress */}
        <div className="space-y-2">
          <div className="flex items-center justify-between text-sm">
            <span className="text-gray-600">전체 진행률</span>
            <span className="font-medium text-primary-600">{progress}%</span>
          </div>
          <div className="w-full bg-gray-200 rounded-full h-3">
            <div
              className="bg-primary-500 h-3 rounded-full transition-all duration-300"
              style={{ width: `${progress}%` }}
            />
          </div>
          <p className="text-sm text-gray-500">
            {completedCount} / {contents.length} 완료
          </p>
        </div>
      </Card>

      {/* Contents List */}
      <div className="space-y-4">
        {contents.map((content, index) => (
          <Card
            key={content.id}
            hover
            onClick={() => navigate(`/learning/contents/${content.id}`)}
            className="p-6 cursor-pointer"
          >
            <div className="flex items-center gap-4">
              {/* Index */}
              <div className="w-10 h-10 bg-gray-100 rounded-lg flex items-center justify-center flex-shrink-0">
                <span className="font-semibold text-gray-700">{index + 1}</span>
              </div>

              {/* Content Info */}
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2 mb-1">
                  <h3 className="font-semibold text-gray-900">{content.title}</h3>
                  {content.isCompleted && (
                    <CheckCircle2 className="text-green-500 flex-shrink-0" size={20} />
                  )}
                </div>
                <div className="flex items-center gap-3 text-sm text-gray-500">
                  <div className="flex items-center gap-1">
                    {getContentIcon(content.contentType)}
                    {getContentTypeLabel(content.contentType)}
                  </div>
                  <div className="flex items-center gap-1">
                    <Clock size={16} />
                    {content.estimatedMinutes}분
                  </div>
                </div>
              </div>

              {/* Action */}
              <div>
                {content.isCompleted ? (
                  <Badge variant="success">완료</Badge>
                ) : (
                  <Badge variant="gray">학습하기</Badge>
                )}
              </div>
            </div>
          </Card>
        ))}
      </div>

      {contents.length === 0 && (
        <div className="text-center py-12">
          <FileText className="mx-auto mb-4 text-gray-400" size={48} />
          <p className="text-gray-600">콘텐츠가 없습니다.</p>
        </div>
      )}
    </div>
  );
};

export default ModuleContentsPage;
