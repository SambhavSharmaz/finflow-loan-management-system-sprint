import { useState, useEffect } from 'react'
import { getAdminStats } from '../api'

function AdminDashboard() {
  const [stats, setStats] = useState({})
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    loadDashboard()
  }, [])

  async function loadDashboard() {
    setLoading(true)
    try {
      const statsResult = await getAdminStats()
      if (statsResult.success) {
        setStats(statsResult.data)
      }
    } catch (err) {
      setError('Could not connect to server.')
    }
    setLoading(false)
  }

  if (loading) return <div className="page"><p>Loading...</p></div>

  return (
    <div className="page">
      <h1>Admin Dashboard</h1>

      {error && <p className="error-msg">{error}</p>}

      {/* Stats Cards */}
      {(() => {
        const labels = {
          totalUsers: 'Total Users',
          totalApplications: 'Total Applications',
          approvedLoans: 'Approved Loans',
          rejectedLoans: 'Rejected Loans',
          pendingReview: 'Pending Review',
          totalDecisions: 'Total Decisions',
          totalLoanAmount: 'Total Loan Amount',
        }
        return (
          <div className="stats-grid">
            {Object.entries(stats).map(([key, value]) => (
              <div className="stat-card" key={key}>
                <div className="stat-value">
                  {key === 'totalLoanAmount' ? '₹' + Number(value).toLocaleString() : typeof value === 'number' ? value.toLocaleString() : String(value)}
                </div>
                <div className="stat-label">{labels[key] || key}</div>
              </div>
            ))}
          </div>
        )
      })()}
    </div>
  )
}

export default AdminDashboard

