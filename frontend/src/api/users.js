import apiClient from './client';

// All /api/v1/users endpoints. Uses PageResponse<UserResponse> for lists;
// the frontend just unwraps `content` and pagination metadata as needed.
export const usersApi = {
  list: (page = 0, size = 10) =>
    apiClient
      .get('/api/v1/users', { params: { page, size } })
      .then((r) => r.data),

  getById: (id) =>
    apiClient.get(`/api/v1/users/${id}`).then((r) => r.data),

  create: (payload) =>
    apiClient.post('/api/v1/users', payload).then((r) => r.data),

  update: (id, payload) =>
    apiClient.put(`/api/v1/users/${id}`, payload).then((r) => r.data),

  changeStatus: (id, status) =>
    apiClient
      .patch(`/api/v1/users/${id}/status`, { status })
      .then((r) => r.data),

  assignRole: (id, roleId) =>
    apiClient
      .patch(`/api/v1/users/${id}/role`, { roleId })
      .then((r) => r.data),

  getPermissions: (id) =>
    apiClient
      .get(`/api/v1/users/${id}/permissions`)
      .then((r) => r.data),
};
