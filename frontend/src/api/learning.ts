import apiClient from './client';
import type { LearningModule, LearningContent, LearningStats, LearningProgress } from '../types';

export const learningApi = {
  // Get all modules
  getModules: async (): Promise<LearningModule[]> => {
    const response = await apiClient.get<LearningModule[]>('/api/learning/modules');
    return response.data;
  },

  // Get module by ID
  getModuleById: async (id: number): Promise<LearningModule> => {
    const response = await apiClient.get<LearningModule>(`/api/learning/modules/${id}`);
    return response.data;
  },

  // Get modules by difficulty
  getModulesByDifficulty: async (difficulty: string): Promise<LearningModule[]> => {
    const response = await apiClient.get<LearningModule[]>(
      `/api/learning/modules/difficulty/${difficulty}`
    );
    return response.data;
  },

  // Get contents by module
  getContentsByModule: async (moduleId: number): Promise<LearningContent[]> => {
    const response = await apiClient.get<LearningContent[]>(
      `/api/learning/modules/${moduleId}/contents`
    );
    return response.data;
  },

  // Get content by ID
  getContentById: async (id: number): Promise<LearningContent> => {
    const response = await apiClient.get<LearningContent>(`/api/learning/contents/${id}`);
    return response.data;
  },

  // Update progress
  updateProgress: async (
    contentId: number,
    data: { completed: boolean; progressPercentage: number }
  ): Promise<LearningProgress> => {
    const response = await apiClient.post<LearningProgress>(
      `/api/learning/contents/${contentId}/progress`,
      data
    );
    return response.data;
  },

  // Like content
  likeContent: async (id: number): Promise<void> => {
    await apiClient.post(`/api/learning/contents/${id}/like`);
  },

  // Unlike content
  unlikeContent: async (id: number): Promise<void> => {
    await apiClient.delete(`/api/learning/contents/${id}/like`);
  },

  // Bookmark content
  bookmarkContent: async (id: number): Promise<void> => {
    await apiClient.post(`/api/learning/contents/${id}/bookmark`);
  },

  // Remove bookmark
  removeBookmark: async (id: number): Promise<void> => {
    await apiClient.delete(`/api/learning/contents/${id}/bookmark`);
  },

  // Get learning statistics
  getStats: async (): Promise<LearningStats> => {
    const response = await apiClient.get<LearningStats>('/api/learning/progress/stats');
    return response.data;
  },
};
