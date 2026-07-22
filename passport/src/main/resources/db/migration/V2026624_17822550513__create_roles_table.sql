CREATE TABLE roles
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) UNIQUE,
    description VARCHAR(255) NULL
);

CREATE INDEX idx_roles_name ON roles (name);


create table user_roles
(
    role_id bigint not null
        constraint fk_users_roles_id references roles,
    user_id bigint not null
        constraint fk_users_users_id references users,
    primary key (role_id, user_id)
);

insert into roles(name, description)
VALUES ('ROLE_USER', 'NORMAL USER ROLE'),
       ('ROLE_ADMIN', 'ADMIN USER ROLE');