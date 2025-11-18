import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { Button, Input, Card } from '../../components/ui';
import { UserPlus } from 'lucide-react';

const SignupPage: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [nickname, setNickname] = useState('');
  const [investmentLevel, setInvestmentLevel] = useState<'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED'>('BEGINNER');
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState<{
    email?: string;
    password?: string;
    confirmPassword?: string;
    nickname?: string;
  }>({});

  const { signup, isAuthenticated, loading: authLoading } = useAuth();
  const navigate = useNavigate();

  // Redirect if already authenticated
  useEffect(() => {
    if (!authLoading && isAuthenticated) {
      navigate('/dashboard', { replace: true });
    }
  }, [isAuthenticated, authLoading, navigate]);

  const validate = () => {
    const newErrors: typeof errors = {};

    if (!email.trim()) {
      newErrors.email = '이메일을 입력해주세요.';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      newErrors.email = '올바른 이메일 형식이 아닙니다.';
    }

    if (!password) {
      newErrors.password = '비밀번호를 입력해주세요.';
    } else if (password.length < 8) {
      newErrors.password = '비밀번호는 8자 이상이어야 합니다.';
    } else if (!/^(?=.*[A-Za-z])(?=.*\d).+$/.test(password)) {
      newErrors.password = '비밀번호는 영문과 숫자를 포함해야 합니다.';
    }

    if (!confirmPassword) {
      newErrors.confirmPassword = '비밀번호를 다시 입력해주세요.';
    } else if (password !== confirmPassword) {
      newErrors.confirmPassword = '비밀번호가 일치하지 않습니다.';
    }

    if (!nickname.trim()) {
      newErrors.nickname = '닉네임을 입력해주세요.';
    } else if (nickname.length < 2 || nickname.length > 50) {
      newErrors.nickname = '닉네임은 2~50자 사이여야 합니다.';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validate()) return;

    setLoading(true);
    try {
      await signup({ email, password, nickname, investmentLevel });
      // After successful signup, user is auto-logged in, redirect to dashboard
      navigate('/dashboard', { replace: true });
    } catch (error) {
      // Error is already handled in AuthContext with toast
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-dark-bg-primary px-4 py-12">
      <div className="w-full max-w-md">
        {/* Logo */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-accent-blue rounded-2xl mb-4">
            <span className="text-white font-bold text-2xl">T</span>
          </div>
          <h1 className="text-3xl font-bold text-dark-text-primary mb-2">TradeVision</h1>
          <p className="text-dark-text-secondary">AI 기반 차트 분석 플랫폼</p>
        </div>

        {/* Signup Form */}
        <Card className="p-8">
          <h2 className="text-2xl font-bold text-dark-text-primary mb-6">회원가입</h2>

          <form onSubmit={handleSubmit} className="space-y-4">
            <Input
              label="이메일"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              error={errors.email}
              placeholder="이메일을 입력하세요"
              fullWidth
              autoFocus
            />

            <Input
              label="닉네임"
              type="text"
              value={nickname}
              onChange={(e) => setNickname(e.target.value)}
              error={errors.nickname}
              placeholder="닉네임을 입력하세요 (2~50자)"
              fullWidth
            />

            <Input
              label="비밀번호"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              error={errors.password}
              placeholder="비밀번호를 입력하세요"
              helperText="8자 이상, 영문과 숫자를 포함해주세요"
              fullWidth
            />

            <Input
              label="비밀번호 확인"
              type="password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              error={errors.confirmPassword}
              placeholder="비밀번호를 다시 입력하세요"
              fullWidth
            />

            <div>
              <label className="block text-sm font-medium text-dark-text-primary mb-2">
                투자 경험 수준
              </label>
              <select
                value={investmentLevel}
                onChange={(e) => setInvestmentLevel(e.target.value as 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED')}
                className="w-full px-4 py-3 border border-dark-border rounded-md bg-dark-bg-card text-dark-text-primary focus:outline-none focus:border-accent-blue transition-all duration-200"
              >
                <option value="BEGINNER">초보자 (투자 경험 1년 미만)</option>
                <option value="INTERMEDIATE">중급자 (투자 경험 1~3년)</option>
                <option value="ADVANCED">고급자 (투자 경험 3년 이상)</option>
              </select>
            </div>

            <Button
              type="submit"
              loading={loading}
              fullWidth
              size="lg"
              className="mt-6"
            >
              <UserPlus size={20} />
              회원가입
            </Button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-dark-text-secondary">
              이미 계정이 있으신가요?{' '}
              <Link to="/login" className="text-accent-blue hover:text-accent-blue/80 font-medium">
                로그인
              </Link>
            </p>
          </div>
        </Card>
      </div>
    </div>
  );
};

export default SignupPage;
