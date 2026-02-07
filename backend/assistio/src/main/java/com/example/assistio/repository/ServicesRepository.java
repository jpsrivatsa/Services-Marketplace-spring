package com.example.assistio.repository;
import com.example.assistio.model.Services;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import com.example.assistio.model.User;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public class ServicesRepository {
    private final JdbcTemplate jdbcTemplate;
    private UserRepository userRepository;
    public ServicesRepository(JdbcTemplate jdbcTemplate, UserRepository userRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
    }
    private final RowMapper<Services> serviceRowMapper = (rs, rowNum) -> {
        Services service = new Services();
        service.setServiceId(rs.getLong("service_id"));
        service.setServiceRequestId(rs.getString("service_request_id"));
        service.setDescription(rs.getString("description"));
        service.setPrice(rs.getDouble("price"));
        service.setCategory(rs.getString("category_short_name"));
        service.setAddress(rs.getString("address"));
        service.setScheduledDate(rs.getDate("scheduled_date").toLocalDate());
        service.setScheduledTime(rs.getTime("scheduled_time").toLocalTime());
        service.setExpectedCompletion(rs.getTimestamp("expected_completion") != null ?
        rs.getTimestamp("expected_completion").toLocalDateTime() : null);
        service.setStatus(rs.getString("status"));
        service.setCancellationReason(rs.getString("cancellation_reason"));
        service.setAdditionalComments(rs.getString("additional_comments"));
        service.setLocation(rs.getString("location"));
        service.setCreatedBy(rs.getString("created_by"));
        service.setUpdatedBy(rs.getString("updated_by"));
        service.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        service.setUpdatedAt(rs.getTimestamp("updated_at") != null ?
        rs.getTimestamp("updated_at").toLocalDateTime() : null);
        service.setServicee(userRepository.findByUsername(rs.getString("servicee_id")).get()); 
        service.getServicee().removeSensitiveFields();
        if(!service.getStatus().equals("Requested")){
            service.setServicer(userRepository.findByUsername(rs.getString("servicer_id")).get()); 
            service.getServicer().removeSensitiveFields();
        }
        return service;
    };
    public Optional<Services> findById(Long serviceId) {
        String sql = "SELECT * FROM services WHERE service_request_id = ?";
        List<Services> services = jdbcTemplate.query(sql, serviceRowMapper, serviceId);
        return services.stream().findFirst();
    }
    public Optional<Services> getServiceByRequestId(String serviceId) {
        String sql = "SELECT * FROM services WHERE service_request_id = ?";
        List<Services> services = jdbcTemplate.query(sql, serviceRowMapper, serviceId);
        return services.stream().findFirst();
    }
    public List<Services> findAll(User user) {
        String sql = "";
        if (user.getRole().getName().equals("user")) {
            sql = "SELECT * FROM services WHERE servicee_id = ?";
        } else if (user.getRole().getName().equals("partner")){
            sql = "SELECT * FROM services WHERE servicer_id = ? OR servicer_id IS NULL";
        }  
        List<Services> services = jdbcTemplate.query(sql, serviceRowMapper, user.getUsername());  
        if (services.isEmpty() && user.getRole().getName().equals("partner")){
            sql = "SELECT * FROM services WHERE servicer_id IS NULL";
            services = jdbcTemplate.query(sql, serviceRowMapper); 
        }
        return services;
    }
    public void save(Services service, User user) {
        String sql = "INSERT INTO services (service_request_id, servicee_id, category_short_name, description, price, address, " +
                "scheduled_date, scheduled_time, expected_completion, status, location, cancellation_reason, additional_comments, " +
                "created_by, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                service.getServiceRequestId(),
                user.getUsername(),
                service.getCategory(),
                service.getDescription(),
                service.getPrice(),
                service.getAddress(),
                service.getScheduledDate(),
                service.getScheduledTime(),
                service.getExpectedCompletion(),
                "Requested",
                service.getLocation(),
                service.getCancellationReason(),
                service.getAdditionalComments(),
                user.getUsername(),
                service.getCreatedAt() != null ? service.getCreatedAt() : LocalDateTime.now()
        );
        Long serviceId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        if (service.getAttachments() != null) {
            for (String url : service.getAttachments()) {
                jdbcTemplate.update("INSERT INTO service_attachments (service_id, attachment_url) VALUES (?, ?)", serviceId, url);
            }
        }
    }
    public boolean serviceIdExists(String serviceId) {
        String sql = "SELECT COUNT(*) FROM services WHERE service_request_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, serviceId);
        return count != null && count > 0;
    }
    public void update(Services service) {
        String sql = "UPDATE services SET description = ?, price = ?, scheduled_date = ?, scheduled_time = ?, " +
                "expected_completion = ?, cancellation_reason = ?, additional_comments = ?, " +
                "updated_by = ?, updated_at = ? WHERE service_id = ?";
        jdbcTemplate.update(sql,
                service.getDescription(),
                service.getPrice(),
                service.getScheduledDate(),
                service.getScheduledTime(),
                service.getExpectedCompletion(),
                service.getCancellationReason(),
                service.getAdditionalComments(),
                service.getUpdatedBy(),
                LocalDateTime.now(),
                service.getServiceId()
        );
        jdbcTemplate.update("DELETE FROM service_attachments WHERE service_id = ?", service.getServiceId());
        if (service.getAttachments() != null) {
            for (String url : service.getAttachments()) {
                jdbcTemplate.update("INSERT INTO service_attachments (service_id, attachment_url) VALUES (?, ?)",
                        service.getServiceId(), url);
            }
        }
    }
    public int updateServiceDynamic(Services service, User user){
        String sql = "UPDATE services SET description = ?, price = ?, scheduled_date = ?, scheduled_time = ?, " +
                "expected_completion = ?, additional_comments = ?, " +
                "updated_by = ?, updated_at = ? WHERE service_request_id = ?";
        jdbcTemplate.update(sql,
                service.getDescription(),
                service.getPrice(),
                service.getScheduledDate(),
                service.getScheduledTime(),
                service.getExpectedCompletion(),
                service.getAdditionalComments(),
                user.getUsername(),
                LocalDateTime.now(),
                service.getServiceRequestId()
        );
        return 0;
    }
    public void assignToMe(String serviceRequest, User user){
        String sql = "UPDATE services SET servicer_id = ?, status = ?, updated_by = ?, updated_at = ? WHERE service_request_id = ?";
        jdbcTemplate.update(sql,
                user.getUsername(),
                "Accepted",
                user.getUsername(),
                LocalDateTime.now(),
                serviceRequest
        );
    }
    public boolean assignedToSomeone(String serviceRequest){
        String sql = "SELECT COUNT(*) FROM services where service_request_id = ? AND servicer_id IS NOT NULL";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, serviceRequest);
        return count != null && count > 0;
    }
    public void setFulfilled(String serviceRequest, User user){
        String sql = "UPDATE services SET status = ?, updated_by = ?, updated_at = ? WHERE service_request_id = ?";
        jdbcTemplate.update(sql,
                "Fulfilled",
                user.getUsername(),
                LocalDateTime.now(),
                serviceRequest
        );
    }
    public void setCompleted(String serviceRequest, User user){
        String sql = "UPDATE services SET status = ?, updated_by = ?, updated_at = ? WHERE service_request_id = ?";
        jdbcTemplate.update(sql,
                "Completed",
                user.getUsername(),
                LocalDateTime.now(),
                serviceRequest
        );
    }
    public void cancelService(String serviceRequest, User user, Services service){
        String sql = "UPDATE services SET status = ?, cancellation_reason = ?, updated_by = ?, updated_at = ? WHERE service_request_id = ?";
        jdbcTemplate.update(sql,
                "Cancelled",
                service.getCancellationReason(),
                user.getUsername(),
                LocalDateTime.now(),
                serviceRequest
        );
    }
    public boolean hasOwnership(User user, String entity, String serviceRequest){
        if (entity == "user") {
            String sql = "SELECT COUNT(*) FROM services where service_request_id = ? AND servicee_id = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, serviceRequest, user.getUsername());
            return count != null && count > 0;
        } else if (entity == "partner") {
            String sql = "SELECT COUNT(*) FROM services where service_request_id = ? AND servicer_id = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, serviceRequest, user.getUsername());
            return count != null && count > 0;
        } else if(entity == "all") {
            String sql = "SELECT COUNT(*) FROM services where service_request_id = ? AND ( servicer_id = ? OR servicee_id = ?)";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, serviceRequest, user.getUsername(), user.getUsername());
            return count != null && count > 0;
        }
        
        { return false;}
    }
    public void delete(Long serviceId) {
        jdbcTemplate.update("DELETE FROM service_attachments WHERE service_id = ?", serviceId);
        jdbcTemplate.update("DELETE FROM services WHERE service_id = ?", serviceId);
    }
    public List<Services> findByServicerUsername(String username) {
        String sql = "SELECT * FROM services WHERE servicer_username = ?";
        return jdbcTemplate.query(sql, serviceRowMapper, username);
    }
    public List<Services> findByServiceeUsername(String username) {
        String sql = "SELECT * FROM services WHERE servicee_username = ?";
        return jdbcTemplate.query(sql, serviceRowMapper, username);
    }
}
