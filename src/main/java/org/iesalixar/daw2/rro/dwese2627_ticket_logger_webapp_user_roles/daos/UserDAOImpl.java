package org.iesalixar.daw2.rro.dwese2627_ticket_logger_webapp_user_roles.daos;

import org.iesalixar.daw2.rro.dwese2627_ticket_logger_webapp_user_roles.entities.Role;
import org.iesalixar.daw2.rro.dwese2627_ticket_logger_webapp_user_roles.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class UserDAOImpl implements UserDAO {

    // Logger para registrar eventos importantes en el DAO
    private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);

    private final JdbcTemplate jdbcTemplate;

    // Inyección de JdbcTemplate
    public UserDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Lista todos los usuarios de la base de datos, cada uno con sus roles.
     * @return Lista de usuarios
     */
    @Override
    public List<User> listAllUsers() {
        logger.info("Listing all users from the database.");
        String sql = "SELECT id, username, email, full_name AS fullName FROM users ORDER BY id";
        List<User> users = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
        for (User user : users) {
            loadRoles(user);
        }
        logger.info("Retrieved {} users from the database.", users.size());
        return users;
    }

    /**
     * Recupera un usuario por su ID, incluyendo sus roles.
     * @param id ID del usuario a recuperar
     * @return Usuario encontrado o null si no existe
     */
    @Override
    public User getUserById(Long id) {
        logger.info("Retrieving user by id: {}", id);
        String sql = "SELECT id, username, email, full_name AS fullName FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), id);
            loadRoles(user);
            logger.info("User retrieved: {}", user.getUsername());
            return user;
        } catch (Exception e) {
            logger.warn("No user found with id: {}", id);
            return null;
        }
    }

    /**
     * Inserta un nuevo usuario y sus roles en la base de datos.
     * @param user Usuario a insertar (roleIds contiene los roles elegidos)
     */
    @Override
    @Transactional
    public void insertUser(User user) {
        logger.info("Inserting user with username: {}", user.getUsername());
        String sql = "INSERT INTO users (username, email, full_name) VALUES (?, ?, ?)";
        // Necesitamos el id generado para poder rellenar la tabla intermedia user_roles
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getFullName());
            return ps;
        }, keyHolder);
        Long newId = keyHolder.getKey().longValue();
        saveRoles(newId, user.getRoleIds());
        logger.info("Inserted user with id: {}", newId);
    }

    /**
     * Actualiza un usuario existente y sustituye sus roles por los elegidos.
     * @param user Usuario a actualizar
     */
    @Override
    @Transactional
    public void updateUser(User user) {
        logger.info("Updating user with id: {}", user.getId());
        String sql = "UPDATE users SET username = ?, email = ?, full_name = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql,
                user.getUsername(), user.getEmail(), user.getFullName(), user.getId());
        jdbcTemplate.update("DELETE FROM user_roles WHERE user_id = ?", user.getId());
        saveRoles(user.getId(), user.getRoleIds());
        logger.info("Updated user. Rows affected: {}", rowsAffected);
    }

    /**
     * Elimina un usuario de la base de datos (sus filas de user_roles se borran en cascada).
     * @param id ID del usuario a eliminar
     */
    @Override
    public void deleteUser(Long id) {
        logger.info("Deleting user with id: {}", id);
        String sql = "DELETE FROM users WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        logger.info("Deleted user. Rows affected: {}", rowsAffected);
    }

    /**
     * Verifica si existe un usuario con el username indicado.
     */
    @Override
    public boolean existsUserByUsername(String username) {
        logger.info("Checking if user with username: {} exists", username);
        String sql = "SELECT COUNT(*) FROM users WHERE UPPER(username) = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username.toUpperCase());
        boolean exists = count != null && count > 0;
        logger.info("User with username: {} exists: {}", username, exists);
        return exists;
    }

    /**
     * Verifica si existe otro usuario (distinto del indicado por id) con el mismo username.
     */
    @Override
    public boolean existsUserByUsernameAndNotId(String username, Long id) {
        logger.info("Checking if user with username: {} exists excluding id: {}", username, id);
        String sql = "SELECT COUNT(*) FROM users WHERE UPPER(username) = ? AND id != ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username.toUpperCase(), id);
        boolean exists = count != null && count > 0;
        logger.info("User with username: {} exists excluding id {}: {}", username, id, exists);
        return exists;
    }

    /**
     * Verifica si existe un usuario con el email indicado.
     */
    @Override
    public boolean existsUserByEmail(String email) {
        logger.info("Checking if user with email: {} exists", email);
        String sql = "SELECT COUNT(*) FROM users WHERE UPPER(email) = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email.toUpperCase());
        boolean exists = count != null && count > 0;
        logger.info("User with email: {} exists: {}", email, exists);
        return exists;
    }

    /**
     * Verifica si existe otro usuario (distinto del indicado por id) con el mismo email.
     */
    @Override
    public boolean existsUserByEmailAndNotId(String email, Long id) {
        logger.info("Checking if user with email: {} exists excluding id: {}", email, id);
        String sql = "SELECT COUNT(*) FROM users WHERE UPPER(email) = ? AND id != ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email.toUpperCase(), id);
        boolean exists = count != null && count > 0;
        logger.info("User with email: {} exists excluding id {}: {}", email, id, exists);
        return exists;
    }

    /**
     * Carga en el usuario sus roles (objetos completos) y sus ids (para marcar los checkboxes).
     */
    private void loadRoles(User user) {
        String sql = "SELECT r.id, r.name FROM roles r " +
                "JOIN user_roles ur ON ur.role_id = r.id " +
                "WHERE ur.user_id = ? ORDER BY r.name";
        List<Role> roles = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Role.class), user.getId());
        user.getRoles().clear();
        user.getRoleIds().clear();
        for (Role role : roles) {
            user.getRoles().add(role);
            user.getRoleIds().add(role.getId());
        }
    }

    /**
     * Inserta en la tabla intermedia una fila por cada rol elegido.
     */
    private void saveRoles(Long userId, List<Long> roleIds) {
        if (roleIds == null) {
            return;
        }
        for (Long roleId : roleIds) {
            jdbcTemplate.update("INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)", userId, roleId);
        }
    }
}
