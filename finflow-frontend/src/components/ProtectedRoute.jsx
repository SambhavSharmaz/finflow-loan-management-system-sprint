import { Navigate } from 'react-router-dom'

function ProtectedRoute({ children, requireAdmin, requireUser }) {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  // Not logged in → go to login
  if (!token) {
    return <Navigate to="/login" />
  }

  // Route requires ADMIN role but user is not admin → redirect to user dashboard
  if (requireAdmin && role !== 'ROLE_ADMIN') {
    return <Navigate to="/applications" />
  }

  // Route requires USER role but user is admin → redirect to admin dashboard
  if (requireUser && role !== 'ROLE_USER') {
    return <Navigate to="/admin" />
  }

  return children
}

export default ProtectedRoute
