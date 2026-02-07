$(document).ready(function () {
    const serviceRequestID =  new URLSearchParams(window.location.search).get("servicerequestid");
    $.ajax({
        url: 'http://localhost:8080/api/services/markAsCompleted/' + serviceRequestID,
        method: 'POST',
        dataType: 'json',
        headers: {
            'Authorization': 'Bearer ' + token  // Ensure you have the token variable available
        },
        success: function (response) {
            alert(response.message);
            window.location.href = 'myservices.html';
        },
        error: function () {
          alert("Failed to load service details.");
          window.location.href = 'myservices.html';
        }
      });
    });