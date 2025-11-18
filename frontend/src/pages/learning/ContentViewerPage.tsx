import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Button, Loading } from '../../components/ui';
import { learningApi } from '../../api';
import type { LearningContent } from '../../types';
import {
  ArrowLeft,
  Heart,
  Bookmark,
  BookmarkCheck,
  CheckCircle2,
  Clock,
} from 'lucide-react';
import toast from 'react-hot-toast';

const ContentViewerPage: React.FC = () => {
  const { contentId } = useParams<{ contentId: string }>();
  const navigate = useNavigate();
  const [content, setContent] = useState<LearningContent | null>(null);
  const [loading, setLoading] = useState(true);
  const [updatingProgress, setUpdatingProgress] = useState(false);

  useEffect(() => {
    fetchContent();
  }, [contentId]);

  const fetchContent = async () => {
    if (!contentId) return;

    try {
      const data = await learningApi.getContentById(Number(contentId));
      setContent(data);
    } catch (error: any) {
      toast.error('콘텐츠를 불러오지 못했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleLike = async () => {
    if (!content) return;

    try {
      if (content.isLiked) {
        await learningApi.unlikeContent(content.id);
        toast.success('좋아요가 취소되었습니다.');
      } else {
        await learningApi.likeContent(content.id);
        toast.success('좋아요를 눌렀습니다!');
      }

      setContent({
        ...content,
        isLiked: !content.isLiked,
        likeCount: content.isLiked ? content.likeCount - 1 : content.likeCount + 1,
      });
    } catch (error: any) {
      toast.error('좋아요 처리에 실패했습니다.');
    }
  };

  const handleBookmark = async () => {
    if (!content) return;

    try {
      if (content.isBookmarked) {
        await learningApi.removeBookmark(content.id);
        toast.success('북마크가 제거되었습니다.');
      } else {
        await learningApi.bookmarkContent(content.id);
        toast.success('북마크에 추가되었습니다!');
      }

      setContent({
        ...content,
        isBookmarked: !content.isBookmarked,
      });
    } catch (error: any) {
      toast.error('북마크 처리에 실패했습니다.');
    }
  };

  const handleMarkAsComplete = async () => {
    if (!content) return;

    setUpdatingProgress(true);
    try {
      await learningApi.updateProgress(content.id, {
        completed: true,
        progressPercentage: 100,
      });

      setContent({
        ...content,
        isCompleted: true,
      });

      toast.success('학습을 완료했습니다! 🎉');
    } catch (error: any) {
      toast.error('완료 처리에 실패했습니다.');
    } finally {
      setUpdatingProgress(false);
    }
  };

  const handleBackToModule = () => {
    if (content) {
      navigate(`/learning/modules/${content.moduleId}`);
    } else {
      navigate('/learning');
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <Loading size="lg" text="콘텐츠를 불러오는 중..." />
      </div>
    );
  }

  if (!content) {
    return (
      <div className="text-center py-12">
        <p className="text-gray-600">콘텐츠를 찾을 수 없습니다.</p>
        <Button onClick={() => navigate('/learning')} className="mt-4">
          목록으로 돌아가기
        </Button>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      {/* Back Button */}
      <Button variant="ghost" onClick={handleBackToModule}>
        <ArrowLeft size={20} />
        모듈로 돌아가기
      </Button>

      {/* Content Header */}
      <Card className="p-8">
        <div className="flex items-start justify-between mb-4">
          <div className="flex-1">
            <h1 className="text-3xl font-bold text-gray-900 mb-3">
              {content.title}
            </h1>
            <div className="flex items-center gap-4 text-sm text-gray-600">
              <div className="flex items-center gap-1">
                <Clock size={16} />
                {content.estimatedMinutes}분
              </div>
              <div className="flex items-center gap-1">
                <Heart
                  size={16}
                  className={content.isLiked ? 'fill-red-500 text-red-500' : ''}
                />
                {content.likeCount}
              </div>
            </div>
          </div>
          {content.isCompleted && (
            <div className="flex items-center gap-2 text-green-600 bg-green-50 px-4 py-2 rounded-lg">
              <CheckCircle2 size={20} />
              <span className="font-medium">완료</span>
            </div>
          )}
        </div>
      </Card>

      {/* Content Body */}
      <Card className="p-8">
        <div
          className="prose prose-lg max-w-none"
          dangerouslySetInnerHTML={{ __html: content.content }}
        />
      </Card>

      {/* Actions */}
      <Card className="p-6">
        <div className="flex items-center justify-between flex-wrap gap-4">
          <div className="flex items-center gap-2">
            <Button
              variant={content.isLiked ? 'primary' : 'outline'}
              onClick={handleLike}
            >
              <Heart
                size={20}
                className={content.isLiked ? 'fill-current' : ''}
              />
              {content.isLiked ? '좋아요 취소' : '좋아요'}
            </Button>

            <Button
              variant={content.isBookmarked ? 'secondary' : 'outline'}
              onClick={handleBookmark}
            >
              {content.isBookmarked ? (
                <>
                  <BookmarkCheck size={20} />
                  북마크됨
                </>
              ) : (
                <>
                  <Bookmark size={20} />
                  북마크
                </>
              )}
            </Button>
          </div>

          {!content.isCompleted && (
            <Button
              variant="primary"
              size="lg"
              onClick={handleMarkAsComplete}
              loading={updatingProgress}
            >
              <CheckCircle2 size={20} />
              학습 완료하기
            </Button>
          )}
        </div>
      </Card>
    </div>
  );
};

export default ContentViewerPage;
