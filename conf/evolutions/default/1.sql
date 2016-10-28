# Car adverts schema

# --- !Ups

CREATE TABLE caradvert (
  id VARCHAR(36) PRIMARY KEY,
  title LONGVARCHAR NOT NULL,
  fuel VARCHAR(255) NOT NULL check(fuel in ('GASOLINE', 'DIESEL')),
  price INT NOT NULL,
  isnew BOOLEAN NOT NULL,
  mileage INT,
  firstregistration DATE
);