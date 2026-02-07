package com.example.assistio.repository;
import com.example.assistio.model.Role;
import com.example.assistio.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;
    public UserRepository(JdbcTemplate jdbcTemplate, RoleRepository roleRepository) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private final RowMapper<Role> roleRowMapper = (rs, rowNum) -> {
        Role role = new Role();
        role.setName(rs.getString("name"));
        role.setServiceAccess(rs.getString("services"));
        return role;
    };
    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setPassword(rs.getString("password"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setAddress(rs.getString("address"));
        user.setCity(rs.getString("city"));
        user.setState(rs.getString("state"));
        user.setCountry(rs.getString("country"));
        user.setRole(findRoleByUsername(rs.getString("username")));
        return user;
    };
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, username);
        Optional<User> user = users.stream().findFirst();
        return user;
    }
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, email);
        return users.stream().findFirst();
    }
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        String sql = "SELECT * FROM users WHERE phone_number = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, phoneNumber);
        return users.stream().findFirst();
    }
    public void saveUser(User user) {
        String sql = "INSERT INTO users (username, email, phone_number, password, first_name, last_name, address, city, state, country, role_name) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getUsername(), user.getEmail(), user.getPhoneNumber(), user.getPassword(),
                user.getFirstName(), user.getLastName(), user.getAddress(), user.getCity(), user.getState(), user.getCountry(), user.getRole().getName());
    }
    public void resetPasswprd(User user) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        jdbcTemplate.update(sql, user.getPassword(), user.getUsername());
    }
    public boolean UsernameExists(String username){
        String sql = "SELECT * FROM users WHERE username = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, username);
        return !users.isEmpty();
    }
    public boolean EmailExists(String email){
        String sql = "SELECT * FROM users WHERE email = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, email);
        return !users.isEmpty();
    }
    public boolean PhoneExists(String phone){
        String sql = "SELECT * FROM users WHERE phone_number = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, phone);
        return !users.isEmpty();
    }
    public Role findRoleByUsername(String username) {
        String sql = "SELECT role_name FROM users WHERE username = ?";
        String role =  jdbcTemplate.queryForObject(sql, String.class, username);
        sql = "SELECT * FROM roles WHERE name = ?";
        List<Role> roles = jdbcTemplate.query(sql, roleRowMapper, role);
        return roles.stream().findFirst().get();
    }
}
