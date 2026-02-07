package com.example.assistio.model;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
@Entity
@Table(name = "services")
public class Services {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;
    @Column(name = "service_request_id", unique = true, nullable = false)
    private String serviceRequestId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicer_id", referencedColumnName = "username", nullable = true)
    private User servicer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicee_id", referencedColumnName = "username", nullable = true)
    private User servicee;
    @Column(nullable = false, name = "category_short_name")
    private String category;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private Double price;
    @Column(nullable = false)
    private LocalDate scheduledDate;
    @Column(nullable = false)
    private LocalTime scheduledTime;
    @Column
    private LocalDateTime expectedCompletion;
    @Column(nullable = false)
    private String status;
    @Column(nullable = true)
    private String location;
    @Column(length = 255)
    private String cancellationReason;
    @Column(columnDefinition = "TEXT")
    private String additionalComments;
    @ElementCollection
    @CollectionTable(name = "service_attachments", joinColumns = @JoinColumn(name = "service_id"))
    @Column(name = "attachment_url")
    private List<String> attachments;
    @Column(name = "created_by", nullable = false)
    private String createdBy;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    public Services() {}
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    public Long getServiceId() {
        return serviceId;
    }
    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }
    public String getServiceRequestId() {
        return serviceRequestId;
    }
    public void setServiceRequestId(String serviceRequestId) {
        this.serviceRequestId = serviceRequestId;
    }
    public User getServicer() {
        return servicer;
    }
    public void setServicer(User servicer) {
        this.servicer = servicer;
    }
    public User getServicee() {
        return servicee;
    }
    public void setServicee(User servicee) {
        this.servicee = servicee;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public LocalDate getScheduledDate() {
        return scheduledDate;
    }
    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }
    public LocalTime getScheduledTime() {
        return scheduledTime;
    }
    public void setScheduledTime(LocalTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
    public LocalDateTime getExpectedCompletion() {
        return expectedCompletion;
    }
    public void setExpectedCompletion(LocalDateTime expectedCompletion) {
        this.expectedCompletion = expectedCompletion;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getCancellationReason() {
        return cancellationReason;
    }
    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
    public String getAdditionalComments() {
        return additionalComments;
    }
    public void setAdditionalComments(String additionalComments) {
        this.additionalComments = additionalComments;
    }
    public List<String> getAttachments() {
        return attachments;
    }
    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }
    public String getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    public String getUpdatedBy() {
        return updatedBy;
    }
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
