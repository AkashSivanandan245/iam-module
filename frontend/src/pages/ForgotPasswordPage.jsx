import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authApi } from '../api/auth';
import ErrorBanner from '../components/ErrorBanner';

// Step 1 of the 3-step password reset:
// (1) here we take an email and ask the backend to send an OTP,
// (2) VerifyOtpPage validates the OTP,
// (3) ResetPasswordPage sets the new password.
export default function ForgotPasswordPage() {
  const [email, setEmail] = useState('');
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      await authApi.forgotPassword(email);
      // Pass the email forward in router state so the next screen doesn't have
      // to ask for it again.
      navigate('/verify-otp', { state: { email } });
    } catch (err) {
      setError(err);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="auth-shell">
      <div className="auth-card">
        <h1>Forgot password</h1>
        <p className="muted">
          Enter your registered email and we'll send you a one-time code.
        </p>

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
          <button type="submit" disabled={submitting}>
            {submitting ? 'Sending…' : 'Send OTP'}
          </button>
        </form>

        <div className="auth-footer">
          <Link to="/login">Back to sign in</Link>
        </div>
      </div>
    </div>
  );
}
