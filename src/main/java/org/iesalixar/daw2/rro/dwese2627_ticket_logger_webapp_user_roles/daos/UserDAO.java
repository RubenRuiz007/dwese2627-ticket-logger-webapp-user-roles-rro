package org.iesalixar.daw2.rro.dwese2627_ticket_logger_webapp_user_roles.daos;

import org.iesalixar.daw2.rro.dwese2627_ticket_logger_webapp_user_roles.entities.User;

import java.util.List;

public interface UserDAO {
    List<User> listAllUsers();

    void insertUser(User user);

    void updateUser(User user);

    void deleteUser(Long id);

    User getUserById(Long id);

    boolean existsUserByUsername(String username);

    boolean existsUserByUsernameAndNotId(String username, Long id);

    boolean existsUserByEmail(String email);

    boolean existsUserByEmailAndNotId(String email, Long id);
}
