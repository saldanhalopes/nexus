import axios from 'axios';

// The API endpoint is proxied through Next.js rewrites in next.config.ts
// which maps /api/* to http://localhost:8089/api/*
export const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

export const setAuthToken = (token: string | null) => {
  if (token) {
    api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    // Store in localStorage for client-side persistence
    if (typeof window !== 'undefined') {
      localStorage.setItem('nexus_token', token);
    }
  } else {
    delete api.defaults.headers.common['Authorization'];
    if (typeof window !== 'undefined') {
      localStorage.removeItem('nexus_token');
    }
  }
};

// Intercept responses to handle 401 Unauthorized globally if needed
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      if (typeof window !== 'undefined') {
        localStorage.removeItem('nexus_token');
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);
