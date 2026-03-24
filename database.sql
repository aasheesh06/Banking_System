CREATE DATABASE banking_system;

USE banking_system;

CREATE TABLE user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    password VARCHAR(100)
);

CREATE TABLE accounts (
    account_number BIGINT PRIMARY KEY,
    full_name VARCHAR(100),
    email VARCHAR(100),
    balance DOUBLE,
    security_pin INT
);
