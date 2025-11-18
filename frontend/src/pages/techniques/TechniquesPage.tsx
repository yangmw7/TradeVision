import React, { useEffect, useState } from 'react';
import { Card, Button, Loading, Badge, Input, Modal } from '../../components/ui';
import { techniquesApi } from '../../api';
import type { TradingTechnique } from '../../types';
import { Search, Bookmark, BookmarkCheck, Eye, TrendingUp } from 'lucide-react';
import toast from 'react-hot-toast';

const TechniquesPage: React.FC = () => {
  const [techniques, setTechniques] = useState<TradingTechnique[]>([]);
  const [filteredTechniques, setFilteredTechniques] = useState<TradingTechnique[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<string>('ALL');
  const [selectedDifficulty, setSelectedDifficulty] = useState<string>('ALL');
  const [selectedTechnique, setSelectedTechnique] = useState<TradingTechnique | null>(null);
  const [modalOpen, setModalOpen] = useState(false);

  const categories = ['ALL', 'TREND_FOLLOWING', 'REVERSAL', 'BREAKOUT', 'SCALPING'];
  const difficulties = ['ALL', 'BEGINNER', 'INTERMEDIATE', 'ADVANCED'];

  const categoryLabels: Record<string, string> = {
    ALL: '전체',
    TREND_FOLLOWING: '추세 추종',
    REVERSAL: '반전',
    BREAKOUT: '돌파',
    SCALPING: '스캘핑',
  };

  const difficultyLabels: Record<string, string> = {
    ALL: '전체',
    BEGINNER: '초급',
    INTERMEDIATE: '중급',
    ADVANCED: '고급',
  };

  useEffect(() => {
    fetchTechniques();
  }, []);

  useEffect(() => {
    filterTechniques();
  }, [techniques, searchQuery, selectedCategory, selectedDifficulty]);

  const fetchTechniques = async () => {
    try {
      const data = await techniquesApi.getAll();
      setTechniques(data);
    } catch (error: any) {
      toast.error('매매 기법을 불러오지 못했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const filterTechniques = () => {
    let filtered = techniques;

    if (selectedCategory !== 'ALL') {
      filtered = filtered.filter((t) => t.category === selectedCategory);
    }

    if (selectedDifficulty !== 'ALL') {
      filtered = filtered.filter((t) => t.difficulty === selectedDifficulty);
    }

    if (searchQuery.trim()) {
      const query = searchQuery.toLowerCase();
      filtered = filtered.filter(
        (t) =>
          t.title.toLowerCase().includes(query) ||
          t.description.toLowerCase().includes(query)
      );
    }

    setFilteredTechniques(filtered);
  };

  const handleBookmark = async (technique: TradingTechnique) => {
    try {
      if (technique.isBookmarked) {
        await techniquesApi.removeBookmark(technique.id);
        toast.success('북마크가 제거되었습니다.');
      } else {
        await techniquesApi.bookmark(technique.id);
        toast.success('북마크에 추가되었습니다.');
      }

      // Update local state
      setTechniques((prev) =>
        prev.map((t) =>
          t.id === technique.id ? { ...t, isBookmarked: !t.isBookmarked } : t
        )
      );

      if (selectedTechnique?.id === technique.id) {
        setSelectedTechnique({
          ...selectedTechnique,
          isBookmarked: !selectedTechnique.isBookmarked,
        });
      }
    } catch (error: any) {
      toast.error('북마크 처리에 실패했습니다.');
    }
  };

  const openModal = (technique: TradingTechnique) => {
    setSelectedTechnique(technique);
    setModalOpen(true);
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
        return 'info';
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <Loading size="lg" text="매매 기법을 불러오는 중..." />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-gray-900 mb-2">매매 기법</h1>
        <p className="text-gray-600">다양한 트레이딩 기법을 학습하세요.</p>
      </div>

      {/* Search and Filters */}
      <Card className="p-6">
        <div className="space-y-4">
          {/* Search */}
          <div className="relative">
            <Search
              className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"
              size={20}
            />
            <Input
              type="text"
              placeholder="기법 검색..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-10"
              fullWidth
            />
          </div>

          {/* Category Filter */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              카테고리
            </label>
            <div className="flex flex-wrap gap-2">
              {categories.map((category) => (
                <Button
                  key={category}
                  variant={selectedCategory === category ? 'primary' : 'outline'}
                  size="sm"
                  onClick={() => setSelectedCategory(category)}
                >
                  {categoryLabels[category]}
                </Button>
              ))}
            </div>
          </div>

          {/* Difficulty Filter */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              난이도
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
          </div>
        </div>
      </Card>

      {/* Results */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {filteredTechniques.map((technique) => (
          <Card key={technique.id} hover className="flex flex-col">
            <div className="p-6 flex-1">
              <div className="flex items-start justify-between mb-3">
                <div className="flex-1">
                  <h3 className="text-lg font-semibold text-gray-900 mb-2">
                    {technique.title}
                  </h3>
                  <p className="text-sm text-gray-600 line-clamp-2 mb-3">
                    {technique.description}
                  </p>
                </div>
                <button
                  onClick={() => handleBookmark(technique)}
                  className="text-gray-400 hover:text-primary-500 transition-colors ml-2"
                >
                  {technique.isBookmarked ? (
                    <BookmarkCheck className="text-primary-500" size={20} />
                  ) : (
                    <Bookmark size={20} />
                  )}
                </button>
              </div>

              <div className="flex items-center gap-2 mb-4">
                <Badge variant={getDifficultyVariant(technique.difficulty)}>
                  {difficultyLabels[technique.difficulty]}
                </Badge>
                <Badge variant="gray">{categoryLabels[technique.category]}</Badge>
              </div>

              <div className="flex items-center gap-4 text-sm text-gray-500">
                <div className="flex items-center gap-1">
                  <Eye size={16} />
                  {technique.viewCount}
                </div>
                <div className="flex items-center gap-1">
                  <Bookmark size={16} />
                  {technique.bookmarkCount}
                </div>
              </div>
            </div>

            <div className="p-6 pt-0">
              <Button
                variant="outline"
                fullWidth
                onClick={() => openModal(technique)}
              >
                자세히 보기
              </Button>
            </div>
          </Card>
        ))}
      </div>

      {filteredTechniques.length === 0 && (
        <div className="text-center py-12">
          <TrendingUp className="mx-auto mb-4 text-gray-400" size={48} />
          <p className="text-gray-600">검색 결과가 없습니다.</p>
        </div>
      )}

      {/* Detail Modal */}
      {selectedTechnique && (
        <Modal
          isOpen={modalOpen}
          onClose={() => setModalOpen(false)}
          title={selectedTechnique.title}
          size="lg"
        >
          <div className="space-y-4">
            <div className="flex items-center gap-2">
              <Badge variant={getDifficultyVariant(selectedTechnique.difficulty)}>
                {difficultyLabels[selectedTechnique.difficulty]}
              </Badge>
              <Badge variant="gray">
                {categoryLabels[selectedTechnique.category]}
              </Badge>
            </div>

            <p className="text-gray-600">{selectedTechnique.description}</p>

            <div className="bg-gray-50 rounded-lg p-4">
              <h4 className="font-semibold text-gray-900 mb-2">상세 설명</h4>
              <div
                className="text-gray-700 whitespace-pre-wrap leading-relaxed"
                dangerouslySetInnerHTML={{ __html: selectedTechnique.content }}
              />
            </div>

            <div className="flex items-center gap-4 text-sm text-gray-500">
              <div className="flex items-center gap-1">
                <Eye size={16} />
                조회수 {selectedTechnique.viewCount}
              </div>
              <div className="flex items-center gap-1">
                <Bookmark size={16} />
                북마크 {selectedTechnique.bookmarkCount}
              </div>
            </div>

            <div className="flex gap-2">
              <Button
                variant={selectedTechnique.isBookmarked ? 'secondary' : 'primary'}
                onClick={() => handleBookmark(selectedTechnique)}
                fullWidth
              >
                {selectedTechnique.isBookmarked ? (
                  <>
                    <BookmarkCheck size={20} />
                    북마크 제거
                  </>
                ) : (
                  <>
                    <Bookmark size={20} />
                    북마크 추가
                  </>
                )}
              </Button>
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
};

export default TechniquesPage;
