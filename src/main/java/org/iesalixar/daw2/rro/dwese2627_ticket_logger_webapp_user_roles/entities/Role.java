package org.iesalixar.daw2.rro.dwese2627_ticket_logger_webapp_user_roles.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa un rol de la aplicación (ADMIN, MANAGER, USER...).
 * La asignación de usuarios a un rol NO se gestiona desde esta entidad:
 * la relación se mantiene únicamente desde {@link User}.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    private Long id;

    @NotBlank(message = "{msg.role.name.notEmpty}")
    @Size(max = 50, message = "{msg.role.name.size}")
    private String name;

    public Role(String name) {
        this.name = name;
    }
}
