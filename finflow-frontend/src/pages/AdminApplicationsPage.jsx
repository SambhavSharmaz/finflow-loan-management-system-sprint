import { useState, useEffect, Fragment } from 'react'
import { getAdminApplications, makeDecision, getDocumentsByApplication, verifyDocument, getDocument } from '../api'

function AdminApplicationsPage() {
  const [applications, setApplications] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [expandedAppId, setExpandedAppId] = useState(null)
  const [appDocs, setAppDocs] = useState([])
  const [docsLoading, setDocsLoading] = useState(false)

  // Document detail view
  const [viewingDoc, setViewingDoc] = useState(null)

  useEffect(() => {
    fetchApplications()
  }, [])

  async function fetchApplications() {
    setLoading(true)
    try {
      const data = await getAdminApplications()
      if (Array.isArray(data)) {
        setApplications(data)
      } else if (data.success) {
        setApplications(data.data)
      } else {
        setError(data.message || 'Failed to load applications.')
      }
    } catch (err) {
      setError('Could not connect to server.')
    }
    setLoading(false)
  }

  async function handleDecision(appId, status) {
    const label = status === 'APPROVED' ? 'approve' : 'reject'
    if (!window.confirm('Are you sure you want to ' + label + ' application #' + appId + '?')) return

    try {
      const result = await makeDecision(appId, status)
      if (result.success) {
        alert('Decision recorded: ' + status)
        fetchApplications()
      } else {
        alert(result.message || 'Failed to record decision.')
      }
    } catch (err) {
      alert('Could not connect to server.')
    }
  }

  // ---------- Documents ----------

  async function toggleDocuments(appId) {
    if (expandedAppId === appId) {
      setExpandedAppId(null)
      setAppDocs([])
      setViewingDoc(null)
      return
    }

    setDocsLoading(true)
    setExpandedAppId(appId)
    setViewingDoc(null)
    try {
      const result = await getDocumentsByApplication(appId)
      if (result.success) {
        setAppDocs(result.data)
      } else {
        setAppDocs([])
      }
    } catch (err) {
      setAppDocs([])
    }
    setDocsLoading(false)
  }

  async function handleVerifyDoc(docId) {
    if (!window.confirm('Mark document #' + docId + ' as verified?')) return

    try {
      const result = await verifyDocument(docId)
      if (result.success) {
        // Update the doc in the list
        setAppDocs(appDocs.map(d => d.id === docId ? result.data : d))
        alert('Document verified.')
      } else {
        alert(result.message || 'Failed to verify document.')
      }
    } catch (err) {
      alert('Could not connect to server.')
    }
  }

  async function handleViewDoc(docId) {
    if (viewingDoc && viewingDoc.id === docId) {
      setViewingDoc(null)
      return
    }
    try {
      const result = await getDocument(docId)
      if (result.success) {
        setViewingDoc(result.data)
      } else {
        alert(result.message || 'Failed to load document details.')
      }
    } catch (err) {
      alert('Could not connect to server.')
    }
  }

  if (loading) return <div className="page"><p>Loading...</p></div>

  return (
    <div className="page">
      <h1>All Applications (Admin)</h1>

      {error && <p className="error-msg">{error}</p>}

      {applications.length === 0 && <p className="info-msg">No applications found.</p>}

      {applications.length > 0 && (
        <table className="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Applicant</th>
              <th>Email</th>
              <th>Amount</th>
              <th>Purpose</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {applications.map(app => (
              <Fragment key={app.id}>
                <tr key={app.id}>
                  <td>{app.id}</td>
                  <td>{app.fullName}</td>
                  <td>{app.userEmail}</td>
                  <td>₹{app.amount?.toLocaleString()}</td>
                  <td>{app.purpose}</td>
                  <td><span className={'badge badge-' + (app.status || '')}>{app.status}</span></td>
                  <td>
                    <div className="actions" style={{ marginTop: 0 }}>
                      <button className="btn btn-small btn-success" onClick={() => handleDecision(app.id, 'APPROVED')}>
                        Approve
                      </button>
                      <button className="btn btn-small btn-danger" onClick={() => handleDecision(app.id, 'REJECTED')}>
                        Reject
                      </button>
                      <button className="btn btn-small" onClick={() => toggleDocuments(app.id)}>
                        {expandedAppId === app.id ? 'Hide Docs' : 'View Docs'}
                      </button>
                    </div>
                  </td>
                </tr>
                {expandedAppId === app.id && (
                  <tr key={app.id + '-docs'}>
                    <td colSpan="7">
                      <div className="doc-detail-panel">
                        <strong>Documents for Application #{app.id}</strong>
                        {docsLoading && <p>Loading documents...</p>}
                        {!docsLoading && appDocs.length === 0 && <p className="info-msg mt-10">No documents uploaded.</p>}
                        {!docsLoading && appDocs.length > 0 && (
                          <table className="data-table mt-10">
                            <thead>
                              <tr>
                                <th>Doc ID</th>
                                <th>File Name</th>
                                <th>Status</th>
                                <th>Actions</th>
                              </tr>
                            </thead>
                            <tbody>
                              {appDocs.map(doc => (
                                <Fragment key={doc.id}>
                                  <tr key={doc.id}>
                                    <td>{doc.id}</td>
                                    <td>{doc.fileName}</td>
                                    <td><span className={'badge badge-' + (doc.status || 'UPLOADED')}>{doc.status || 'UPLOADED'}</span></td>
                                    <td>
                                      <div className="actions" style={{ marginTop: 0 }}>
                                        <button className="btn btn-small" onClick={() => handleViewDoc(doc.id)}>
                                          {viewingDoc && viewingDoc.id === doc.id ? 'Hide' : 'View'}
                                        </button>
                                        {(doc.status !== 'VERIFIED') && (
                                          <button className="btn btn-small btn-success" onClick={() => handleVerifyDoc(doc.id)}>
                                            Verify
                                          </button>
                                        )}
                                      </div>
                                    </td>
                                  </tr>
                                  {viewingDoc && viewingDoc.id === doc.id && (
                                    <tr key={doc.id + '-detail'}>
                                      <td colSpan="4">
                                        <div className="doc-detail-panel">
                                          <div className="detail-grid">
                                            <div className="detail-item">
                                              <div className="detail-label">Document ID</div>
                                              <div>{viewingDoc.id}</div>
                                            </div>
                                            <div className="detail-item">
                                              <div className="detail-label">File Name</div>
                                              <div>{viewingDoc.fileName}</div>
                                            </div>
                                            <div className="detail-item">
                                              <div className="detail-label">Status</div>
                                              <div><span className={'badge badge-' + (viewingDoc.status || 'UPLOADED')}>{viewingDoc.status || 'UPLOADED'}</span></div>
                                            </div>
                                            <div className="detail-item">
                                              <div className="detail-label">Application ID</div>
                                              <div>{viewingDoc.applicationId}</div>
                                            </div>
                                            {viewingDoc.fileUrl && (
                                              <div className="detail-item">
                                                <div className="detail-label">File URL</div>
                                                <div><a href={viewingDoc.fileUrl} target="_blank" rel="noopener noreferrer">Open File</a></div>
                                              </div>
                                            )}
                                          </div>
                                        </div>
                                      </td>
                                    </tr>
                                  )}
                                </Fragment>
                              ))}
                            </tbody>
                          </table>
                        )}
                      </div>
                    </td>
                  </tr>
                )}
              </Fragment>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}

export default AdminApplicationsPage
