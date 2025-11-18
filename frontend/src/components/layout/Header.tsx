import React, { useState, useEffect, useRef } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { TrendingUp, User, ChevronDown } from 'lucide-react';

interface HeaderProps {
  onMenuClick?: () => void;
}

const Header: React.FC<HeaderProps> = () => {
  const { user, logout, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [showUserMenu, setShowUserMenu] = useState(false);
  const userMenuRef = useRef<HTMLDivElement>(null);

  const handleLogout = () => {
    logout();
    navigate('/');
    setShowUserMenu(false);
  };

  const toggleUserMenu = () => {
    setShowUserMenu(!showUserMenu);
  };

  // Close user menu when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (userMenuRef.current && !userMenuRef.current.contains(event.target as Node)) {
        setShowUserMenu(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  const isActive = (path: string) => location.pathname === path;

  return (
    <header className="fixed top-0 left-0 right-0 z-50 bg-[#131722] border-b border-[#2a2e39]">
      <div className="max-w-full px-6 py-3 flex items-center justify-between">
        {/* Logo - FAR LEFT */}
        <Link to="/" className="flex items-center gap-2">
          <TrendingUp className="w-6 h-6 text-blue-500" />
          <span className="text-xl font-bold text-white">TradeVision</span>
        </Link>

        {/* Navigation - CENTER (only show if logged in) */}
        {isAuthenticated && (
          <nav className="hidden md:flex items-center gap-8">
            <Link
              to="/dashboard"
              className={`transition-colors ${
                isActive('/dashboard')
                  ? 'text-white font-medium'
                  : 'text-gray-400 hover:text-white'
              }`}
            >
              대시보드
            </Link>
            <Link
              to="/analysis"
              className={`transition-colors ${
                isActive('/analysis')
                  ? 'text-white font-medium'
                  : 'text-gray-400 hover:text-white'
              }`}
            >
              차트 분석
            </Link>
            <Link
              to="/techniques"
              className={`transition-colors ${
                isActive('/techniques')
                  ? 'text-white font-medium'
                  : 'text-gray-400 hover:text-white'
              }`}
            >
              매매 기법
            </Link>
            <Link
              to="/learning"
              className={`transition-colors ${
                isActive('/learning')
                  ? 'text-white font-medium'
                  : 'text-gray-400 hover:text-white'
              }`}
            >
              학습 콘텐츠
            </Link>
          </nav>
        )}

        {/* Right side - User menu or Login/Signup */}
        <div className="flex items-center gap-4">
          {isAuthenticated ? (
            <>
              <Link
                to="/pricing"
                className="hidden md:block text-gray-400 hover:text-white transition-colors"
              >
                요금제
              </Link>
              <div className="relative" ref={userMenuRef}>
                <button
                  onClick={toggleUserMenu}
                  className="flex items-center gap-2 px-3 py-2 rounded-lg hover:bg-[#1e222d] transition-colors"
                >
                  <div className="w-8 h-8 rounded-full bg-blue-600 flex items-center justify-center">
                    <User className="w-5 h-5 text-white" />
                  </div>
                  <span className="hidden md:block text-white font-medium">
                    {user?.nickname || '사용자'}
                  </span>
                  <ChevronDown className={`w-4 h-4 text-gray-400 transition-transform ${showUserMenu ? 'rotate-180' : ''}`} />
                </button>

                {showUserMenu && (
                  <div className="absolute right-0 mt-2 w-56 bg-[#1e222d] rounded-lg shadow-xl border border-[#2a2e39] py-2">
                    <div className="px-4 py-3 border-b border-[#2a2e39]">
                      <p className="text-white font-medium">{user?.nickname}</p>
                      <p className="text-gray-400 text-sm">{user?.email}</p>
                    </div>
                    <Link
                      to="/dashboard"
                      className="block px-4 py-2 text-gray-300 hover:bg-[#2a2e39] hover:text-white transition-colors"
                      onClick={() => setShowUserMenu(false)}
                    >
                      나의 대시보드
                    </Link>
                    <Link
                      to="/profile"
                      className="block px-4 py-2 text-gray-300 hover:bg-[#2a2e39] hover:text-white transition-colors"
                      onClick={() => setShowUserMenu(false)}
                    >
                      프로필 설정
                    </Link>
                    <Link
                      to="/pricing"
                      className="block md:hidden px-4 py-2 text-gray-300 hover:bg-[#2a2e39] hover:text-white transition-colors"
                      onClick={() => setShowUserMenu(false)}
                    >
                      요금제
                    </Link>
                    <div className="border-t border-[#2a2e39] mt-2 pt-2">
                      <button
                        onClick={handleLogout}
                        className="w-full text-left px-4 py-2 text-red-400 hover:bg-[#2a2e39] hover:text-red-300 transition-colors"
                      >
                        로그아웃
                      </button>
                    </div>
                  </div>
                )}
              </div>
            </>
          ) : (
            <>
              <Link
                to="/login"
                className="text-gray-300 hover:text-white transition-colors"
              >
                로그인
              </Link>
              <Link
                to="/pricing"
                className="hidden md:block text-gray-300 hover:text-white transition-colors"
              >
                요금제
              </Link>
              <Link
                to="/signup"
                className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors"
              >
                무료 시작하기
              </Link>
            </>
          )}
        </div>
      </div>
    </header>
  );
};

export default Header;
