import { Link } from 'react-router-dom'

function LandingPage() {
  const products = [
    {
      name: 'Personal Loan',
      rate: '10.5%',
      maxAmount: '₹10,00,000',
      tenure: 'Up to 60 months',
      description: 'For personal expenses, weddings, travel, or emergencies.',
    },
    {
      name: 'Home Loan',
      rate: '8.5%',
      maxAmount: '₹50,00,000',
      tenure: 'Up to 240 months',
      description: 'Purchase your dream home with affordable EMIs.',
    },
    {
      name: 'Education Loan',
      rate: '9.0%',
      maxAmount: '₹20,00,000',
      tenure: 'Up to 84 months',
      description: 'Invest in your future with hassle-free education financing.',
    },
    {
      name: 'Vehicle Loan',
      rate: '11.0%',
      maxAmount: '₹15,00,000',
      tenure: 'Up to 72 months',
      description: 'Drive home your new car or bike today.',
    },
  ]

  return (
    <div>
      {/* Hero Section */}
      <div className="landing-hero">
        <h1>Welcome to FinFlow</h1>
        <p>Your trusted digital loan management platform. Apply, track, and manage loans — all in one place.</p>
        <div className="landing-hero-actions">
          <Link to="/signup" className="btn btn-success">Get Started</Link>
          <Link to="/login" className="btn">Login</Link>
        </div>
      </div>

      {/* Products Section */}
      <div className="page">
        <h2 className="mb-20" style={{ textAlign: 'center' }}>Our Loan Products</h2>

        <div className="products-grid">
          {products.map((product, index) => (
            <div className="product-card" key={index}>
              <h3>{product.name}</h3>
              <p className="product-desc">{product.description}</p>
              <div className="product-details">
                <div><strong>Interest Rate:</strong> {product.rate} p.a.</div>
                <div><strong>Max Amount:</strong> {product.maxAmount}</div>
                <div><strong>Tenure:</strong> {product.tenure}</div>
              </div>
              <Link to="/signup" className="btn btn-small" style={{ marginTop: '12px' }}>Apply Now</Link>
            </div>
          ))}
        </div>

        {/* How It Works */}
        <h2 className="mt-20 mb-20" style={{ textAlign: 'center' }}>How It Works</h2>

        <div className="steps-grid">
          <div className="step-card">
            <div className="step-number">1</div>
            <h4>Register</h4>
            <p>Create your free account in seconds.</p>
          </div>
          <div className="step-card">
            <div className="step-number">2</div>
            <h4>Apply</h4>
            <p>Fill the guided loan application wizard.</p>
          </div>
          <div className="step-card">
            <div className="step-number">3</div>
            <h4>Upload Documents</h4>
            <p>Submit your KYC and income documents.</p>
          </div>
          <div className="step-card">
            <div className="step-number">4</div>
            <h4>Get Approved</h4>
            <p>Track your application until approval.</p>
          </div>
        </div>
      </div>
    </div>
  )
}

export default LandingPage
