DROP TABLE IF EXISTS users;

CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       userId VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       nickname VARCHAR(255) NOT NULL,
                       name VARCHAR(255) NOT NULL,
                       birthdate VARCHAR(10) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       phone VARCHAR(50) NOT NULL,
                       profilePhoto VARCHAR(500)
);
