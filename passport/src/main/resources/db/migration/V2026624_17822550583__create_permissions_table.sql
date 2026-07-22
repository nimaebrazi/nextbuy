CREATE TABLE permissions
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) UNIQUE,
    description VARCHAR(255) NULL
);


CREATE INDEX idx_permissions_name ON permissions (name);

create table role_permissions
(
    permission_id bigint not null
        constraint fk_permission_id references permissions,

    role_id       bigint not null
        constraint fk_role_id references roles,

    primary key (permission_id, role_id)
);

CREATE INDEX idx_role_permissions_ids ON role_permissions (role_id, permission_id);

