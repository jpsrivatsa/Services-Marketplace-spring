const token = localStorage.getItem('authToken');
const role = localStorage.getItem('userRole')
$(document).ready(function () {
    if (!token) {
      alert("Login Credentials Expired. Kindly login again!");
      window.location.href = 'login.html';
    }
  });