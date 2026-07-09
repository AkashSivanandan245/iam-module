import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { authApi } from '../api/auth';
import ErrorBanner from '../components/ErrorBanner';

export default function ResetPasswordPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const email = location.state?.email || '';
  const otp = location.state?.otp || '';

  const [newPassword, setNewPassword] = useState('');
  const [confirm, setConfirm] = useState('');
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [done, setDone] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError(null);
    // Simple client-side check — real strength rules live server-side.
    if (newPassword !== confirm) {
      setError({ status: 400, message: 'Passwords do not match.' });
      return;
    }
    setSubmitting(true);
    try {
      await authApi.resetPassword(email, otp, newPassword);
      setDone(true);
      // Small delay so the success message actually gets read.
      setTimeout(() => navigate('/login'), 1500);
    } catch (err) {
      setError(err);
    } finally {
      setSubmitting(false);
    }
  };

  if (done) {
    return (
      <div className="auth-shell">
        <div className="auth-card">
          <h1>Password updated</h1>
          <p className="muted">Redirecting you to sign in…</p>
        </div>
      </div>
    );
  }

  return (
    <div className="auth-shell">
      <div className="auth-card">
        <h1>Set a new password</h1>
        <p className="muted">Resetting the password for {email || 'your account'}.</p>

        <ErrorBanner error={error} />

        <form onSubmit={handleSubmit} className="stack">
          <label>
            New password
            <input
              type="password"
              autoComplete="new-password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              required
              minLength={8}
            />
          </label>
          <label>
            Confirm password
            <input
              type="password"
              autoComplete="new-password"
              value={confirm}
              onChange={(e) => setConfirm(e.target.value)}
              required
              minLength={8}
            />
          </label>
          <button type="submit" disabled={submitting}>
            {submitting ? 'Updating…' : 'Update password'}
          </button>
        </form>

        <div className="auth-footer">
          <Link to="/login">Back to sign in</Link>
        </div>
      </div>
    </div>
  );
}
