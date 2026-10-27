package org.iesalixar.daw2.rro.dwese2627_ticket_logger_webapp_user_roles.daos;

import org.iesalixar.daw2.rro.dwese2627_ticket_logger_webapp_user_roles.entities.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleDAOImpl implements RoleDAO {

    // Logger para registrar eventos importantes en el DAO
    private static final Logger logger = LoggerFactory.getLogger(RoleDAOImpl.class);

    private final JdbcTemplate jdbcTemplate;

    // Inyección de JdbcTemplate
    public RoleDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Lista todos los roles de la base de datos.
     * @return Lista de roles
     */
    @Override
    public List<Role> listAllRoles() {
        logger.info("Listing all roles from the database.");
        String sql = "SELECT id, name FROM roles ORDER BY id";
        List<Role> roles = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Role.class));
        logger.info("Retrieved {} roles from the database.", roles.size());
        return roles;
    }

    /**
     * Inserta un nuevo rol en la base de datos.
     * @param role Rol a insertar
     */
    @Override
    public void insertRole(Role role) {
        logger.info("Inserting role with name: {}", role.getName());
        String sql = "INSERT INTO roles (name) VALUES (?)";
        int rowsAffected = jdbcTemplate.update(sql, role.getName());
        logger.info("Inserted role. Rows affected: {}", rowsAffected);
    }

    /**
     * Actualiza un rol existente en la base de datos.
     * @param role Rol a actualizar
     */
    @Override
    public void updateRole(Role role) {
        logger.info("Updating role with id: {}", role.getId());
        String sql = "UPDATE roles SET name = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, role.getName(), role.getId());
        logger.info("Updated role. Rows affected: {}", rowsAffected);
    }

    /**
     * Elimina un rol de la base de datos.
     * @param id ID del rol a eliminar
     */
    @Override
    public void deleteRole(Long id) {
        logger.info("Deleting role with id: {}", id);
        String sql = "DELETE FROM roles WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        logger.info("Deleted role. Rows affected: {}", rowsAffected);
    }

    /**
     * Recupera un rol por su ID.
     * @param id ID del rol a recuperar
     * @return Rol encontrado o null si no existe
     */
    @Override
    public Role getRoleById(Long id) {
        logger.info("Retrieving role by id: {}", id);
        String sql = "SELECT id, name FROM roles WHERE id = ?";
        try {
            Role role = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Role.class), id);
            logger.info("Role retrieved: {}", role.getName());
            return role;
        } catch (Exception e) {
            logger.warn("No role found with id: {}", id);
            return null;
        }
    }

    /**
     * Verifica si ya existe un rol con el nombre indicado (sin distinguir mayúsculas).
     */
    @Override
    public boolean existsRoleByName(String name) {
        logger.info("Checking if role with name: {} exists", name);
        String sql = "SELECT COUNT(*) FROM roles WHERE UPPER(name) = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, name.toUpperCase());
        boolean exists = count != null && count > 0;
        logger.info("Role with name: {} exists: {}", name, exists);
        return exists;
    }

    /**
     * Verifica si existe otro rol (distinto del indicado por id) con el mismo nombre.
     */
    @Override
    public boolean existsRoleByNameAndNotId(String name, Long id) {
        logger.info("Checking if role with name: {} exists excluding id: {}", name, id);
        String sql = "SELECT COUNT(*) FROM roles WHERE UPPER(name) = ? AND id != ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, name.toUpperCase(), id);
        boolean exists = count != null && count > 0;
        logger.info("Role with name: {} exists excluding id {}: {}", name, id, exists);
        return exists;
    }

    /**
     * Indica si el rol está asignado a algún usuario (en cuyo caso no se debe borrar).
     */
    @Override
    public boolean isRoleInUse(Long id) {
        logger.info("Checking if role with id: {} is assigned to any user", id);
        String sql = "SELECT COUNT(*) FROM user_roles WHERE role_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        boolean inUse = count != null && count > 0;
        logger.info("Role with id: {} in use: {}", id, inUse);
        return inUse;
    }
}
