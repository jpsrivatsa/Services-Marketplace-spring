$(document).ready(function () {
    $.ajax({
        url: base_url + 'api/services/myServices',  // Adjust the API endpoint
        method: 'POST',
        contentType: 'application/json',
        headers: {
            'Authorization': 'Bearer ' + token  // Ensure you have the token variable available
        },
        success: function (data) {
            const tableBody = $('#serviceTable tbody');  // Adjust the table ID as needed
            tableBody.empty();  // Clear any existing rows

            // Loop through the data and append rows to the table
            data.forEach(function(service) {
                const row = $('<tr></tr>');
                row.append('<td>' + service.serviceRequestId + '</td>');
                row.append('<td>' + service.category + '</td>');
                row.append('<td>' + service.address + '</td>');
                row.append('<td>' + service.price + '</td>');
                row.append('<td>' + (service.servicer ? service.servicer.firstName : 'Unassigned') + '</td>');
                row.append('<td>' + service.status + '</td>');
                if(role === "user"){
                    row.append('<td>\
                        <a href="viewservice.html?servicerequestid=' + service.serviceRequestId + '" title="View"><i class="fas fa-eye"></i></a> \
                        <a href="editservice.html?servicerequestid=' + service.serviceRequestId + '" title="Edit"><i class="fas fa-pencil-alt"></i></a>\
                        <a href="cancel.html?servicerequestid=' + service.serviceRequestId + '" title="Cancel"><i class="fa fa-times"></i></a>\
                        <a href="markAsFulfilled.html?servicerequestid=' + service.serviceRequestId + '" title="Edit"><button class = "btn btn-primary py-3 px-4">Mark as Fulfilled</button></a>\
                      </td>');
                } else {
                    row.append('<td>\
                        <a href="assignToMe.html?servicerequestid=' + service.serviceRequestId + '" title="View"><button class = "btn btn-primary py-3 px-4">Assign to Me</button></a> \
                        <a href="markAsComplete.html?servicerequestid=' + service.serviceRequestId + '" title="Edit"><button class = "btn btn-primary py-3 px-4">Mark as Complete</button></a>\
                        <a href="viewservice.html?servicerequestid=' + service.serviceRequestId + '" title="View"><i class="fas fa-eye"></i></a> \
                      </td>');
                }
                
                tableBody.append(row);
            });
        },
        error: function (xhr, status, error) {
            console.error('Error fetching data: ' + error);
        }
    });
});