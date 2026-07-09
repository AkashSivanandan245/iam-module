import { createContext, useCallback, useContext, useEffect, useState } from 'react';
import { authApi } from '../api/auth';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [permissions, setPermissions] = useState(new Set());
  const [loading, setLoading] = useState(true);

  // Hydrate both profile and permissions in one go so the sidebar
  // knows what to show immediately on page refresh.
  const hydrate = useCallback(async () => {
    const [profile, permsData] = await Promise.all([
      authApi.me(),
      authApi.myPermissions(),
    ]);
    setUser(profile);
    setPermissions(new Set(permsData.permissions || []));
    return profile;
  }, []);

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (!token) { setLoading(false); return; }
    hydrate()
        .catch(() => {
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');
        })
        .finally(() => setLoading(false));
  }, [hydrate]);

  const login = async (email, password) => {
    const tokens = await authApi.login(email, password);
    localStorage.setItem('accessToken', tokens.accessToken);
    if (tokens.refreshToken) localStorage.setItem('refreshToken', tokens.refreshToken);
    return hydrate();
  };

  const logout = async () => {
    try { await authApi.logout(); } catch { /* ignore */ }
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    setUser(null);
    setPermissions(new Set());
  };

  // Convenience helper used by components to gate UI.
  const can = (permission) => permissions.has(permission);

  return (
      <AuthContext.Provider value={{ user, permissions, loading, login, logout, can }}>
        {children}
      </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used inside <AuthProvider>');
  return ctx;
}