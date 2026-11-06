package org.iesalixar.daw2.rro.dwese2627_ticket_logger_webapp_user_roles.demo;

import jakarta.validation.Valid;
import org.iesalixar.daw2.rro.dwese2627_ticket_logger_webapp_user_roles.daos.RoleDAO;
import org.iesalixar.daw2.rro.dwese2627_ticket_logger_webapp_user_roles.daos.UserDAO;
import org.iesalixar.daw2.rro.dwese2627_ticket_logger_webapp_user_roles.entities.User;
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
 * CRUD de usuarios. Es el único sitio desde el que se asignan roles a los usuarios.
 */
@Controller
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String listUsers(Model model, Locale locale) {
        logger.info("Solicitando la lista de todos los usuarios...");
        try {
            List<User> listUsers = userDAO.listAllUsers();
            logger.info("Se han cargado {} usuarios.", listUsers.size());
            model.addAttribute("listUsers", listUsers);
        } catch (Exception e) {
            logger.error("Error al listar los usuarios: {}", e.getMessage());
            String errorMessage = messageSource.getMessage("msg.user-controller.list.error", null, locale);
            model.addAttribute("errorMessage", errorMessage);
        }
        return "views/user/user-list";
    }

    @GetMapping("/new")
    public String showNewForm(Model model, Locale locale) {
        logger.info("Mostrando formulario para nuevo usuario.");
        try {
            model.addAttribute("user", new User());
            model.addAttribute("listRoles", roleDAO.listAllRoles());
        } catch (Exception e) {
            logger.error("Error al cargar los roles para el formulario de usuario: {}", e.getMessage());
            String errorMessage = messageSource.getMessage("msg.user-controller.edit.error", null, locale);
            model.addAttribute("errorMessage", errorMessage);
        }
        return "views/user/user-form";
    }

    @PostMapping("/insert")
    public String insertUser(@Valid @ModelAttribute("user") User user,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model,
                             Locale locale) {
        logger.info("Insertando nuevo usuario con username {}", user.getUsername());
        try {
            if (result.hasErrors()) {
                // Volvemos a cargar los roles para los checkboxes cuando hay errores
                model.addAttribute("listRoles", roleDAO.listAllRoles());
                return "views/user/user-form";
            }
            if (userDAO.existsUserByUsername(user.getUsername())) {
                logger.warn("El username {} ya existe.", user.getUsername());
                String errorMessage = messageSource.getMessage("msg.user-controller.username.exist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/users/new";
            }
            if (userDAO.existsUserByEmail(user.getEmail())) {
                logger.warn("El email {} ya existe.", user.getEmail());
                String errorMessage = messageSource.getMessage("msg.user-controller.email.exist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/users/new";
            }
            userDAO.insertUser(user);
            logger.info("Usuario {} insertado con éxito.", user.getUsername());
        } catch (Exception e) {
            logger.error("Error al insertar el usuario {}: {}", user.getUsername(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.user-controller.insert.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/users";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id,
                               Model model,
                               RedirectAttributes redirectAttributes,
                               Locale locale) {
        logger.info("Mostrando formulario de edición para el usuario con ID {}", id);
        try {
            User user = userDAO.getUserById(id);
            if (user == null) {
                logger.warn("No se encontró el usuario con ID {}", id);
                String errorMessage = messageSource.getMessage("msg.user-controller.edit.notfound", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/users";
            }
            // user.roleIds ya contiene los roles actuales, así que aparecen marcados en el formulario
            model.addAttribute("user", user);
            model.addAttribute("listRoles", roleDAO.listAllRoles());
        } catch (Exception e) {
            logger.error("Error al obtener el usuario con ID {}: {}", id, e.getMessage());
            String errorMessage = messageSource.getMessage("msg.user-controller.edit.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/users";
        }
        return "views/user/user-form";
    }

    @PostMapping("/update")
    public String updateUser(@Valid @ModelAttribute("user") User user,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model,
                             Locale locale) {
        logger.info("Actualizando usuario con ID {}", user.getId());
        try {
            if (result.hasErrors()) {
                model.addAttribute("listRoles", roleDAO.listAllRoles());
                return "views/user/user-form";
            }
            if (userDAO.existsUserByUsernameAndNotId(user.getUsername(), user.getId())) {
                logger.warn("El username {} ya existe para otro usuario.", user.getUsername());
                String errorMessage = messageSource.getMessage("msg.user-controller.username.exist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/users/edit?id=" + user.getId();
            }
            if (userDAO.existsUserByEmailAndNotId(user.getEmail(), user.getId())) {
                logger.warn("El email {} ya existe para otro usuario.", user.getEmail());
                String errorMessage = messageSource.getMessage("msg.user-controller.email.exist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/users/edit?id=" + user.getId();
            }
            userDAO.updateUser(user);
            logger.info("Usuario con ID {} actualizado con éxito.", user.getId());
        } catch (Exception e) {
            logger.error("Error al actualizar el usuario con ID {}: {}", user.getId(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.user-controller.update.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/users";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        logger.info("Eliminando usuario con ID {}", id);
        try {
            userDAO.deleteUser(id);
            logger.info("Usuario con ID {} eliminado con éxito.", id);
        } catch (Exception e) {
            logger.error("Error al eliminar el usuario con ID {}: {}", id, e.getMessage());
            String errorMessage = messageSource.getMessage("msg.user-controller.delete.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/users";
    }
}