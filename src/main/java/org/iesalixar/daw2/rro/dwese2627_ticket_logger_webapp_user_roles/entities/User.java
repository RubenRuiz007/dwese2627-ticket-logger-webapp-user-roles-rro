package org.iesalixar.daw2.rro.dwese2627_ticket_logger_webapp_user_roles.entities;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un usuario de la aplicación.
 * Un usuario puede tener uno o varios roles (relación N:M con {@link Role}).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long id;

    @NotBlank(message = "{msg.user.username.notEmpty}")
    @Size(min = 3, max = 50, message = "{msg.user.username.size}")
    private String username;

    @NotBlank(message = "{msg.user.email.notEmpty}")
    @Email(message = "{msg.user.email.invalid}")
    @Size(max = 100, message = "{msg.user.email.size}")
    private String email;

    @NotBlank(message = "{msg.user.fullName.notEmpty}")
    @Size(max = 100, message = "{msg.user.fullName.size}")
    private String fullName;

    // Roles completos del usuario (se usan para mostrarlos en listado y detalle).
    private List<Role> roles = new ArrayList<>();

    // Ids de los roles seleccionados en el formulario (alta / edición).
    @NotEmpty(message = "{msg.user.roles.notEmpty}")
    private List<Long> roleIds = new ArrayList<>();

    public User(String username, String email, String fullName) {
        this.username = username;
        this.email = email;
        this.fullName = fullName;
    }
}
