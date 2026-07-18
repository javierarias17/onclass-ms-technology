--Dentro de la conexión de postgres
DO
$$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'technology_user') THEN
        CREATE ROLE technology_user LOGIN PASSWORD 'vaca1234';
    END IF;
END
$$;

CREATE DATABASE onclass_technology OWNER technology_user;
