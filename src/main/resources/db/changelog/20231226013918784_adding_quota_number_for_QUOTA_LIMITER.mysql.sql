-- liquibase formatted sql

-- changeset Luis Kupferberg:adding quota_number to limit access and save it somewhere
ALTER TABLE "user" ADD quota_number INT NULL;

