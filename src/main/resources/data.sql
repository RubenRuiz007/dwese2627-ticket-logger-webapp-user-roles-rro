-- Roles de ejemplo
INSERT IGNORE INTO roles (id, name) VALUES
(1, 'ADMIN'),
(2, 'MANAGER'),
(3, 'USER');

-- Usuarios de ejemplo
INSERT IGNORE INTO users (id, username, email, full_name) VALUES
(1, 'admin', 'admin@ticketlogger.com', 'Administrador del Sistema'),
(2, 'maria', 'maria@ticketlogger.com', 'María López'),
(3, 'juan', 'juan@ticketlogger.com', 'Juan Pérez');

-- Asignación de roles a usuarios
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES
(1, 1),
(1, 3),
(2, 2),
(2, 3),
(3, 3);
