import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { createApplication } from '../api'

const STEPS = ['Personal', 'Employment', 'Loan Details', 'Review']

function NewApplicationPage() {
  const navigate = useNavigate()
  const [step, setStep] = useState(0)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  // Form data
  const [fullName, setFullName] = useState('')
  const [phone, setPhone] = useState('')
  const [company, setCompany] = useState('')
  const [salary, setSalary] = useState('')
  const [amount, setAmount] = useState('')
  const [tenure, setTenure] = useState('')
  const [purpose, setPurpose] = useState('')

  function nextStep() {
    // Simple validation per step
    if (step === 0) {
      if (!fullName.trim() || !phone.trim()) {
        setError('Please fill in all fields.')
        return
      }
      if (!/^[0-9]{10}$/.test(phone)) {
        setError('Phone must be 10 digits.')
        return
      }
    }
    if (step === 1) {
      if (!company.trim() || !salary) {
        setError('Please fill in all fields.')
        return
      }
    }
    if (step === 2) {
      if (!amount || !tenure || !purpose.trim()) {
        setError('Please fill in all fields.')
        return
      }
    }
    setError('')
    setStep(step + 1)
  }

  function prevStep() {
    setError('')
    setStep(step - 1)
  }

  async function handleSubmit() {
    setError('')
    setLoading(true)

    try {
      const data = {
        fullName,
        phone,
        company,
        salary: parseFloat(salary),
        amount: parseFloat(amount),
        tenure: parseInt(tenure),
        purpose,
      }

      const result = await createApplication(data)

      if (result.success) {
        navigate('/applications/' + result.data.id)
      } else {
        setError(result.message || 'Failed to create application.')
      }
    } catch (err) {
      setError('Could not connect to server.')
    }

    setLoading(false)
  }

  return (
    <div className="page">
      <div className="form-card" style={{ maxWidth: '580px' }}>
        <h2>New Loan Application</h2>

        {/* Stepper */}
        <div className="wizard-stepper">
          {STEPS.map((label, i) => (
            <div
              key={label}
              className={'wizard-step' + (i === step ? ' active' : '') + (i < step ? ' completed' : '')}
            >
              <div className="wizard-step-number">{i < step ? '✓' : i + 1}</div>
              <div className="wizard-step-label">{label}</div>
            </div>
          ))}
        </div>

        {error && <p className="error-msg">{error}</p>}

        {/* Step 1: Personal */}
        {step === 0 && (
          <div>
            <div className="form-group">
              <label>Full Name</label>
              <input type="text" value={fullName} onChange={(e) => setFullName(e.target.value)} placeholder="Enter your full name" />
            </div>
            <div className="form-group">
              <label>Phone (10 digits)</label>
              <input type="text" value={phone} onChange={(e) => setPhone(e.target.value)} placeholder="Enter phone number" maxLength={10} />
            </div>
          </div>
        )}

        {/* Step 2: Employment */}
        {step === 1 && (
          <div>
            <div className="form-group">
              <label>Company</label>
              <input type="text" value={company} onChange={(e) => setCompany(e.target.value)} placeholder="Enter company name" />
            </div>
            <div className="form-group">
              <label>Monthly Salary (₹)</label>
              <input type="number" value={salary} onChange={(e) => setSalary(e.target.value)} placeholder="Enter monthly salary" min="1" />
            </div>
          </div>
        )}

        {/* Step 3: Loan Details */}
        {step === 2 && (
          <div>
            <div className="form-group">
              <label>Loan Amount (₹)</label>
              <input type="number" value={amount} onChange={(e) => setAmount(e.target.value)} placeholder="Enter loan amount" min="1" />
            </div>
            <div className="form-group">
              <label>Tenure (months)</label>
              <input type="number" value={tenure} onChange={(e) => setTenure(e.target.value)} placeholder="Enter tenure in months" min="1" />
            </div>
            <div className="form-group">
              <label>Purpose</label>
              <textarea value={purpose} onChange={(e) => setPurpose(e.target.value)} placeholder="Describe the purpose of the loan" />
            </div>
          </div>
        )}

        {/* Step 4: Review */}
        {step === 3 && (
          <div>
            <div className="review-section">
              <h4>Personal Details</h4>
              <div className="detail-grid">
                <div className="detail-item">
                  <div className="detail-label">Full Name</div>
                  <div>{fullName}</div>
                </div>
                <div className="detail-item">
                  <div className="detail-label">Phone</div>
                  <div>{phone}</div>
                </div>
              </div>

              <h4>Employment Details</h4>
              <div className="detail-grid">
                <div className="detail-item">
                  <div className="detail-label">Company</div>
                  <div>{company}</div>
                </div>
                <div className="detail-item">
                  <div className="detail-label">Monthly Salary</div>
                  <div>₹{parseFloat(salary).toLocaleString()}</div>
                </div>
              </div>

              <h4>Loan Details</h4>
              <div className="detail-grid">
                <div className="detail-item">
                  <div className="detail-label">Loan Amount</div>
                  <div>₹{parseFloat(amount).toLocaleString()}</div>
                </div>
                <div className="detail-item">
                  <div className="detail-label">Tenure</div>
                  <div>{tenure} months</div>
                </div>
                <div className="detail-item">
                  <div className="detail-label">Purpose</div>
                  <div>{purpose}</div>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Navigation Buttons */}
        <div className="wizard-nav">
          {step > 0 && (
            <button className="btn" onClick={prevStep} type="button">← Back</button>
          )}
          {step < 3 && (
            <button className="btn btn-success" onClick={nextStep} type="button" style={{ marginLeft: 'auto' }}>Next →</button>
          )}
          {step === 3 && (
            <button className="btn btn-success" onClick={handleSubmit} disabled={loading} style={{ marginLeft: 'auto' }}>
              {loading ? 'Creating...' : 'Submit Application'}
            </button>
          )}
        </div>
      </div>
    </div>
  )
}

export default NewApplicationPage
