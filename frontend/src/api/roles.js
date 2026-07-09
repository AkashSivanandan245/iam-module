import apiClient from './client';

export const rolesApi = {
  list: () => apiClient.get('/api/v1/roles').then((r) => r.data),

  create: (payload) =>
    apiClient.post('/api/v1/roles', payload).then((r) => r.data),

  update: (id, payload) =>
    apiClient.put(`/api/v1/roles/${id}`, payload).then((r) => r.data),

  updatePermissions: (id, permissionIds) =>
    apiClient
      .patch(`/api/v1/roles/${id}/permissions`, { permissionIds })
      .then((r) => r.data),

  remove: (id) =>
    apiClient.delete(`/api/v1/roles/${id}`).then((r) => r.data),
};

// The catalog exposes the full authority list (module × action combinations)
// used by the role permission editor.
export const catalogApi = {
  listAuthorities: () =>
    apiClient.get('/api/v1/authorities').then((r) => r.data),

  matrix: () =>
    apiClient.get('/api/v1/catalog/matrix').then((r) => r.data),
};
