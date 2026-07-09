import apiClient from './client';

// Grouped by entity — each entity has the same CRUD shape so the master-data
// page can render them behind a shared component.
export const masterDataApi = {
  orgs: {
    list: () => apiClient.get('/api/v1/orgs').then((r) => r.data),
    create: (payload) => apiClient.post('/api/v1/orgs', payload).then((r) => r.data),
    update: (id, payload) => apiClient.put(`/api/v1/orgs/${id}`, payload).then((r) => r.data),
    remove: (id) => apiClient.delete(`/api/v1/orgs/${id}`).then((r) => r.data),
  },
  universities: {
    list: () => apiClient.get('/api/v1/universities').then((r) => r.data),
    create: (payload) => apiClient.post('/api/v1/universities', payload).then((r) => r.data),
    update: (id, payload) => apiClient.put(`/api/v1/universities/${id}`, payload).then((r) => r.data),
    remove: (id) => apiClient.delete(`/api/v1/universities/${id}`).then((r) => r.data),
  },
  branches: {
    list: () => apiClient.get('/api/v1/branches').then((r) => r.data),
    create: (payload) => apiClient.post('/api/v1/branches', payload).then((r) => r.data),
    update: (id, payload) => apiClient.put(`/api/v1/branches/${id}`, payload).then((r) => r.data),
    remove: (id) => apiClient.delete(`/api/v1/branches/${id}`).then((r) => r.data),
  },
  domains: {
    list: () => apiClient.get('/api/v1/domains').then((r) => r.data),
    create: (payload) => apiClient.post('/api/v1/domains', payload).then((r) => r.data),
    update: (id, payload) => apiClient.put(`/api/v1/domains/${id}`, payload).then((r) => r.data),
    remove: (id) => apiClient.delete(`/api/v1/domains/${id}`).then((r) => r.data),
  },
};
