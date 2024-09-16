CREATE TABLE contacts (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100),
    address VARCHAR(255),
    phone VARCHAR(20),
    photo_url VARCHAR(255),
    status VARCHAR(20),
    title VARCHAR(100)
);