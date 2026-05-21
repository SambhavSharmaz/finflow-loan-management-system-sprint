import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { getMyApplications, deleteApplication, getApplicationCount } from '../api'

function MyApplicationsPage() {
  const [applications, setApplications] = useState([])
  const [count, setCount] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    fetchApplications()
  }, [])

  async function fetchApplications() {
    setLoading(true)
    try {
      const result = await getMyApplications()
      if (result.success) {
        setApplications(result.data)
      } else {
        setError(result.message || 'Failed to load applications.')
      }

      // Fetch total application count
      const countResult = await getApplicationCount()
      if (countResult.success) {
        setCount(countResult.data)
      }
    } catch (err) {
      setError('Could not connect to server.')
    }
    setLoading(false)
  }

  async function handleDelete(id) {
    if (!window.confirm('Are you sure you want to delete this application?')) return

    try {
      const result = await deleteApplication(id)
      if (result.success) {
        // Remove from list
        setApplications(applications.filter(app => app.id !== id))
        // Decrement count
        if (count !== null) setCount(count - 1)
      } else {
        alert(result.message || 'Failed to delete.')
      }
    } catch (err) {
      alert('Could not connect to server.')
    }
  }

  if (loading) return <div className="page"><p>Loading...</p></div>

  return (
    <div className="page">
      <h1>My Loan Applications</h1>

      {error && <p className="error-msg">{error}</p>}

      {/* Application Count */}
      {count !== null && (
        <div className="stats-grid mb-20">
          <div className="stat-card">
            <div className="stat-value">{count}</div>
            <div className="stat-label">Total Applications</div>
          </div>
          <div className="stat-card">
            <div className="stat-value">{applications.length}</div>
            <div className="stat-label">My Applications</div>
          </div>
        </div>
      )}

      <Link to="/applications/new" className="btn mb-20" style={{ display: 'inline-block', marginBottom: '20px', textDecoration: 'none' }}>
        + New Application
      </Link>

      {applications.length === 0 && <p className="info-msg">No applications yet.</p>}

      {applications.length > 0 && (
        <table className="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Amount</th>
              <th>Purpose</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {applications.map(app => (
              <tr key={app.id}>
                <td>{app.id}</td>
                <td>{app.fullName}</td>
                <td>₹{app.amount?.toLocaleString()}</td>
                <td>{app.purpose}</td>
                <td><span className={'badge badge-' + app.status}>{app.status}</span></td>
                <td>
                  <div className="actions">
                    <Link to={'/applications/' + app.id} className="btn btn-small">View</Link>
                    {app.status === 'DRAFT' && (
                      <button className="btn btn-small btn-danger" onClick={() => handleDelete(app.id)}>
                        Delete
                      </button>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}

export default MyApplicationsPage

