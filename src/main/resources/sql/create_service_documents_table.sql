-- Script para crear la tabla registration_documents
-- Base de datos: PostgreSQL / MySQL / H2 compatible

-- Eliminar tabla si existe (solo para desarrollo)
DROP TABLE IF EXISTS registration_document;

-- Crear tabla registration_documents
CREATE TABLE registration_document (
    id UUID PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file MEDIUMBLOB NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    service_id UUID NOT NULL,
    CONSTRAINT fk_service_document FOREIGN KEY (service_id) 
        REFERENCES external_services(id) ON DELETE CASCADE
);