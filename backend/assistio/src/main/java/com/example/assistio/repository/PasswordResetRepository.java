package com.example.assistio.repository;
import com.example.assistio.model.PasswordResetToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.example.assistio.model.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public class PasswordResetRepository {
    @Autowired
    private final JdbcTemplate jdbcTemplate;
    private UserRepository userRepository;
    private final String INSERT_SQL = "INSERT INTO password_reset_tokens (token, expiry_date, username, created_at) VALUES (?, ?, ?, ?)";
    private final String SELECT_SQL = "SELECT * FROM password_reset_tokens WHERE token = ?";
    private final String SELECT_SQL_USER = "SELECT * FROM password_reset_tokens WHERE username = ?";
    private final String DELETE_SQL = "DELETE FROM password_reset_tokens WHERE token = ?";
    public PasswordResetRepository(JdbcTemplate jdbcTemplate, UserRepository userRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
    }
    public void saveToken(PasswordResetToken token) {
        jdbcTemplate.update(INSERT_SQL, token.getUser().getEmail(), token.getToken(), token.getExpiryDate());
    }
    private final RowMapper<PasswordResetToken> PasswordResetRowMapper = (rs, rowNum) -> {
        PasswordResetToken token = new PasswordResetToken();
        token.setId(rs.getLong("id"));
        token.setToken(rs.getString("token"));
        token.setExpiryDate(rs.getTimestamp("expiry_date").toLocalDateTime());
        token.setUser(userRepository.findByUsername(rs.getString("username")).get()); 
        return token;
    };
    public void insertToken(User user, PasswordResetToken prt){
        jdbcTemplate.update(this.INSERT_SQL,
                prt.getToken(),
                prt.getExpiryDate(),
                user.getUsername(),
                prt.getCreatedAt() != null ? prt.getCreatedAt() : LocalDateTime.now()
        );
    }
    public String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) return email;
        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        int halfLength = username.length() / 2;
        String visiblePart = username.substring(0, halfLength);
        String maskedPart = "*".repeat(username.length() - halfLength);
        return visiblePart + maskedPart + domain;
    }
    public Optional<PasswordResetToken> findByToken(String token) {
        List<PasswordResetToken> PRT = jdbcTemplate.query(this.SELECT_SQL, PasswordResetRowMapper, token);
        return PRT.stream().findFirst();
    }
    public Optional<PasswordResetToken> findByUsername(String username) {
        List<PasswordResetToken> PRT = jdbcTemplate.query(this.SELECT_SQL_USER, PasswordResetRowMapper, username);
        return PRT.stream().findFirst();
    }
    public void deleteToken(String token) {
        jdbcTemplate.update(DELETE_SQL, token);
    }
    public boolean validateToken(String token, String username) {
        try {
            String sql = "SELECT * FROM password_reset_tokens WHERE token = ? AND username = ?";
            PasswordResetToken resetToken = jdbcTemplate.queryForObject(sql, PasswordResetRowMapper, token, username);
            if (resetToken == null || resetToken.getExpiryDate() == null) {
                return false;
            }
            return !LocalDateTime.now().isAfter(resetToken.getExpiryDate());       
        } catch (EmptyResultDataAccessException e) {
                return false;
            }
        }
}
