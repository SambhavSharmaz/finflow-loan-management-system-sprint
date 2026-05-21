import { Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import ProtectedRoute from './components/ProtectedRoute'
import LandingPage from './pages/LandingPage'
import LoginPage from './pages/LoginPage'
import SignupPage from './pages/SignupPage'
import MyApplicationsPage from './pages/MyApplicationsPage'
import NewApplicationPage from './pages/NewApplicationPage'
import ApplicationDetailPage from './pages/ApplicationDetailPage'
import AdminDashboard from './pages/AdminDashboard'
import AdminApplicationsPage from './pages/AdminApplicationsPage'
import AdminReportsPage from './pages/AdminReportsPage'
import AdminUsersPage from './pages/AdminUsersPage'

function App() {
  return (
    <>
      <Navbar />
      <Routes>
        {/* Public routes */}
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />

        {/* User routes — applicants only */}
        <Route path="/applications" element={<ProtectedRoute requireUser><MyApplicationsPage /></ProtectedRoute>} />
        <Route path="/applications/new" element={<ProtectedRoute requireUser><NewApplicationPage /></ProtectedRoute>} />
        <Route path="/applications/:id" element={<ProtectedRoute requireUser><ApplicationDetailPage /></ProtectedRoute>} />

        {/* Admin routes */}
        <Route path="/admin" element={<ProtectedRoute requireAdmin><AdminDashboard /></ProtectedRoute>} />
        <Route path="/admin/applications" element={<ProtectedRoute requireAdmin><AdminApplicationsPage /></ProtectedRoute>} />
        <Route path="/admin/reports" element={<ProtectedRoute requireAdmin><AdminReportsPage /></ProtectedRoute>} />
        <Route path="/admin/users" element={<ProtectedRoute requireAdmin><AdminUsersPage /></ProtectedRoute>} />
      </Routes>
    </>
  )
}

export default App
