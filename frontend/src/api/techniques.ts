import apiClient from './client';
import type { TradingTechnique } from '../types';

export const techniquesApi = {
  // Get all techniques
  getAll: async (): Promise<TradingTechnique[]> => {
    const response = await apiClient.get<TradingTechnique[]>('/api/trading-techniques');
    return response.data;
  },

  // Get technique by ID
  getById: async (id: number): Promise<TradingTechnique> => {
    const response = await apiClient.get<TradingTechnique>(`/api/trading-techniques/${id}`);
    return response.data;
  },

  // Get by category
  getByCategory: async (category: string): Promise<TradingTechnique[]> => {
    const response = await apiClient.get<TradingTechnique[]>(
      `/api/trading-techniques/category/${category}`
    );
    return response.data;
  },

  // Get by difficulty
  getByDifficulty: async (difficulty: string): Promise<TradingTechnique[]> => {
    const response = await apiClient.get<TradingTechnique[]>(
      `/api/trading-techniques/difficulty/${difficulty}`
    );
    return response.data;
  },

  // Search techniques
  search: async (keyword: string): Promise<TradingTechnique[]> => {
    const response = await apiClient.get<TradingTechnique[]>(
      '/api/trading-techniques/search',
      { params: { keyword } }
    );
    return response.data;
  },

  // Get popular techniques
  getPopular: async (): Promise<TradingTechnique[]> => {
    const response = await apiClient.get<TradingTechnique[]>(
      '/api/trading-techniques/popular'
    );
    return response.data;
  },

  // Bookmark technique
  bookmark: async (id: number): Promise<void> => {
    await apiClient.post(`/api/trading-techniques/${id}/bookmark`);
  },

  // Remove bookmark
  removeBookmark: async (id: number): Promise<void> => {
    await apiClient.delete(`/api/trading-techniques/${id}/bookmark`);
  },
};
