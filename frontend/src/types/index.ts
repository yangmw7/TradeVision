// Authentication types
export interface User {
  id: number;
  username?: string;
  nickname: string;
  email: string;
  createdAt: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface SignupRequest {
  email: string;
  password: string;
  nickname: string;
  investmentLevel?: 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED';
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  user: User;
}

// Chart Analysis types
export interface ChartAnalysis {
  id: number;
  userId: number;
  imagePath: string;
  analysis: string;
  status: 'PENDING' | 'COMPLETED' | 'FAILED';
  createdAt: string;
  updatedAt: string;
}

export interface AnalysisHistory {
  id: number;
  imagePath: string;
  analysis: string;
  createdAt: string;
}

// Trading Technique types
export interface TradingTechnique {
  id: number;
  title: string;
  description: string;
  category: string;
  difficulty: 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED';
  content: string;
  imageUrl?: string;
  viewCount: number;
  bookmarkCount: number;
  isBookmarked?: boolean;
  createdAt: string;
  updatedAt: string;
}

// Learning Content types
export interface LearningModule {
  id: number;
  title: string;
  description: string;
  difficulty: 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED';
  orderIndex: number;
  totalContents: number;
  completedContents?: number;
  createdAt: string;
  updatedAt: string;
}

export interface LearningContent {
  id: number;
  moduleId: number;
  title: string;
  content: string;
  contentType: 'TEXT' | 'VIDEO' | 'QUIZ';
  orderIndex: number;
  estimatedMinutes: number;
  likeCount: number;
  isLiked?: boolean;
  isBookmarked?: boolean;
  isCompleted?: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface LearningProgress {
  contentId: number;
  userId: number;
  completed: boolean;
  progressPercentage: number;
  lastAccessedAt: string;
}

export interface LearningStats {
  totalModules: number;
  completedModules: number;
  totalContents: number;
  completedContents: number;
  totalLearningMinutes: number;
  currentStreak: number;
  totalBookmarks: number;
}

// API Response types
export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
}

// Error types
export interface ApiError {
  message: string;
  status: number;
  errors?: Record<string, string[]>;
}
