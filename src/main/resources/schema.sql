-- Crear tabla de roles
CREATE TABLE IF NOT EXISTS roles
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Crear tabla de usuarios
CREATE TABLE IF NOT EXISTS users
(
    id        INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(50)  NOT NULL UNIQUE,
    email     VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL
);

-- Tabla intermedia de la relación N:M entre usuarios y roles.
-- Al borrar un usuario se borran sus asignaciones; un rol asignado no se puede borrar (sin CASCADE).
CREATE TABLE IF NOT EXISTS user_roles
(
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id)
);
