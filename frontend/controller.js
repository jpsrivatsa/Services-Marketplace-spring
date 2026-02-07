$(document).ready(function () {
    const token = localStorage.getItem('authToken');
    const role = localStorage.getItem('userRole');
    $('#servicerbecome').on('click', function (e) {
        e.preventDefault();
            var userConfirmed = window.confirm("Are you sure you want to logout and register as a partner?");
            if (userConfirmed) {
                localStorage.clear();
                window.location.href = 'login.html'
            } else {
            location.reload();
            }
      });
      $('#service-form').on('submit', function (e) {
        e.preventDefault();
        var category = $('#category').val();
        var address = $('#address').val();
        var description = $('#description').val();
        var price = parseFloat($('#price').val());
        var scheduledDate = $('#scheduledDate').val();
        var scheduledTime = $('#scheduledTime').val();
        var expectedCompletion = $('#expectedCompletion').val();
        var location = $('#location').val();
        var additionalComments = $('#additionalComments').val();
        $.ajax({
          url: base_url + 'api/services/create',
          method: 'POST',
          contentType: 'application/json',
          headers: {
            'Authorization': 'Bearer ' + token
        },
          data: JSON.stringify({
            category: category,
            address: address,
            description: description,
            price: price,
            scheduledDate: scheduledDate,
            scheduledTime: scheduledTime,
            expectedCompletion: expectedCompletion,
            additionalComments: additionalComments,
            location: location
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