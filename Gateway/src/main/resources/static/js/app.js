// Global JS Configuration

const API_BASE = '/gateway';

function showAlert(message, type = 'error') {
    const alertBox = document.getElementById('alert-box');
    if(alertBox) {
        alertBox.className = `alert-${type}`;
        alertBox.innerText = message;
        alertBox.style.display = 'block';
        setTimeout(() => { alertBox.style.display = 'none'; }, 5000);
    } else {
        alert(message);
    }
}

function getAuthHeaders() {
    const token = localStorage.getItem('jwt_token');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

function getAuthHeadersMultipart() {
    const token = localStorage.getItem('jwt_token');
    return {
        'Authorization': `Bearer ${token}`
    };
}

function logout() {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user_role');
    window.location.href = 'login.html';
}

function checkAuthAndRole(expectedRole) {
    const token = localStorage.getItem('jwt_token');
    const role = localStorage.getItem('user_role');
    if (!token) {
        window.location.href = 'login.html';
        return false;
    }
    if (expectedRole && role !== expectedRole) {
        alert('Unauthorized acccess.');
        logout();
        return false;
    }
    return true;
}

function getStatusBadgeClass(status) {
    switch(status) {
        case 'DRAFT': return 'badge-draft';
        case 'SUBMITTED': return 'badge-submitted';
        case 'DOCS_PENDING': return 'badge-docspending';
        case 'DOCS_VERIFIED': return 'badge-docsverified';
        case 'APPROVED': return 'badge-approved';
        case 'REJECTED': return 'badge-rejected';
        default: return 'badge-draft';
    }
}
