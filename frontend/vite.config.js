import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Standard Vite + React setup. Dev server runs on 5173 (default) — matches the
// origin whitelisted in the backend's SecurityConfig CORS bean.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
  },
});
