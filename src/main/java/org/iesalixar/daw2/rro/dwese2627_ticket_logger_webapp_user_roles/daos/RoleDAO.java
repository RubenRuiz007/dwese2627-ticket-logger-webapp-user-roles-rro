package org.iesalixar.daw2.rro.dwese2627_ticket_logger_webapp_user_roles.daos;

import org.iesalixar.daw2.rro.dwese2627_ticket_logger_webapp_user_roles.entities.Role;

import java.util.List;

public interface RoleDAO {
    List<Role> listAllRoles();

    void insertRole(Role role);

    void updateRole(Role role);

    void deleteRole(Long id);

    Role getRoleById(Long id);

    boolean existsRoleByName(String name);

    boolean existsRoleByNameAndNotId(String name, Long id);

    boolean isRoleInUse(Long id);
}
