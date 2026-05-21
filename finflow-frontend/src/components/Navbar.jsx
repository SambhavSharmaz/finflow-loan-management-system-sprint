import { Link, useNavigate } from 'react-router-dom'

function Navbar() {
  const navigate = useNavigate()

  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')
  const isLoggedIn = !!token
  const isAdmin = role === 'ROLE_ADMIN'

  function handleLogout() {
    localStorage.removeItem('token')
    localStorage.removeItem('role')
    navigate('/login')
  }

  return (
    <nav className="navbar">
      <Link to="/" className="brand">FinFlow</Link>
      <div className="nav-links">
        {!isLoggedIn && (
          <>
            <Link to="/">Home</Link>
            <Link to="/login">Login</Link>
            <Link to="/signup">Signup</Link>
          </>
        )}
        {isLoggedIn && !isAdmin && (
          <>
            <Link to="/applications">My Applications</Link>
            <Link to="/applications/new">New Application</Link>
            <button onClick={handleLogout}>Logout</button>
          </>
        )}
        {isLoggedIn && isAdmin && (
          <>
            <Link to="/admin">Dashboard</Link>
            <Link to="/admin/applications">Applications</Link>
            <Link to="/admin/reports">Reports</Link>
            <Link to="/admin/users">Users</Link>
            <button onClick={handleLogout}>Logout</button>
          </>
        )}
      </div>
    </nav>
  )
}

export default Navbar
