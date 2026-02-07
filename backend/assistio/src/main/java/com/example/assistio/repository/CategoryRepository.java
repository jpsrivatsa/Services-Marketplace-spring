package com.example.assistio.repository;
import com.example.assistio.model.Category;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public class CategoryRepository {
    private final JdbcTemplate jdbcTemplate;
    public CategoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private final RowMapper<Category> categoryRowMapper = (rs, rowNum) -> {
        Category category = new Category();
        category.setCategoryId(rs.getLong("category_id"));
        category.setShortName(rs.getString("short_name"));
        category.setLongName(rs.getString("long_name"));
        category.setDescription(rs.getString("description"));
        category.setImageUrl(rs.getString("image_url"));
        category.setActive(rs.getBoolean("active"));
        category.setCreatedBy(rs.getString("created_by"));
        category.setUpdatedBy(rs.getString("updated_by"));
        category.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        category.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
        return category;
    };
    public void saveCategory(Category category) {
        String sql = "INSERT INTO service_categories (short_name, long_name, description, image_url, active, created_by, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                category.getShortName(),
                category.getLongName(),
                category.getDescription(),
                category.getImageUrl(),
                category.isActive(),
                category.getCreatedBy(),
                category.getCreatedAt()
        );
    }
    public Optional<Category> findByShortName(String shortName) {
        String sql = "SELECT * FROM categories WHERE short_name = ?";
        List<Category> categories = jdbcTemplate.query(sql, categoryRowMapper, shortName);
        return categories.stream().findFirst();
    }
    public Optional<Category> findById(Long id) {
        String sql = "SELECT * FROM categories WHERE category_id = ?";
        List<Category> categories = jdbcTemplate.query(sql, categoryRowMapper, id);
        return categories.stream().findFirst();
    }
    public List<Category> findAll() {
        String sql = "SELECT * FROM categories";
        return jdbcTemplate.query(sql, categoryRowMapper);
    }
    public boolean shortNameExists(String shortName) {
        String sql = "SELECT COUNT(*) FROM categories WHERE short_name = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, shortName);
        return count != null && count > 0;
    }
    public void updateCategory(Category category) {
        String sql = "UPDATE categories SET long_name = ?, description = ?, image_url = ?, active = ?, updated_by = ?, updated_at = ? " +
                     "WHERE short_name = ?";
        jdbcTemplate.update(sql,
                category.getLongName(),
                category.getDescription(),
                category.getImageUrl(),
                category.isActive(),
                category.getUpdatedBy(),
                category.getUpdatedAt(),
                category.getShortName()
        );
    }
    public void deleteCategoryById(Long categoryId) {
        String sql = "DELETE FROM service_categories WHERE category_id = ?";
        jdbcTemplate.update(sql, categoryId);
    }
}
