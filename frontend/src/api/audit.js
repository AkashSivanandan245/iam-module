import apiClient from './client';

export const auditApi = {
  list: (page = 0, size = 20) =>
    apiClient
      .get('/api/v1/audit', { params: { page, size } })
      .then((r) => r.data),
};
