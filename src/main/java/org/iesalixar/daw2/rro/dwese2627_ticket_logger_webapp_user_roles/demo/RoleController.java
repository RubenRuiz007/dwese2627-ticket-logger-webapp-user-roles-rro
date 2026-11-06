package org.iesalixar.daw2.rro.dwese2627_ticket_logger_webapp_user_roles.demo;

import jakarta.validation.Valid;
import org.iesalixar.daw2.rro.dwese2627_ticket_logger_webapp_user_roles.daos.RoleDAO;
import org.iesalixar.daw2.rro.dwese2627_ticket_logger_webapp_user_roles.entities.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;

/**
 * CRUD de roles. Desde aquí NO se asignan usuarios a un rol (ni en alta ni en edición):
 * esa relación se gestiona únicamente desde el CRUD de usuarios.
 */
@Controller
@RequestMapping("/roles")
public class RoleController {

    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String listRoles(Model model, Locale locale) {
        logger.info("Solicitando la lista de todos los roles...");
        try {
            List<Role> listRoles = roleDAO.listAllRoles();
            logger.info("Se han cargado {} roles.", listRoles.size());
            model.addAttribute("listRoles", listRoles);
        } catch (Exception e) {
            logger.error("Error al listar los roles: {}", e.getMessage());
            String errorMessage = messageSource.getMessage("msg.role-controller.list.error", null, locale);
            model.addAttribute("errorMessage", errorMessage);
        }
        return "views/role/role-list";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Mostrando formulario para nuevo rol.");
        model.addAttribute("role", new Role());
        return "views/role/role-form";
    }

    @PostMapping("/insert")
    public String insertRole(@Valid @ModelAttribute("role") Role role,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        logger.info("Insertando nuevo rol con nombre {}", role.getName());
        try {
            if (result.hasErrors()) {
                return "views/role/role-form"; // Devuelve el formulario para mostrar los errores de validación
            }
            if (roleDAO.existsRoleByName(role.getName())) {
                logger.warn("El rol {} ya existe.", role.getName());
                String errorMessage = messageSource.getMessage("msg.role-controller.insert.nameExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/roles/new";
            }
            roleDAO.insertRole(role);
            logger.info("Rol {} insertado con éxito.", role.getName());
        } catch (Exception e) {
            logger.error("Error al insertar el rol {}: {}", role.getName(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.role-controller.insert.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/roles";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id,
                               Model model,
                               RedirectAttributes redirectAttributes,
                               Locale locale) {
        logger.info("Mostrando formulario de edición para el rol con ID {}", id);
        try {
            Role role = roleDAO.getRoleById(id);
            if (role == null) {
                logger.warn("No se encontró el rol con ID {}", id);
                String errorMessage = messageSource.getMessage("msg.role-controller.edit.notfound", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/roles";
            }
            model.addAttribute("role", role);
        } catch (Exception e) {
            logger.error("Error al obtener el rol con ID {}: {}", id, e.getMessage());
            String errorMessage = messageSource.getMessage("msg.role-controller.edit.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/roles";
        }
        return "views/role/role-form";
    }

    @PostMapping("/update")
    public String updateRole(@Valid @ModelAttribute("role") Role role,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        logger.info("Actualizando rol con ID {}", role.getId());
        try {
            if (result.hasErrors()) {
                return "views/role/role-form";
            }
            if (roleDAO.existsRoleByNameAndNotId(role.getName(), role.getId())) {
                logger.warn("El rol {} ya existe para otro rol.", role.getName());
                String errorMessage = messageSource.getMessage("msg.role-controller.update.nameExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/roles/edit?id=" + role.getId();
            }
            roleDAO.updateRole(role);
            logger.info("Rol con ID {} actualizado con éxito.", role.getId());
        } catch (Exception e) {
            logger.error("Error al actualizar el rol con ID {}: {}", role.getId(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.role-controller.update.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/roles";
    }

    @PostMapping("/delete")
    public String deleteRole(@RequestParam("id") Long id,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        logger.info("Eliminando rol con ID {}", id);
        try {
            // Un rol asignado a algún usuario no se puede borrar: primero hay que quitarlo desde el CRUD de usuarios.
            if (roleDAO.isRoleInUse(id)) {
                logger.warn("El rol con ID {} está asignado a usuarios y no se puede eliminar.", id);
                String errorMessage = messageSource.getMessage("msg.role-controller.delete.inUse", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/roles";
            }
            roleDAO.deleteRole(id);
            logger.info("Rol con ID {} eliminado con éxito.", id);
        } catch (Exception e) {
            logger.error("Error al eliminar el rol con ID {}: {}", id, e.getMessage());
            String errorMessage = messageSource.getMessage("msg.role-controller.delete.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/roles";
    }
}