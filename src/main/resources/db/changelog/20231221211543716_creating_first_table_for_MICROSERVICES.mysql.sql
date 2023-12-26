-- liquibase formatted sql

-- changeset Luis Kupferberg:creating first table context
CREATE TABLE "user" (id varchar(36) NOT NULL, disabled BOOLEAN NOT NULL, first_name VARCHAR(255) NULL, last_login_utc datetime(6) NULL, last_name VARCHAR(255) NULL, CONSTRAINT userPK PRIMARY KEY (id));

