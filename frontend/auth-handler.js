$(document).ready(function () {
    // Login form handler
    $('#login-form').on('submit', function (e) {
      e.preventDefault();
      var username = $('#username').val();
      var password = $('#password').val();
  
      $.ajax({
        url: base_url + 'api/auth/login',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
          username: username,
          password: password
        }),
        success: function (response) {
          if (response.token) {
            localStorage.setItem('authToken', response.token);
            if (response.role) {
                localStorage.setItem('userRole', response.role);
            }
            alert('Login successful!');
            if(localStorage.getItem("userRole") === 'user'){
                window.location.href = 'index.html';
            } else {
                window.location.href = 'myservices.html';
            }
                
          } else if(response.error.message){
            alert(response.error.message);
          } else {
            alert('Login failed. Please check your credentials.');
          }
        },
        error: function (error) {
          console.error('Error during login:', error);
          alert('An error occurred. Please try again.');
        }
      });
    });
  
    // Register form handler
    $('#register-form').on('submit', function (e) {
      e.preventDefault();
      var username = $('#username_reg').val();
      var email = $('#email').val();
      var phoneNumber = $('#phoneNumber').val();
      var password = $('#password_reg').val();
      var firstName = $('#firstName').val();
      var lastName = $('#lastName').val();
      var address = $('#address').val();
      var city = $('#city').val();
      var state = $('#state').val();
      var country = $('#country').val();
      var role = $('#role').val();
  
      $.ajax({
        url: base_url +  'api/auth/register',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
          username: username,
          email: email,
          phoneNumber: phoneNumber,
          password: password,
          firstName: firstName,
          lastName: lastName,
          address: address,
          city: city,
          state: state,
          country: country,
          role: {
            name: role
          }
        }),
        success: function (response) {
          if (response.message) {
            alert(response.message);
          } else if (response.error) {
            alert(response.error);
          } else {
            alert('Something Went Wrong. Please try again.');
          }
          location.reload();
        },
        error: function (error) {
          console.error('Error during registration:', error);
          alert('An error occurred. Please try again.');
        }
      });
    });
  });
  