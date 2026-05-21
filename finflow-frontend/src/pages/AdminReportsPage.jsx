import { useState, useEffect } from 'react'
import { getAdminReports, generateAdminReport } from '../api'

function AdminReportsPage() {
  const [report, setReport] = useState(null)
  const [history, setHistory] = useState([])
  const [loading, setLoading] = useState(true)
  const [generating, setGenerating] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    loadReports()
  }, [])

  async function loadReports() {
    setLoading(true)
    try {
      const result = await getAdminReports()
      if (result.success && result.data && result.data.length > 0) {
        setReport(result.data[0])
        setHistory(result.data)
      }
    } catch (err) {
      setError('Could not connect to server.')
    }
    setLoading(false)
  }

  async function handleGenerate() {
    setGenerating(true)
    setError('')
    try {
      const result = await generateAdminReport()
      if (result.success) {
        setReport(result.data)
        // Reload history
        const historyResult = await getAdminReports()
        if (historyResult.success) {
          setHistory(historyResult.data)
        }
      } else {
        setError(result.message || 'Failed to generate report.')
      }
    } catch (err) {
      setError('Could not connect to server.')
    }
    setGenerating(false)
  }

  if (loading) return <div className="page"><p>Loading...</p></div>

  return (
    <div className="page">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h1>Reports</h1>
        <button className="btn btn-success" onClick={handleGenerate} disabled={generating}>
          {generating ? 'Generating...' : '+ Generate New Report'}
        </button>
      </div>

      {error && <p className="error-msg">{error}</p>}

      {/* Current Report */}
      {report ? (
        <div>
          {/* Summary Stats */}
          <div className="stats-grid">
            <div className="stat-card">
              <div className="stat-value">{report.totalApplications}</div>
              <div className="stat-label">Total Applications</div>
            </div>
            <div className="stat-card">
              <div className="stat-value" style={{ color: '#2ecc71' }}>{report.approvedCount}</div>
              <div className="stat-label">Approved</div>
            </div>
            <div className="stat-card">
              <div className="stat-value" style={{ color: '#e74c3c' }}>{report.rejectedCount}</div>
              <div className="stat-label">Rejected</div>
            </div>
            <div className="stat-card">
              <div className="stat-value" style={{ color: '#f39c12' }}>{report.pendingCount}</div>
              <div className="stat-label">Pending</div>
            </div>
          </div>

          {/* Loan Amounts */}
          <div className="stats-grid">
            <div className="stat-card">
              <div className="stat-value" style={{ fontSize: '24px' }}>
                ₹{report.totalLoanAmount != null ? report.totalLoanAmount.toLocaleString() : '0'}
              </div>
              <div className="stat-label">Total Loan Amount</div>
            </div>
            <div className="stat-card">
              <div className="stat-value" style={{ fontSize: '24px', color: '#2ecc71' }}>
                ₹{report.approvedLoanAmount != null ? report.approvedLoanAmount.toLocaleString() : '0'}
              </div>
              <div className="stat-label">Approved Loan Amount</div>
            </div>
            <div className="stat-card">
              <div className="stat-value" style={{ fontSize: '24px' }}>
                {report.totalApplications > 0
                  ? ((report.approvedCount / report.totalApplications) * 100).toFixed(1) + '%'
                  : '0%'}
              </div>
              <div className="stat-label">Approval Rate</div>
            </div>
          </div>

          {/* Status Breakdown */}
          {report.statusBreakdown && Object.keys(report.statusBreakdown).length > 0 && (
            <div className="card">
              <h3>Status Breakdown</h3>
              <table className="data-table" style={{ marginTop: '10px' }}>
                <thead>
                  <tr>
                    <th>Status</th>
                    <th>Count</th>
                    <th>Percentage</th>
                  </tr>
                </thead>
                <tbody>
                  {Object.entries(report.statusBreakdown)
                    .sort((a, b) => b[1] - a[1])
                    .map(([status, count]) => (
                    <tr key={status}>
                      <td><span className={'badge badge-' + status}>{status}</span></td>
                      <td>{count}</td>
                      <td>{report.totalApplications > 0
                        ? ((count / report.totalApplications) * 100).toFixed(1) + '%'
                        : '0%'}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

          {/* Report Info */}
          <div className="card mt-20">
            <p style={{ fontSize: '13px', color: '#888' }}>
              <strong>Report ID:</strong> #{report.id} &nbsp;|&nbsp;
              <strong>Generated:</strong> {report.generatedAt ? new Date(report.generatedAt).toLocaleString() : 'N/A'} &nbsp;|&nbsp;
              <strong>Summary:</strong> {report.summary}
            </p>
          </div>
        </div>
      ) : (
        <div className="card">
          <p className="info-msg">No reports generated yet. Click "Generate New Report" to create one.</p>
        </div>
      )}

      {/* Report History */}
      {history.length > 1 && (
        <div className="mt-20">
          <h2 className="mb-10">Report History</h2>
          <table className="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Title</th>
                <th>Applications</th>
                <th>Approved</th>
                <th>Rejected</th>
                <th>Generated At</th>
              </tr>
            </thead>
            <tbody>
              {history.map(r => (
                <tr key={r.id} onClick={() => setReport(r)} style={{ cursor: 'pointer' }}>
                  <td>#{r.id}</td>
                  <td>{r.title}</td>
                  <td>{r.totalApplications}</td>
                  <td>{r.approvedCount}</td>
                  <td>{r.rejectedCount}</td>
                  <td>{r.generatedAt ? new Date(r.generatedAt).toLocaleString() : 'N/A'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}

export default AdminReportsPage
