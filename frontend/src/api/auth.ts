import apiClient from './client';
import type { AuthResponse, LoginRequest, SignupRequest, User } from '../types';

export const authApi = {
  // Login
  login: async (data: LoginRequest): Promise<AuthResponse> => {
    const response = await apiClient.post<{ data: AuthResponse }>('/api/auth/login', data);
    return response.data.data; // Unwrap ApiResponse wrapper
  },

  // Signup
  signup: async (data: SignupRequest): Promise<AuthResponse> => {
    const response = await apiClient.post<{ data: AuthResponse }>('/api/auth/signup', data);
    return response.data.data; // Unwrap ApiResponse wrapper
  },

  // Get current user
  getCurrentUser: async (): Promise<User> => {
    const response = await apiClient.get<User>('/api/auth/me');
    return response.data;
  },

  // Logout (client-side)
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },
};
