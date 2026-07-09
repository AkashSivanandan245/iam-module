import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { authApi } from '../api/auth';
import ErrorBanner from '../components/ErrorBanner';

export default function VerifyOtpPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const initialEmail = location.state?.email || '';

  const [email, setEmail] = useState(initialEmail);
  const [otp, setOtp] = useState('');
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      await authApi.verifyOtp(email, otp);
      // Verified — the backend still requires the OTP again during the reset
      // step (so it stays authoritative), so we forward it in router state.
      navigate('/reset-password', { state: { email, otp } });
    } catch (err) {
      setError(err);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="auth-shell">
      <div className="auth-card">
        <h1>Verify OTP</h1>
        <p className="muted">Enter the code we emailed you.</p>

        <ErrorBanner error={error} />

        <form onSubmit={handleSubmit} className="stack">
          <label>
            Email
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </label>
          <label>
            One-time code
            <input
              type="text"
              inputMode="numeric"
              autoComplete="one-time-code"
              value={otp}
              onChange={(e) => setOtp(e.target.value)}
              required
            />
          </label>
          <button type="submit" disabled={submitting}>
            {submitting ? 'Verifying…' : 'Verify'}
          </button>
        </form>

        <div className="auth-footer">
          <Link to="/forgot-password">Send a new code</Link>
        </div>
      </div>
    </div>
  );
}
