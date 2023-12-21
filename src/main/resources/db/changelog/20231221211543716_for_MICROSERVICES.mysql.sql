-- liquibase formatted sql

-- changeset Luis Kupferberg:creating first table
CREATE TABLE user (id VARCHAR(255) NOT NULL, first_name VARCHAR(255) NULL, last_login_utc datetime(6) NULL, last_name VARCHAR(255) NULL, CONSTRAINT userPK PRIMARY KEY (id));

