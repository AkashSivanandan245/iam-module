import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

// Wraps any route that should only render for a signed-in user.
// While the initial /me hydration is in flight we show a lightweight placeholder
// instead of bouncing the user to /login on a page refresh.
export default function ProtectedRoute({ children }) {
  const { user, loading } = useAuth();

  if (loading) {
    return <div className="page-center muted">Checking your session…</div>;
  }
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  return children;
}
