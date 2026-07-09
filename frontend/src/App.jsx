import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import AppLayout from './components/AppLayout';
import LoginPage from './pages/LoginPage';
import ForgotPasswordPage from './pages/ForgotPasswordPage';
import VerifyOtpPage from './pages/VerifyOtpPage';
import ResetPasswordPage from './pages/ResetPasswordPage';
import DashboardPage from './pages/DashboardPage';
import UsersPage from './pages/UsersPage';
import RolesPage from './pages/RolesPage';
import MasterDataPage from './pages/MasterDataPage';
import AuditPage from './pages/AuditPage';
import CoursesPage from './pages/CoursesPage';
import EnrollmentsPage from './pages/EnrollmentsPage';
import AssessmentsPage from './pages/AssessmentsPage';
import ComingSoonPage from './pages/ComingSoonPage';
import AnalyticsPage from './pages/AnalyticsPage';

export default function App() {
  return (
      <BrowserRouter>
        <AuthProvider>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/forgot-password" element={<ForgotPasswordPage />} />
            <Route path="/verify-otp" element={<VerifyOtpPage />} />
            <Route path="/reset-password" element={<ResetPasswordPage />} />

            <Route
                element={
                  <ProtectedRoute>
                    <AppLayout />
                  </ProtectedRoute>
                }
            >
              <Route path="/" element={<Navigate to="/dashboard" replace />} />
              <Route path="/dashboard" element={<DashboardPage />} />
              <Route path="/users" element={<UsersPage />} />
              <Route path="/roles" element={<RolesPage />} />
              <Route path="/master-data" element={<MasterDataPage />} />
              <Route path="/audit" element={<AuditPage />} />
              <Route path="/courses" element={<CoursesPage />} />
              <Route path="/enrollments" element={<EnrollmentsPage />} />
              <Route path="/assessments" element={<AssessmentsPage />} />
              <Route path="/analytics" element={<AnalyticsPage />} />
            </Route>

            <Route path="*" element={<Navigate to="/login" replace />} />
          </Routes>
        </AuthProvider>
      </BrowserRouter>
  );
}