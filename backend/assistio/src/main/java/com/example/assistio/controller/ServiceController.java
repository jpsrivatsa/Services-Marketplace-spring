package com.example.assistio.controller;
import com.example.assistio.model.Services;
import com.example.assistio.model.User;
import com.example.assistio.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/services")
public class ServiceController {
    private final CustomServiceService serviceService;
    private final UserService userService;
    public ServiceController(CustomServiceService serviceService, UserService userService) {
        this.serviceService = serviceService;
        this.userService = userService;
    }
    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getUser(userDetails.getUsername());
    }
    @PostMapping("/create")
    public ResponseEntity<?> registerService(@RequestBody Services service) {
        User user = getCurrentUser();
        if (user == null || !user.getRole().getName().equals("user")) {
            return ResponseEntity
            .status(403)
            .body(Map.of("message", "You are logged in as a partner. Not allowed to create the service. Only user can create service"));
        }
        List<String> missingParameters = serviceService.getMissingParameters(service);
        if (!missingParameters.isEmpty()){
            return ResponseEntity
            .status(422) 
            .body(Map.of("message", "Missing Parameters ","Missing Fields", missingParameters));
        }
        serviceService.createService(service, user);
        return ResponseEntity.status(201).body(Map.of("message", "Service registered successfully"));
    }
    @PostMapping("/assignToMe/{serviceRequestId}")
    public ResponseEntity<?> registerServiceToMe(@PathVariable String serviceRequestId) {
        User user = getCurrentUser();
        if (user == null || !user.getRole().getName().equals("partner")) {
            return ResponseEntity
            .status(403)
            .body(Map.of("message", "Not allowed to assign service to you. Invalid account role"));
        }
        if(serviceService.isServiceAssigned(serviceRequestId) == true){
            return ResponseEntity.status(409).body(Map.of("message", "Service already assigned"));
        }
        serviceService.assignToMe(serviceRequestId, user);
        return ResponseEntity.status(200).body(Map.of("message", "Service is successfully assigned to you"));
    }
    @PostMapping("/markAsFulfilled/{serviceRequestId}")
    public ResponseEntity<?> markAsFulfilled(@PathVariable String serviceRequestId) {
        User user = getCurrentUser();
        Services service = serviceService.getServiceByRequestId(serviceRequestId).get();
        if (!service.getStatus().equals("Accepted")) {
            return ResponseEntity.status(200).body(Map.of("message", "Not allowed to modify as current status is " + service.getStatus()));
        }
        if (user == null || !user.getRole().getName().equals("user")) {
            return ResponseEntity
            .status(200)
            .body(Map.of("message", "Not allowed to set Status as fullfilled. Only Requester can set it as fulfilled."));
        }
        if(serviceService.checkOwnership(user,"user", serviceRequestId) == false){
            return ResponseEntity.status(401).body(Map.of("message", "Not allowed to modify this service as you do not own the service."));
        }
        if (!service.getStatus().equals("Accepted")){
            return ResponseEntity.status(401).body(Map.of("message", "Only services Accepted Status can be set as fulfilled"));
        }
        serviceService.fulfillService(serviceRequestId, user);
        return ResponseEntity.status(200).body(Map.of("message", "Service Successfully marked as Fulfilled."));
    }
    @PostMapping("/markAsCompleted/{serviceRequestId}")
    public ResponseEntity<?> markAsCompleted(@PathVariable String serviceRequestId) {
        User user = getCurrentUser();
        if (user == null || !user.getRole().getName().equals("partner")) {
            return ResponseEntity
            .status(403)
            .body(Map.of("message", "Not allowed to set Status as Completed. Only Partner can set it as complete"));
        }
        if(serviceService.checkOwnership(user,"partner", serviceRequestId) == false){
            return ResponseEntity.status(401).body(Map.of("message", "Not allowed to modify this service as you did not serve this service."));
        }
        Services service = serviceService.getServiceByRequestId(serviceRequestId).get();
        if (!service.getStatus().equals("Fulfilled")) {
            return ResponseEntity.status(200).body(Map.of("message", "Not allowed to modify as current status is " + service.getStatus()));
        }
        serviceService.completeService(serviceRequestId, user);
        return ResponseEntity.status(200).body(Map.of("message", "Service Successfully marked as Completed."));
    }
    @PostMapping("/cancelService/{serviceRequestId}")
    public ResponseEntity<?> cancelService(@PathVariable String serviceRequestId, @RequestParam String cancellationReason) {
        User user = getCurrentUser();
        if (user == null || !user.getRole().getName().equals("user")) {
            return ResponseEntity
            .status(403)
            .body(Map.of("message", "Cannot perform cancellation operation. Only requester can cancel the service"));
        }
        if (cancellationReason == null || cancellationReason.isEmpty()){
            return ResponseEntity.status(400).body(Map.of("message", "Please specify cancellation reason"));
        }
        if(serviceService.checkOwnership(user,"user", serviceRequestId) == false){
            return ResponseEntity.status(401).body(Map.of("message", "Not allowed to modify this service as you did not request this service."));
        }
        Services service = serviceService.getServiceByRequestId(serviceRequestId).get();
        if(service.getStatus().equals("Fulfilled") || service.getStatus().equals("Completed")){
            String msg = "Service with status " +  service.getStatus() + " Cannot be cancelled";
            return ResponseEntity.status(200).body(Map.of("message", msg));
            }
        serviceService.cancelService(serviceRequestId, user, service);
        return ResponseEntity.status(200).body(Map.of("message", "Service Successfully Marked as Cancelled."));
    }
    @PostMapping("/myServices")
    public ResponseEntity<?> getMyServices() {
        User user = getCurrentUser();
        if (user == null) {
            return ResponseEntity
            .status(403)
            .body(Map.of("message", "Unauthorised. Kindly Login"));
        }
        List<Services> services = serviceService.getAllServices(user);
            return new ResponseEntity<>(services, HttpStatus.OK);
    }

    @PostMapping("/getService/{serviceRequestId}")
    public ResponseEntity<?> getSingleService(@PathVariable String serviceRequestId) {
        User user = getCurrentUser();
        if(serviceService.checkOwnership(user,"all", serviceRequestId) == false){
            return ResponseEntity.status(401).body(Map.of("message", "Not allowed to view this service as you do not have permission"));
        }
        Services service = serviceService.getServiceByRequestId(serviceRequestId).get();
        return ResponseEntity.status(200).body(Map.of("service", service));
    }
    
    @PostMapping("update/{serviceRequestId}")
    public ResponseEntity<?> updateServices(@PathVariable String serviceRequestId, @RequestBody Services service) {
        User user = getCurrentUser();
        if (user == null || !user.getRole().getName().equals("user")) {
            return ResponseEntity
            .status(403)
            .body(Map.of("message", "You are not authorised to perform this action"));
        }
        if (serviceService.checkOwnership(user, "user", serviceRequestId) == false){
            return ResponseEntity
            .status(403)
            .body(Map.of("message", "Caution! You are not the owner of this service request. Operation failed"));
        }
        Services existing_service = serviceService.getServiceByRequestId(serviceRequestId).get();
        if (existing_service.getStatus().equals("Fulfilled") || existing_service.getStatus().equals("Completed")) {
            String msg = "Service with status " +  existing_service.getStatus() + " Cannot be updated";
            return ResponseEntity.status(401).body(Map.of("message", msg));
        }
        if (service.getDescription() != existing_service.getDescription() && service.getDescription() != null) {
            service.setDescription(existing_service.getDescription());
        }
        if (service.getPrice() != existing_service.getPrice() && service.getPrice() != null) {
            service.setPrice(existing_service.getPrice());
        }
        if (service.getScheduledDate() != existing_service.getScheduledDate() && service.getScheduledDate() != null) {
            service.setScheduledDate(existing_service.getScheduledDate());
        }
        if (service.getScheduledTime() != existing_service.getScheduledTime() && service.getScheduledTime() != null) {
            service.setScheduledTime(existing_service.getScheduledTime());
        }
        if (service.getExpectedCompletion() != existing_service.getExpectedCompletion() && service.getExpectedCompletion() != null) {
            service.setExpectedCompletion(existing_service.getExpectedCompletion());
        }
        if (service.getAdditionalComments() != existing_service.getAdditionalComments() && service.getAdditionalComments() != null) {
            service.setAdditionalComments(existing_service.getAdditionalComments());
        }
        serviceService.updateServiceDynamic(service, user);
        return ResponseEntity.status(200).body(Map.of("message", "Service Successfully updated."));
    }
}
