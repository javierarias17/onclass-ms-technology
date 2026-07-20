--Dentro de la nueva conexión de onclass_technology

CREATE SCHEMA IF NOT EXISTS onclass_technology AUTHORIZATION technology_user;

CREATE TABLE IF NOT EXISTS onclass_technology.technologies (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(90) NOT NULL
);

CREATE TABLE IF NOT EXISTS onclass_technology.capability_technologies (
    id             BIGSERIAL PRIMARY KEY,
    capability_id  BIGINT NOT NULL,
    technology_id  BIGINT NOT NULL REFERENCES onclass_technology.technologies(id),
    UNIQUE (capability_id, technology_id)
);
