package com.example.assistio.repository;
import com.example.assistio.model.Role;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public class RoleRepository {
    private final JdbcTemplate jdbcTemplate;
    public RoleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Role> roleRowMapper = (rs, rowNum) -> {
        Role role = new Role();
        role.setName(rs.getString("name"));
        role.setServiceAccess(rs.getString("services"));
        return role;
    };

    public Optional<Role> findByName(String name) {
        String sql = "SELECT * FROM roles WHERE name = ?";
        List<Role> roles = jdbcTemplate.query(sql, roleRowMapper, name);
        return roles.stream().findFirst();
    }

    public void save(Role role) {
        String sql = "INSERT INTO roles (name, services) VALUES (?, ?)";
        jdbcTemplate.update(sql, role.getName(), role.getServiceAccess());
    }
}
