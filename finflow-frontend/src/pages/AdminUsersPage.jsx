import { useState, useEffect } from 'react'
import { getAdminUsers, updateUserRole } from '../api'

function AdminUsersPage() {
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [updating, setUpdating] = useState(null)

  useEffect(() => {
    loadUsers()
  }, [])

  async function loadUsers() {
    setLoading(true)
    try {
      const result = await getAdminUsers()
      if (result.success) {
        setUsers(result.data)
      } else {
        setError(result.message || 'Failed to load users.')
      }
    } catch (err) {
      setError('Could not connect to server.')
    }
    setLoading(false)
  }

  async function handleRoleChange(userId, newRole) {
    setUpdating(userId)
    try {
      const result = await updateUserRole(userId, newRole)
      if (result.success) {
        setUsers(users.map(u => u.id === userId ? { ...u, role: result.data.role } : u))
      } else {
        alert(result.message || 'Failed to update role.')
      }
    } catch (err) {
      alert('Could not connect to server.')
    }
    setUpdating(null)
  }

  if (loading) return <div className="page"><p>Loading...</p></div>

  // Counts
  const totalUsers = users.length
  const adminCount = users.filter(u => u.role === 'ROLE_ADMIN').length
  const userCount = users.filter(u => u.role === 'ROLE_USER').length

  return (
    <div className="page">
      <h1>User Management</h1>

      {error && <p className="error-msg">{error}</p>}

      {/* Stats */}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-value">{totalUsers}</div>
          <div className="stat-label">Total Users</div>
        </div>
        <div className="stat-card">
          <div className="stat-value">{userCount}</div>
          <div className="stat-label">Applicants</div>
        </div>
        <div className="stat-card">
          <div className="stat-value">{adminCount}</div>
          <div className="stat-label">Admins</div>
        </div>
      </div>

      {/* Users Table */}
      {users.length === 0 && <p className="info-msg">No users found.</p>}

      {users.length > 0 && (
        <table className="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Email</th>
              <th>Role</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {users.map(user => (
              <tr key={user.id}>
                <td>{user.id}</td>
                <td>{user.name}</td>
                <td>{user.email}</td>
                <td>
                  <span className={'badge ' + (user.role === 'ROLE_ADMIN' ? 'badge-APPROVED' : 'badge-SUBMITTED')}>
                    {user.role === 'ROLE_ADMIN' ? 'Admin' : 'User'}
                  </span>
                </td>
                <td>
                  {updating === user.id ? (
                    <span style={{ fontSize: '13px', color: '#888' }}>Updating...</span>
                  ) : (
                    <select
                      value={user.role}
                      onChange={(e) => handleRoleChange(user.id, e.target.value)}
                      style={{ padding: '4px 8px', fontSize: '13px', borderRadius: '4px', border: '1px solid #ccc' }}
                    >
                      <option value="ROLE_USER">User</option>
                      <option value="ROLE_ADMIN">Admin</option>
                    </select>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}

export default AdminUsersPage
