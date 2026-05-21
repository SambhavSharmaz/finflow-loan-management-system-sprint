import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { login } from '../api'

function LoginPage() {
  const navigate = useNavigate()

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      const result = await login(email, password)

      if (result.success) {
        // Save token and role to localStorage
        localStorage.setItem('token', result.data.token)
        localStorage.setItem('role', result.data.role)

        // Redirect based on role
        if (result.data.role === 'ROLE_ADMIN') {
          navigate('/admin')
        } else {
          navigate('/applications')
        }
      } else {
        setError(result.message || 'Login failed.')
      }
    } catch (err) {
      setError('Could not connect to server.')
    }

    setLoading(false)
  }

  return (
    <div className="page">
      <div className="form-card">
        <h2>Login</h2>

        {error && <p className="error-msg">{error}</p>}

        <form onSubmit={handleSubmit}>
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
            />
          </div>

          <button className="btn" type="submit" disabled={loading}>
            {loading ? 'Logging in...' : 'Login'}
          </button>
        </form>

        <p className="mt-10" style={{ fontSize: '14px' }}>
          Don't have an account? <Link to="/signup">Sign up</Link>
        </p>
      </div>
    </div>
  )
}

export default LoginPage
