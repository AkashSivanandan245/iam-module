import apiClient from './client';

// Thin wrapper around every /api/v1/auth endpoint so components don't
// have to remember URL strings or payload shapes.
export const authApi = {
  login: (email, password) =>
    apiClient.post('/api/v1/auth/login', { email, password }).then((r) => r.data),

  refresh: (refreshToken) =>
    apiClient.post('/api/v1/auth/refresh', { refreshToken }).then((r) => r.data),

  logout: () => apiClient.post('/api/v1/auth/logout').then((r) => r.data),

  forgotPassword: (email) =>
    apiClient.post('/api/v1/auth/forgot-password', { email }).then((r) => r.data),

  verifyOtp: (email, otp) =>
    apiClient.post('/api/v1/auth/verify-otp', { email, otp }).then((r) => r.data),

  resetPassword: (email, otp, newPassword) =>
    apiClient
      .post('/api/v1/auth/reset-password', { email, otp, newPassword })
      .then((r) => r.data),

  me: () => apiClient.get('/api/v1/auth/me').then((r) => r.data),

  myPermissions: () =>
    apiClient.get('/api/v1/auth/me/permissions').then((r) => r.data),
};
