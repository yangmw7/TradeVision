import apiClient from './client';
import type { ChartAnalysis, AnalysisHistory } from '../types';

export const chartsApi = {
  // Upload and analyze chart
  uploadChart: async (file: File): Promise<ChartAnalysis> => {
    const formData = new FormData();
    formData.append('file', file);

    const response = await apiClient.post<ChartAnalysis>(
      '/api/charts/upload',
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }
    );
    return response.data;
  },

  // Analyze uploaded chart
  analyzeChart: async (chartId: number): Promise<ChartAnalysis> => {
    const response = await apiClient.post<ChartAnalysis>(
      `/api/charts/analyze/${chartId}`
    );
    return response.data;
  },

  // Get analysis history
  getHistory: async (): Promise<AnalysisHistory[]> => {
    const response = await apiClient.get<AnalysisHistory[]>('/api/charts/history');
    return response.data;
  },

  // Get specific analysis
  getAnalysis: async (id: number): Promise<ChartAnalysis> => {
    const response = await apiClient.get<ChartAnalysis>(`/api/charts/history/${id}`);
    return response.data;
  },

  // Delete analysis
  deleteAnalysis: async (id: number): Promise<void> => {
    await apiClient.delete(`/api/charts/${id}`);
  },
};
