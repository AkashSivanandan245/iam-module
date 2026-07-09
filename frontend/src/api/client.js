import axios from 'axios';

// Central axios instance — every API call in the app should go through this.
// Base URL is driven by VITE_API_BASE_URL so we can point at any environment
// without rebuilding.
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 15000,
});

// Request interceptor: attach the JWT to every request if we have one.
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor: shape the error payload so components can render it
// uniformly. Also surfaces the Retry-After header from 429 responses.
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const responseData = error.response?.data || {};
    const normalised = {
      status: error.response?.status || 0,
      message: responseData.message || error.message || 'Something went wrong. Please try again.',
      retryAfter: Number(error.response?.headers?.['retry-after']) || null,
    };
    return Promise.reject(normalised);
  }
);

export default apiClient;
