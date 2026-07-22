CREATE TABLE categories
(
    id         uuid PRIMARY KEY      DEFAULT gen_random_uuid(),
    name       VARCHAR(100) NOT NULL,
    slug       VARCHAR(100) NOT NULL UNIQUE,
    parent_id  UUID REFERENCES categories (id) ON DELETE RESTRICT,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_categories_parent_id ON categories (parent_id);
