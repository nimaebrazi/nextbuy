-- Fixed UUIDs for deterministic integration tests (idempotent for multi-test classes)
INSERT INTO categories (id, name, slug)
VALUES ('11111111-1111-1111-1111-111111111111', 'Electronics', 'electronics')
ON CONFLICT (id) DO NOTHING;

INSERT INTO countries (id, name, iso_code, slug)
VALUES ('22222222-2222-2222-2222-222222222222', 'Testland', 'TL', 'testland')
ON CONFLICT (id) DO NOTHING;

INSERT INTO provinces (id, country_id, name, slug)
VALUES ('33333333-3333-3333-3333-333333333333', '22222222-2222-2222-2222-222222222222', 'Test Province', 'test-province')
ON CONFLICT (id) DO NOTHING;

INSERT INTO cities (id, province_id, name, slug)
VALUES ('44444444-4444-4444-4444-444444444444', '33333333-3333-3333-3333-333333333333', 'Test City', 'test-city')
ON CONFLICT (id) DO NOTHING;
