-- ============================================================================
-- Database Creation Script: External Payment Module
-- ============================================================================
-- Database: external_payment_db
-- Engine: MySQL 8.0+
-- ============================================================================

-- Create database if it doesn't exist
DROP DATABASE IF EXISTS external_payment_db;
CREATE DATABASE external_payment_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE external_payment_db;

-- ============================================================================
-- TABLE: customers
-- Description: Stores bank customers who can link services
-- ============================================================================
CREATE TABLE customers (
    id BINARY(16) PRIMARY KEY COMMENT 'Customer UUID',
    name VARCHAR(255) NOT NULL COMMENT 'Customer name',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Creation date'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Bank customers in the system';

-- ============================================================================
-- TABLE: holders
-- Description: Holders/legal representatives of external services
-- ============================================================================
CREATE TABLE holders (
    id BINARY(16) PRIMARY KEY COMMENT 'Holder UUID',
    name VARCHAR(255) NOT NULL COMMENT 'Holder name',
    identification_number VARCHAR(50) NOT NULL UNIQUE COMMENT 'Identification number',
    email VARCHAR(255) NOT NULL UNIQUE COMMENT 'Holder email',
    landline_phone VARCHAR(20) NOT NULL UNIQUE COMMENT 'Landline phone',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Creation date',
    updated_at DATETIME(6) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Update date',
    
    INDEX idx_holder_email (email),
    INDEX idx_holder_identification (identification_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Holders of external services';

-- ============================================================================
-- TABLE: external_services
-- Description: External services available for linking
-- ============================================================================
CREATE TABLE external_services (
    id BINARY(16) PRIMARY KEY COMMENT 'Service UUID',
    provider_name VARCHAR(255) NOT NULL UNIQUE COMMENT 'Provider name',
    account_reference VARCHAR(10) NOT NULL UNIQUE COMMENT 'Unique reference code',
    phone_country_code VARCHAR(5) NOT NULL COMMENT 'Phone country code',
    phone_number VARCHAR(10) NOT NULL UNIQUE COMMENT 'Phone number',
    email VARCHAR(255) NOT NULL UNIQUE COMMENT 'Service email',
    contact_details TEXT COMMENT 'Additional contact details',
    enabled BIT(1) NOT NULL DEFAULT 1 COMMENT 'Indicates if the service is active',
    contract_expiration DATE NULL COMMENT 'Contract expiration date',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Creation date',
    updated_at DATETIME(6) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Update date',
    holder_id BINARY(16) NOT NULL UNIQUE COMMENT 'Holder ID (FK)',
    
    CONSTRAINT fk_external_service_holder 
        FOREIGN KEY (holder_id) REFERENCES holders(id)
        ON DELETE RESTRICT 
        ON UPDATE CASCADE,
    
    INDEX idx_provider_name (provider_name),
    INDEX idx_email (email),
    INDEX idx_enabled (enabled),
    INDEX idx_account_reference (account_reference)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='External services for payments';

-- ============================================================================
-- TABLE: registrationdocument
-- Description: Registration documents associated with external services
-- ============================================================================
CREATE TABLE registration_document (
    id BINARY(16) PRIMARY KEY COMMENT 'Document UUID',
    name VARCHAR(255) NOT NULL COMMENT 'File name',
    file MEDIUMBLOB NOT NULL COMMENT 'File content',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Creation date',
    updated_at DATETIME(6) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Update date',
    service_id BINARY(16) NOT NULL COMMENT 'Service ID (FK)',
    
    CONSTRAINT fk_document_service 
        FOREIGN KEY (service_id) REFERENCES external_services(id)
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    
    INDEX idx_service_id (service_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Service registration documents';

-- ============================================================================
-- TABLE: customer_services (Many-to-Many Junction Table)
-- Description: Links between customers and external services
-- ============================================================================
CREATE TABLE customer_services (
    customer_id BINARY(16) NOT NULL COMMENT 'Customer ID (FK)',
    service_id BINARY(16) NOT NULL COMMENT 'Service ID (FK)',
    
    PRIMARY KEY (customer_id, service_id),
    
    CONSTRAINT fk_customer_service_customer 
        FOREIGN KEY (customer_id) REFERENCES customers(id)
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    
    CONSTRAINT fk_customer_service_service 
        FOREIGN KEY (service_id) REFERENCES external_services(id)
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    
    INDEX idx_customer_id (customer_id),
    INDEX idx_service_id (service_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Links between customers and external services';


