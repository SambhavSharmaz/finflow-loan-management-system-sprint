// api.js — Simple fetch wrappers for all backend API endpoints
// All requests go through the Vite proxy → Gateway (localhost:8080)

const BASE = '/gateway';

// ---------- Helper ----------

function getToken() {
  return localStorage.getItem('token');
}

function authHeaders() {
  const token = getToken();
  const headers = { 'Content-Type': 'application/json' };
  if (token) {
    headers['Authorization'] = 'Bearer ' + token;
  }
  return headers;
}

// ---------- Global Error Handler ----------
// Intercepts 401 (Unauthorized) and 403 (Forbidden) responses

async function handleResponse(res) {
  if (res.status === 401) {
    // Token expired or invalid — clear session and redirect to login
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    window.location.href = '/login';
    return { success: false, message: 'Session expired. Please log in again.' };
  }

  if (res.status === 403) {
    return { success: false, message: 'Access denied. You don\'t have permission.' };
  }

  return res.json();
}

// ---------- Auth ----------

export async function signup(name, email, password) {
  const res = await fetch(BASE + '/auth/signup', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name, email, password }),
  });
  return res.json();
}

export async function login(email, password) {
  const res = await fetch(BASE + '/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password }),
  });
  return res.json();
}

// ---------- Applications ----------

export async function createApplication(data) {
  const res = await fetch(BASE + '/applications', {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify(data),
  });
  return handleResponse(res);
}

export async function getMyApplications() {
  const res = await fetch(BASE + '/applications/my', {
    headers: authHeaders(),
  });
  return handleResponse(res);
}

export async function getAllApplications() {
  const res = await fetch(BASE + '/applications/all', {
    headers: authHeaders(),
  });
  return handleResponse(res);
}

export async function getApplicationStatus(id) {
  const res = await fetch(BASE + '/applications/' + id + '/status', {
    headers: authHeaders(),
  });
  return handleResponse(res);
}

export async function updateApplication(id, data) {
  const res = await fetch(BASE + '/applications/' + id, {
    method: 'PUT',
    headers: authHeaders(),
    body: JSON.stringify(data),
  });
  return handleResponse(res);
}

export async function deleteApplication(id) {
  const res = await fetch(BASE + '/applications/' + id, {
    method: 'DELETE',
    headers: authHeaders(),
  });
  return handleResponse(res);
}

export async function submitApplication(id) {
  const res = await fetch(BASE + '/applications/' + id + '/submit', {
    method: 'POST',
    headers: authHeaders(),
  });
  return handleResponse(res);
}

export async function getApplicationCount() {
  const res = await fetch(BASE + '/applications/count', {
    headers: authHeaders(),
  });
  return handleResponse(res);
}

export async function updateApplicationStatus(id, status) {
  const res = await fetch(BASE + '/applications/' + id + '/status?status=' + status, {
    method: 'PUT',
    headers: authHeaders(),
  });
  return handleResponse(res);
}

// ---------- Documents ----------

export async function uploadDocument(applicationId, file) {
  const formData = new FormData();
  formData.append('applicationId', applicationId);
  formData.append('file', file);

  const token = getToken();
  const headers = {};
  if (token) {
    headers['Authorization'] = 'Bearer ' + token;
  }
  // Don't set Content-Type — browser sets it with boundary for multipart

  const res = await fetch(BASE + '/documents/upload', {
    method: 'POST',
    headers,
    body: formData,
  });
  return handleResponse(res);
}

export async function getDocument(id) {
  const res = await fetch(BASE + '/documents/' + id, {
    headers: authHeaders(),
  });
  return handleResponse(res);
}

export async function getDocumentsByApplication(applicationId) {
  const res = await fetch(BASE + '/documents/application/' + applicationId, {
    headers: authHeaders(),
  });
  return handleResponse(res);
}

export async function verifyDocument(id) {
  const res = await fetch(BASE + '/documents/' + id + '/verify', {
    method: 'PUT',
    headers: authHeaders(),
  });
  return handleResponse(res);
}

export async function deleteDocument(id) {
  const res = await fetch(BASE + '/documents/' + id, {
    method: 'DELETE',
    headers: authHeaders(),
  });
  return handleResponse(res);
}

// ---------- Admin ----------

export async function getAdminApplications() {
  const res = await fetch(BASE + '/admin/applications', {
    headers: authHeaders(),
  });
  return handleResponse(res);
}

export async function makeDecision(applicationId, status) {
  const res = await fetch(
    BASE + '/admin/applications/' + applicationId + '/decision?status=' + status,
    {
      method: 'POST',
      headers: authHeaders(),
    }
  );
  return handleResponse(res);
}

export async function getAdminStats() {
  const res = await fetch(BASE + '/admin/stats', {
    headers: authHeaders(),
  });
  return handleResponse(res);
}


export async function getAdminReports() {
  const res = await fetch(BASE + '/admin/reports', {
    headers: authHeaders(),
  });
  return handleResponse(res);
}

export async function generateAdminReport() {
  const res = await fetch(BASE + '/admin/reports/generate', {
    method: 'POST',
    headers: authHeaders(),
  });
  return handleResponse(res);
}

export async function getAdminUsers() {
  const res = await fetch(BASE + '/admin/users', {
    headers: authHeaders(),
  });
  return handleResponse(res);
}

export async function updateUserRole(userId, role) {
  const res = await fetch(BASE + '/admin/users/' + userId + '?role=' + role, {
    method: 'PUT',
    headers: authHeaders(),
  });
  return handleResponse(res);
}
