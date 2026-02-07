$(document).ready(function () {
    const serviceRequestID =  new URLSearchParams(window.location.search).get("servicerequestid");
        if (!serviceRequestID) {
            alert("Service Request ID not found in URL.");
            window.history.back();
          }
    $.ajax({
        url: base_url + 'api/services/getService/' + serviceRequestID,
        method: 'POST',
        dataType: 'json',
        headers: {
            'Authorization': 'Bearer ' + token  // Ensure you have the token variable available
        },
        success: function (response) {
          const data = response['service'];
  
          const servicedetails = {
            "Service Request ID": data.serviceRequestId,
            "Category": data.category,
            "Description": data.description,
            "Address": data.address,
            "Price": data.price,
            "Scheduled Date": data.scheduledDate,
            "Scheduled Time": data.scheduledTime,
            "Location": data.location,
            "Expected Completion": data.expectedCompletion,
            "Status": data.status,
            "Created By": data.createdBy,
            "Created At": data.createdAt,
          }
          const userdetails = {
            "Email": data.servicee?.email,
            "Phone Number": data.servicee?.phoneNumber,
            "First Name": data.servicee?.firstName
          }

          const partnerdetails = {
            "Email": data.servicer?.email,
            "Phone Number": data.servicer?.phoneNumber,
            "First Name": data.servicer?.firstName
          }
  
          function populateTable(tableId, details) {
            const $table = $(tableId + ' tbody');
            $table.empty();
            $.each(details, function (key, value) {
              if (key === "Location"){
                $table.append('<tr><td><strong>' + key + '</strong></td><td><a href = ' + (value ?? '-') + ' <i class="fa fa-location"></i></a></td></tr>');
              } else {
                $table.append('<tr><td><strong>' + key + '</strong></td><td>' + (value ?? '-') + '</td></tr>');
              }
            });
          }
          
          // Populate the tables
          populateTable('#service-details-table', servicedetails);
          if (role === "user"){
            populateTable('#servicer-details-table', partnerdetails);
          } else if (role === "partner") {
            populateTable('#servicer-details-table', userdetails);
          }
        },
        error: function () {
          alert("Failed to load service details.");
        }
      });
    });