import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from './contexts/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Layout from './components/layout/Layout';

// Public Pages
import LandingPage from './pages/landing/LandingPage';
import PricingPage from './pages/pricing/PricingPage';

// Auth Pages
import LoginPage from './pages/auth/LoginPage';
import SignupPage from './pages/auth/SignupPage';

// Dashboard
import DashboardPage from './pages/dashboard/DashboardPage';

// Analysis
import AnalysisPage from './pages/analysis/AnalysisPage';

// Techniques
import TechniquesPage from './pages/techniques/TechniquesPage';

// Learning
import LearningModulesPage from './pages/learning/LearningModulesPage';
import ModuleContentsPage from './pages/learning/ModuleContentsPage';
import ContentViewerPage from './pages/learning/ContentViewerPage';

// Profile
import ProfilePage from './pages/profile/ProfilePage';

function App() {
  return (
    <Router>
      <AuthProvider>
        <Toaster
          position="top-right"
          toastOptions={{
            duration: 3000,
            style: {
              background: '#1a1f2e',
              color: '#e4e7eb',
              padding: '16px',
              borderRadius: '8px',
              boxShadow: '0 4px 12px rgba(0, 0, 0, 0.4)',
              border: '1px solid #2a3142',
            },
            success: {
              iconTheme: {
                primary: '#00d4ff',
                secondary: '#0a0e17',
              },
            },
          }}
        />

        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<LandingPage />} />
          <Route path="/pricing" element={<PricingPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupPage />} />

          {/* Protected Routes */}
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <Layout>
                  <DashboardPage />
                </Layout>
              </ProtectedRoute>
            }
          />

          <Route
            path="/analysis"
            element={
              <ProtectedRoute>
                <Layout>
                  <AnalysisPage />
                </Layout>
              </ProtectedRoute>
            }
          />

          <Route
            path="/techniques"
            element={
              <ProtectedRoute>
                <Layout>
                  <TechniquesPage />
                </Layout>
              </ProtectedRoute>
            }
          />

          <Route
            path="/learning"
            element={
              <ProtectedRoute>
                <Layout>
                  <LearningModulesPage />
                </Layout>
              </ProtectedRoute>
            }
          />

          <Route
            path="/learning/modules/:moduleId"
            element={
              <ProtectedRoute>
                <Layout>
                  <ModuleContentsPage />
                </Layout>
              </ProtectedRoute>
            }
          />

          <Route
            path="/learning/contents/:contentId"
            element={
              <ProtectedRoute>
                <Layout>
                  <ContentViewerPage />
                </Layout>
              </ProtectedRoute>
            }
          />

          <Route
            path="/profile"
            element={
              <ProtectedRoute>
                <Layout>
                  <ProfilePage />
                </Layout>
              </ProtectedRoute>
            }
          />

          {/* Catch all - redirect to home */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </Router>
  );
}

export default App;
