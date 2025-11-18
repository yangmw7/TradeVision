import React, { useState, useRef, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Card, CardHeader, CardBody, Button, Loading, Badge } from '../../components/ui';
import { chartsApi } from '../../api';
import type { ChartAnalysis, AnalysisHistory } from '../../types';
import { Upload, FileImage, Trash2, Sparkles } from 'lucide-react';
import toast from 'react-hot-toast';

const AnalysisPage: React.FC = () => {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);
  const [uploading, setUploading] = useState(false);
  const [analyzing, setAnalyzing] = useState(false);
  const [currentAnalysis, setCurrentAnalysis] = useState<ChartAnalysis | null>(null);
  const [history, setHistory] = useState<AnalysisHistory[]>([]);
  const [loadingHistory, setLoadingHistory] = useState(true);
  const fileInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    fetchHistory();
  }, []);

  const fetchHistory = async () => {
    try {
      const data = await chartsApi.getHistory();
      setHistory(data);
    } catch (error: any) {
      toast.error('분석 내역을 불러오지 못했습니다.');
    } finally {
      setLoadingHistory(false);
    }
  };

  const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      if (!file.type.startsWith('image/')) {
        toast.error('이미지 파일만 업로드 가능합니다.');
        return;
      }

      if (file.size > 10 * 1024 * 1024) {
        toast.error('파일 크기는 10MB를 초과할 수 없습니다.');
        return;
      }

      setSelectedFile(file);
      setPreviewUrl(URL.createObjectURL(file));
      setCurrentAnalysis(null);
    }
  };

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    const file = e.dataTransfer.files[0];
    if (file) {
      const fakeEvent = {
        target: { files: [file] },
      } as React.ChangeEvent<HTMLInputElement>;
      handleFileSelect(fakeEvent);
    }
  };

  const handleUploadAndAnalyze = async () => {
    if (!selectedFile) return;

    setUploading(true);
    try {
      // Upload the file
      const uploadedChart = await chartsApi.uploadChart(selectedFile);
      toast.success('차트가 업로드되었습니다!');

      // Analyze the chart
      setAnalyzing(true);
      const analysis = await chartsApi.analyzeChart(uploadedChart.id);
      setCurrentAnalysis(analysis);
      toast.success('분석이 완료되었습니다!');

      // Refresh history
      fetchHistory();
    } catch (error: any) {
      toast.error(error.message || '분석에 실패했습니다.');
    } finally {
      setUploading(false);
      setAnalyzing(false);
    }
  };

  const handleReset = () => {
    setSelectedFile(null);
    setPreviewUrl(null);
    setCurrentAnalysis(null);
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-gray-900 mb-2">차트 분석</h1>
        <p className="text-gray-600">AI가 차트를 분석하여 인사이트를 제공합니다.</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Upload Section */}
        <Card>
          <CardHeader>
            <h2 className="text-xl font-semibold text-gray-900">차트 업로드</h2>
          </CardHeader>
          <CardBody className="space-y-4">
            {!previewUrl ? (
              <div
                onDragOver={handleDragOver}
                onDrop={handleDrop}
                onClick={() => fileInputRef.current?.click()}
                className="border-2 border-dashed border-gray-300 rounded-lg p-12 text-center cursor-pointer hover:border-primary-500 hover:bg-primary-50 transition-all"
              >
                <Upload className="mx-auto mb-4 text-gray-400" size={48} />
                <p className="text-gray-900 font-medium mb-1">
                  클릭하거나 파일을 드래그하세요
                </p>
                <p className="text-sm text-gray-500">PNG, JPG, JPEG (최대 10MB)</p>
                <input
                  ref={fileInputRef}
                  type="file"
                  accept="image/*"
                  onChange={handleFileSelect}
                  className="hidden"
                />
              </div>
            ) : (
              <div className="space-y-4">
                <div className="relative">
                  <img
                    src={previewUrl}
                    alt="Preview"
                    className="w-full rounded-lg"
                  />
                  <Button
                    variant="danger"
                    size="sm"
                    onClick={handleReset}
                    className="absolute top-2 right-2"
                  >
                    <Trash2 size={16} />
                    제거
                  </Button>
                </div>

                {!currentAnalysis && (
                  <Button
                    fullWidth
                    size="lg"
                    onClick={handleUploadAndAnalyze}
                    loading={uploading || analyzing}
                  >
                    <Sparkles size={20} />
                    {uploading
                      ? '업로드 중...'
                      : analyzing
                      ? '분석 중...'
                      : 'AI 분석 시작'}
                  </Button>
                )}
              </div>
            )}
          </CardBody>
        </Card>

        {/* Analysis Result */}
        <Card>
          <CardHeader>
            <h2 className="text-xl font-semibold text-gray-900">분석 결과</h2>
          </CardHeader>
          <CardBody>
            {analyzing ? (
              <div className="py-12">
                <Loading size="lg" text="AI가 차트를 분석하고 있습니다..." />
              </div>
            ) : currentAnalysis ? (
              <div className="space-y-4">
                <div className="flex items-center gap-2">
                  <Badge variant="success">분석 완료</Badge>
                  <span className="text-sm text-gray-500">
                    {new Date(currentAnalysis.createdAt).toLocaleString('ko-KR')}
                  </span>
                </div>

                <div className="bg-gray-50 rounded-lg p-4">
                  <h3 className="font-semibold text-gray-900 mb-2">
                    AI 분석 인사이트
                  </h3>
                  <p className="text-gray-700 whitespace-pre-wrap leading-relaxed">
                    {currentAnalysis.analysis}
                  </p>
                </div>

                <Button
                  variant="outline"
                  fullWidth
                  onClick={handleReset}
                >
                  새로운 차트 분석하기
                </Button>
              </div>
            ) : (
              <div className="text-center py-12">
                <FileImage className="mx-auto mb-4 text-gray-400" size={48} />
                <p className="text-gray-600">
                  차트를 업로드하고 분석을 시작하세요.
                </p>
              </div>
            )}
          </CardBody>
        </Card>
      </div>

      {/* Analysis History */}
      <Card>
        <CardHeader>
          <h2 className="text-xl font-semibold text-gray-900">분석 내역</h2>
        </CardHeader>
        <CardBody>
          {loadingHistory ? (
            <div className="py-12">
              <Loading text="내역을 불러오는 중..." />
            </div>
          ) : history.length === 0 ? (
            <div className="text-center py-12">
              <FileImage className="mx-auto mb-4 text-gray-400" size={48} />
              <p className="text-gray-600">아직 분석한 차트가 없습니다.</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {history.map((item) => (
                <Card key={item.id} hover className="overflow-hidden">
                  <img
                    src={`http://localhost:8080${item.imagePath}`}
                    alt="Chart"
                    className="w-full h-40 object-cover"
                  />
                  <div className="p-4">
                    <p className="text-sm text-gray-700 line-clamp-3 mb-2">
                      {item.analysis}
                    </p>
                    <p className="text-xs text-gray-500">
                      {new Date(item.createdAt).toLocaleString('ko-KR')}
                    </p>
                  </div>
                </Card>
              ))}
            </div>
          )}
        </CardBody>
      </Card>
    </div>
  );
};

export default AnalysisPage;
