INSERT INTO users (id, email, password, username, role, created_at)
VALUES (
           gen_random_uuid(),
           'admin@logistics.com',
           '$2a$12$6hJUKWPaPqpBxA1Mb0.16OQrex.QmHlfJDdbVopvPYE8524zdY42m', -- 'password'
           'LogisticsAdmin',
           'ADMIN',
           NOW()
       );

INSERT INTO warehouses (id, location, capacity, available_space, manager_id)
VALUES (
           gen_random_uuid(),
           'Main Distribution Center',
           5000,
           3000,
           (SELECT id FROM users WHERE role = 'ADMIN' LIMIT 1)
       );

INSERT INTO vehicles (id, type, license_plate, capacity, current_location, driver_id)
VALUES
    (
        gen_random_uuid(),
        'TRUCK',
        'LOG-123',
        10000.0,
        'Warehouse',
        (SELECT id FROM users WHERE role = 'ADMIN' LIMIT 1)
    ),
    (
        gen_random_uuid(),
        'VAN',
        'LOG-456',
        5000.0,
        'Storage Facility',
        NULL
    );


INSERT INTO routes (id, vehicle_id, origin, destination, estimated_time, traffic_conditions)
VALUES
    (
        gen_random_uuid(),
        (SELECT id FROM vehicles WHERE type = 'TRUCK' LIMIT 1),
        'Warehouse A',
        'Client Address',
        '6 hours',
        'Moderate traffic'
    ),
    (
        gen_random_uuid(),
        (SELECT id FROM vehicles WHERE type = 'VAN' LIMIT 1),
        'Warehouse B',
        'Retail Store',
        '4 hours',
        'Heavy traffic'
    );
