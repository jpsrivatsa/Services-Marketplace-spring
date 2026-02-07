$(document).ready(function () {
    const serviceRequestID =  new URLSearchParams(window.location.search).get("servicerequestid");
        if (!serviceRequestID) {
            alert("Service Request ID not found in URL.");
            window.history.back();
          }
          $('#service-edit').on('submit', function (e) {
            e.preventDefault();
            var category = $('#Category').val();
            var description = $('#Description').val();
            var address = $('#Address').val();
            var price = parseFloat($('#Price').val());
            var scheduledDate = $('#Scheduled Date').val();
            var scheduledTime = $('#Scheduled Time').val();
            var expectedCompletion = $('#Expected Completion').val();
            var additionalComments = $('#Additional Comments').val();
            $.ajax({
              url: base_url + '/api/services/update/' + serviceRequestID,
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
                additionalComments: additionalComments
            }),
              success: function (response) {
                if (response.message) {
                  alert(response.message);
                  window.location.href = 'myservices.html'
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
    $.ajax({
        url: base_url + '/api/services/getService/' + serviceRequestID,
        method: 'GET',
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
            "Expected Completion": data.expectedCompletion,
            "Additional Comments": data.additionalComments,
            "Status": data.status,
            "Created By": data.createdBy,
            "Created At": data.createdAt,
          }
  
          function populateServiceForm(tableId, details) {
            const $table = $(tableId + ' tbody');
            $table.empty();
            $.each(details, function (key, value) {
              // Exclude non-editable fields: "Service Request ID", "Created By", "Created At"
              if (key === "Service Request ID" || key === "Created By" || key === "Created At") {
                $table.append(`
                  <tr>
                    <td><strong>${key}</strong></td>
                    <td>${value ?? '-'}</td>
                  </tr>
                `);
              } else {
                // Determine input type based on field value type
                let inputElement = '';
                if (key === "Scheduled Date") {
                  inputElement = `<input type="date" id="${key}" value="${value ? value.split('T')[0] : ''}" class="form-control" />`;
                } else if (key === "Expected Completion") {
                  inputElement = `<input type="datetime-local" id="${key}" value="${value ? value.split('T')[0] : ''}" class="form-control" />`;
                }
                else if (key === "Price") {
                  inputElement = `<input type="number" id="${key}" value="${value ?? ''}" class="form-control" />`;
                } else if (key === "Description" || key === "Additional Comments"){
                    inputElement = `<input type="textarea" id="${key}" value="${value ?? ''}" class="form-control" />`;
                } else {
                  inputElement = `<input type="text" id="${key}" value="${value ?? '-'}" class="form-control" />`;
                }
    
                // Add the row to the table
                $table.append(`
                  <tr>
                    <td><strong>${key}</strong></td>
                    <td>${inputElement}</td>
                  </tr>
                `);
              }
            });
          }
    
          // Populate service details table as a form with exclusions
          populateServiceForm('#service-edit-table', servicedetails);
        },
        error: function () {
          alert("Failed to load service details.");
        }
      });
    });