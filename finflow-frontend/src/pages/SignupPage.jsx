import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { signup } from '../api'

function SignupPage() {
  const navigate = useNavigate()

  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      const result = await signup(name, email, password)

      if (result.success) {
        // Signup worked — go to login page
        navigate('/login')
      } else {
        setError(result.message || 'Signup failed.')
      }
    } catch (err) {
      setError('Could not connect to server.')
    }

    setLoading(false)
  }

  return (
    <div className="page">
      <div className="form-card">
        <h2>Sign Up</h2>

        {error && <p className="error-msg">{error}</p>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Name</label>
            <input
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label>Email</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label>Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              minLength={4}
            />
          </div>

          <button className="btn" type="submit" disabled={loading}>
            {loading ? 'Signing up...' : 'Sign Up'}
          </button>
        </form>

        <p className="mt-10" style={{ fontSize: '14px' }}>
          Already have an account? <Link to="/login">Login</Link>
        </p>
      </div>
    </div>
  )
}

export default SignupPage
