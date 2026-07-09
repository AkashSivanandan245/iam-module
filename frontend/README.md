# LMS IAM Frontend

Minimal Vite + React client for the Xebia LMS IAM (M01) service.

## What's inside

- **Login** — `POST /api/v1/auth/login`, stores the access & refresh tokens in `localStorage`.
- **Forgot password flow** — three-step: request OTP → verify OTP → set new password.
- **Dashboard** — shows the current user profile (`/api/v1/auth/me`) and their effective permissions (`/api/v1/auth/me/permissions`).
- **Rate-limit awareness** — when the backend returns `429 Too Many Requests`, the UI surfaces the `Retry-After` hint from the response headers so the user knows how long to wait.

## Stack

- Vite + React 18
- React Router v6 for routing
- Axios for HTTP (single shared client in `src/api/client.js`)
- Plain CSS, no UI library

## Getting started

```bash
cp .env.example .env      # adjust VITE_API_BASE_URL if the backend isn't on localhost:8080
npm install
npm run dev
```

The dev server runs on `http://localhost:5173`, which is already whitelisted in the backend's CORS configuration (`SecurityConfig#corsConfigurationSource`).

## Structure

```
src/
├── api/
│   ├── client.js         # axios instance + interceptors, normalises 429 payload
│   └── auth.js           # thin wrapper around every /api/v1/auth endpoint
├── context/
│   └── AuthContext.jsx   # login/logout, hydrates /me on refresh
├── components/
│   ├── ProtectedRoute.jsx
│   └── ErrorBanner.jsx   # renders errors, calls out rate-limit hits
├── pages/
│   ├── LoginPage.jsx
│   ├── ForgotPasswordPage.jsx
│   ├── VerifyOtpPage.jsx
│   ├── ResetPasswordPage.jsx
│   └── DashboardPage.jsx
├── App.jsx               # router setup
├── main.jsx
└── styles.css            # theme tokens + component styles
```

## Notes

- The refresh-token rotation is stubbed — `/auth/refresh` is wired in `authApi` but the axios interceptor doesn't yet auto-refresh on 401. That's a good Slice 2 improvement.
- Password strength rules are enforced server-side; the client only checks that the two entries match.
- `localStorage` is used for token storage for simplicity — a follow-up would move this to httpOnly cookies once the backend supports it.
