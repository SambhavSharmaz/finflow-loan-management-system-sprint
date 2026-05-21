import { useState, useEffect, Fragment } from 'react'
import { useParams } from 'react-router-dom'
import {
  getMyApplications,
  submitApplication,
  updateApplication,
  uploadDocument,
  getDocumentsByApplication,
  getDocument,
  deleteDocument,
} from '../api'

function ApplicationDetailPage() {
  const { id } = useParams()

  const [app, setApp] = useState(null)
  const [documents, setDocuments] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [file, setFile] = useState(null)
  const [uploading, setUploading] = useState(false)

  // Edit form state
  const [editing, setEditing] = useState(false)
  const [editData, setEditData] = useState({})



  // Document detail view state
  const [viewingDoc, setViewingDoc] = useState(null)

  useEffect(() => {
    loadData()
  }, [id])

  async function loadData() {
    setLoading(true)
    try {
      // Load application details
      const appsResult = await getMyApplications()
      if (appsResult.success) {
        const found = appsResult.data.find(a => a.id === parseInt(id))
        if (found) {
          setApp(found)
        } else {
          setError('Application not found.')
        }
      }

      // Load documents for this application
      const docsResult = await getDocumentsByApplication(id)
      if (docsResult.success) {
        setDocuments(docsResult.data)
      }
    } catch (err) {
      setError('Could not connect to server.')
    }
    setLoading(false)
  }

  async function handleSubmit() {
    if (!window.confirm('Submit this application? You cannot edit it after submission.')) return

    try {
      const result = await submitApplication(id)
      if (result.success) {
        setApp(result.data)
      } else {
        alert(result.message || 'Failed to submit.')
      }
    } catch (err) {
      alert('Could not connect to server.')
    }
  }

  // ---------- Edit ----------

  function startEditing() {
    setEditData({
      fullName: app.fullName || '',
      phone: app.phone || '',
      company: app.company || '',
      salary: app.salary || '',
      amount: app.amount || '',
      tenure: app.tenure || '',
      purpose: app.purpose || '',
    })
    setEditing(true)
  }

  function cancelEditing() {
    setEditing(false)
    setEditData({})
  }

  async function handleEditSave(e) {
    e.preventDefault()
    try {
      const payload = {
        fullName: editData.fullName,
        phone: editData.phone,
        company: editData.company,
        salary: parseFloat(editData.salary),
        amount: parseFloat(editData.amount),
        tenure: parseInt(editData.tenure),
        purpose: editData.purpose,
      }
      const result = await updateApplication(id, payload)
      if (result.success) {
        setApp(result.data)
        setEditing(false)
      } else {
        alert(result.message || 'Failed to update.')
      }
    } catch (err) {
      alert('Could not connect to server.')
    }
  }

  function handleEditChange(field, value) {
    setEditData({ ...editData, [field]: value })
  }



  // ---------- Document View ----------

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

  // ---------- Upload ----------

  async function handleUpload(e) {
    e.preventDefault()
    if (!file) return

    setUploading(true)
    try {
      const result = await uploadDocument(id, file)
      if (result.success) {
        setDocuments([...documents, result.data])
        setFile(null)
        // Reset the file input
        e.target.reset()
      } else {
        alert(result.message || 'Upload failed.')
      }
    } catch (err) {
      alert('Could not connect to server.')
    }
    setUploading(false)
  }

  async function handleDeleteDoc(docId) {
    if (!window.confirm('Delete this document?')) return

    try {
      const result = await deleteDocument(docId)
      if (result.success) {
        setDocuments(documents.filter(d => d.id !== docId))
        if (viewingDoc && viewingDoc.id === docId) {
          setViewingDoc(null)
        }
      }
    } catch (err) {
      alert('Could not connect to server.')
    }
  }

  if (loading) return <div className="page"><p>Loading...</p></div>
  if (error) return <div className="page"><p className="error-msg">{error}</p></div>
  if (!app) return <div className="page"><p>Application not found.</p></div>

  return (
    <div className="page">
      <h1>Application #{app.id}</h1>

      {/* ---------- Details / Edit Form ---------- */}
      <div className="card">
        {!editing ? (
          <>
            <div className="detail-grid">
              <div className="detail-item">
                <div className="detail-label">Full Name</div>
                <div>{app.fullName}</div>
              </div>
              <div className="detail-item">
                <div className="detail-label">Phone</div>
                <div>{app.phone}</div>
              </div>
              <div className="detail-item">
                <div className="detail-label">Company</div>
                <div>{app.company}</div>
              </div>
              <div className="detail-item">
                <div className="detail-label">Salary</div>
                <div>₹{app.salary?.toLocaleString()}</div>
              </div>
              <div className="detail-item">
                <div className="detail-label">Loan Amount</div>
                <div>₹{app.amount?.toLocaleString()}</div>
              </div>
              <div className="detail-item">
                <div className="detail-label">Tenure</div>
                <div>{app.tenure} months</div>
              </div>
              <div className="detail-item">
                <div className="detail-label">Purpose</div>
                <div>{app.purpose}</div>
              </div>
              <div className="detail-item">
                <div className="detail-label">Status</div>
                <div><span className={'badge badge-' + app.status}>{app.status}</span></div>
              </div>
            </div>

            <div className="actions">
              {app.status === 'DRAFT' && (
                <>
                  <button className="btn btn-success" onClick={handleSubmit}>Submit Application</button>
                  <button className="btn" onClick={startEditing}>Edit Application</button>
                </>
              )}
            </div>
          </>
        ) : (
          <>
            <h3 className="mb-10">Edit Application</h3>
            <form onSubmit={handleEditSave}>
              <div className="edit-grid">
                <div className="form-group">
                  <label>Full Name</label>
                  <input type="text" value={editData.fullName} onChange={(e) => handleEditChange('fullName', e.target.value)} required />
                </div>
                <div className="form-group">
                  <label>Phone</label>
                  <input type="text" value={editData.phone} onChange={(e) => handleEditChange('phone', e.target.value)} required pattern="[0-9]{10}" />
                </div>
                <div className="form-group">
                  <label>Company</label>
                  <input type="text" value={editData.company} onChange={(e) => handleEditChange('company', e.target.value)} required />
                </div>
                <div className="form-group">
                  <label>Salary (₹)</label>
                  <input type="number" value={editData.salary} onChange={(e) => handleEditChange('salary', e.target.value)} required min="1" />
                </div>
                <div className="form-group">
                  <label>Loan Amount (₹)</label>
                  <input type="number" value={editData.amount} onChange={(e) => handleEditChange('amount', e.target.value)} required min="1" />
                </div>
                <div className="form-group">
                  <label>Tenure (months)</label>
                  <input type="number" value={editData.tenure} onChange={(e) => handleEditChange('tenure', e.target.value)} required min="1" />
                </div>
              </div>
              <div className="form-group">
                <label>Purpose</label>
                <textarea value={editData.purpose} onChange={(e) => handleEditChange('purpose', e.target.value)} required />
              </div>
              <div className="actions">
                <button className="btn btn-success" type="submit">Save Changes</button>
                <button className="btn btn-danger" type="button" onClick={cancelEditing}>Cancel</button>
              </div>
            </form>
          </>
        )}
      </div>

      {/* ---------- Documents Section ---------- */}
      <h2 className="mt-20 mb-10">Documents</h2>

      <form onSubmit={handleUpload} className="card">
        <div className="form-group">
          <label>Upload a Document</label>
          <input type="file" onChange={(e) => setFile(e.target.files[0])} required />
        </div>
        <button className="btn btn-small" type="submit" disabled={uploading}>
          {uploading ? 'Uploading...' : 'Upload'}
        </button>
      </form>

      {documents.length === 0 && <p className="info-msg mt-10">No documents uploaded yet.</p>}

      {documents.length > 0 && (
        <table className="data-table mt-10">
          <thead>
            <tr>
              <th>ID</th>
              <th>File Name</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {documents.map(doc => (
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
                      <button className="btn btn-small btn-danger" onClick={() => handleDeleteDoc(doc.id)}>
                        Delete
                      </button>
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
  )
}

export default ApplicationDetailPage
