# dwese2627-ticket-logger-webapp-user-roles-rro

CRUD de usuarios y roles en Spring Boot (Thymeleaf + JdbcTemplate + MariaDB), con relación N:M:
un usuario puede tener varios roles y un rol puede estar en varios usuarios.
Los roles se asignan **solo desde el CRUD de usuarios**.

## Arranque
1. `docker compose up -d` (usa el fichero `.env`; la base de datos es `users-roles-webapp`).
2. Ejecutar `Dwese2627TicketLoggerWebappUserRolesRroApplication` desde el IDE.
3. Abrir http://localhost:8080 (`/users` y `/roles`). `schema.sql` y `data.sql` se cargan solos.
