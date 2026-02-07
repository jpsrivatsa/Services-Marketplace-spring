package com.example.assistio.service;
import com.example.assistio.model.Services;
import com.example.assistio.model.User;
import com.example.assistio.repository.ServicesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
@Service
public class CustomServiceService {
    private final ServicesRepository servicesRepository;
    public CustomServiceService(ServicesRepository servicesRepository) {
        this.servicesRepository = servicesRepository;
    }
    private String generateRandomDigits(int length) {
        Random random = new Random();
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < length; i++) {
            digits.append(random.nextInt(10));  // Append a random digit
        }
        return digits.toString();
    }
    private String generateServiceRequestId() {
        String serviceId;
        do {
            serviceId = "SA" + generateRandomDigits(16);
        } while (servicesRepository.serviceIdExists(serviceId));
        return serviceId;
    }
    public List<String> getMissingParameters(Services service){
        List<String> missingFields = new ArrayList<>();
            if (service.getScheduledDate() == null) {
                missingFields.add("scheduled_date");
            }   
            if (service.getScheduledTime() == null) {
                missingFields.add("scheduled_time");
            }
            if (service.getCategory() == null || service.getCategory() == null || service.getCategory().trim().isEmpty()) {
                missingFields.add("category_short_name");
            }
            if (service.getPrice() == null || service.getPrice() <= 0) {
                missingFields.add("price");
            }
            if (service.getExpectedCompletion() == null) {
                missingFields.add("expected_completion");
            }
            if (service.getDescription() == null || service.getDescription().trim().isEmpty()) {
                missingFields.add("description");
            }
            return missingFields;
    }
    @Transactional
    public void createService(Services service, User user) {
        service.setServiceRequestId(generateServiceRequestId());
        servicesRepository.save(service, user);
    }
    public void updateServiceDynamic(Services service, User user){
        servicesRepository.updateServiceDynamic(service, user);
    }
    public Optional<Services> getServiceById(Long serviceId) {
        return servicesRepository.findById(serviceId);
    }
    public Optional<Services> getServiceByRequestId(String serviceId) {
        return servicesRepository.getServiceByRequestId(serviceId);
    }
    public List<Services> getAllServices(User user) {
        return servicesRepository.findAll(user);
    }
    public boolean isServiceAssigned(String serviceRequestId) {
        return servicesRepository.assignedToSomeone(serviceRequestId);
    }
    @Transactional
    public void assignToMe(String serviceRequestId, User user) {
        if (isServiceAssigned(serviceRequestId)) {
            throw new IllegalStateException("Service is already assigned.");
        }
        servicesRepository.assignToMe(serviceRequestId, user);
    }
    @Transactional
    public void fulfillService(String serviceRequestId, User user) {
        servicesRepository.setFulfilled(serviceRequestId, user);
    }
    @Transactional
    public void completeService(String serviceRequestId, User user) {
        servicesRepository.setCompleted(serviceRequestId, user);
    }
    @Transactional
    public void cancelService(String serviceRequestId, User user, Services service) {
        servicesRepository.cancelService(serviceRequestId, user, service);
    }
    public boolean checkOwnership(User user, String entity, String serviceRequestId) {
        return servicesRepository.hasOwnership(user, entity, serviceRequestId);
    }
    @Transactional
    public void deleteService(Long serviceId) {
        servicesRepository.delete(serviceId);
    }
    public List<Services> getServicesByServicer(String servicerUsername) {
        return servicesRepository.findByServicerUsername(servicerUsername);
    }
    public List<Services> getServicesByServicee(String serviceeUsername) {
        return servicesRepository.findByServiceeUsername(serviceeUsername);
    }
}