-- Script para crear la tabla external_services
-- Base de datos: PostgreSQL / MySQL / H2 compatible

-- Eliminar tabla si existe (solo para desarrollo)
DROP TABLE IF EXISTS external_services;

-- Crear tabla external_services
CREATE TABLE external_services (
    id UUID PRIMARY KEY,
    provider_name VARCHAR(100) NOT NULL,
    account_reference VARCHAR(10) NOT NULL UNIQUE,
    phone_country_code VARCHAR(5) NOT NULL,
    phone_number VARCHAR(10) NOT NULL,
    email VARCHAR(255) NOT NULL,
    contact_details TEXT,
    enabled BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    
);
