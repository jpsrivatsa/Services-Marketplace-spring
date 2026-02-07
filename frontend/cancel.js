$(document).ready(function () {
    const serviceRequestID =  new URLSearchParams(window.location.search).get("servicerequestid");
    let userInput = prompt("Cancellation Reason:", "");
    $.ajax({
        url: base_url + '/api/services/cancelService/' + serviceRequestID,
        method: 'POST',
        dataType: 'json',
        headers: {
            'Authorization': 'Bearer ' + token  // Ensure you have the token variable available
        },
        data: {
            cancellationReason: userInput  // Sending data in the body
        },
        success: function (response) {
            alert(response.message);
            window.location.href = 'myservices.html';
        },
        error: function () {
          alert("Failed to load service details.");
        }
      });
    });