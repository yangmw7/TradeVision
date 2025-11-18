import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import type { User, LoginRequest, SignupRequest } from '../types';
import { authApi } from '../api';
import toast from 'react-hot-toast';

interface AuthContextType {
  user: User | null;
  token: string | null;
  loading: boolean;
  login: (data: LoginRequest) => Promise<void>;
  signup: (data: SignupRequest) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  // Initialize auth state from localStorage
  useEffect(() => {
    try {
      const storedToken = localStorage.getItem('token');
      const storedUser = localStorage.getItem('user');

      if (storedToken && storedToken !== 'undefined' && storedToken !== 'null') {
        setToken(storedToken);
      }

      if (storedUser && storedUser !== 'undefined' && storedUser !== 'null') {
        try {
          const parsedUser = JSON.parse(storedUser);
          setUser(parsedUser);
        } catch (e) {
          console.error('Failed to parse user from localStorage:', e);
          localStorage.removeItem('user');
        }
      }
    } catch (error) {
      console.error('Error initializing auth from localStorage:', error);
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    } finally {
      setLoading(false);
    }
  }, []);

  const login = async (data: LoginRequest) => {
    try {
      const response = await authApi.login(data);

      if (!response || !response.accessToken) {
        throw new Error('Invalid response from server');
      }

      // Set state first
      setToken(response.accessToken);
      setUser(response.user);

      // Then update localStorage - wrapped in try-catch to prevent blocking
      try {
        localStorage.setItem('token', response.accessToken);
        if (response.user) {
          localStorage.setItem('user', JSON.stringify(response.user));
        }
      } catch (storageError) {
        console.error('Failed to save to localStorage:', storageError);
        // Don't throw - auth still succeeded
      }

      toast.success('로그인 성공!');
      // Explicitly return void - no error thrown
    } catch (error: any) {
      console.error('Login error:', error);
      toast.error(error.message || '로그인에 실패했습니다.');
      throw error;
    }
  };

  const signup = async (data: SignupRequest) => {
    try {
      const response = await authApi.signup(data);

      if (!response || !response.accessToken) {
        throw new Error('Invalid response from server');
      }

      // Set state first
      setToken(response.accessToken);
      setUser(response.user);

      // Then update localStorage - wrapped in try-catch to prevent blocking
      try {
        localStorage.setItem('token', response.accessToken);
        if (response.user) {
          localStorage.setItem('user', JSON.stringify(response.user));
        }
      } catch (storageError) {
        console.error('Failed to save to localStorage:', storageError);
        // Don't throw - auth still succeeded
      }

      toast.success('회원가입 성공!');
      // Explicitly return void - no error thrown
    } catch (error: any) {
      console.error('Signup error:', error);
      toast.error(error.message || '회원가입에 실패했습니다.');
      throw error;
    }
  };

  const logout = () => {
    authApi.logout();
    setToken(null);
    setUser(null);
    toast.success('로그아웃되었습니다.');
  };

  const value: AuthContextType = {
    user,
    token,
    loading,
    login,
    signup,
    logout,
    isAuthenticated: !!token && !!user,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
