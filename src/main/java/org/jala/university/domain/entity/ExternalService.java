package org.jala.university.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jala.university.application.validator.ValidationConstants;
import org.jala.university.commons.domain.entity.BaseEntity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing an external service provider registration.
 * This entity stores information about service providers that can be
 * used for external payments.
 */
@Entity
@Table(name = "external_services")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class ExternalService implements BaseEntity<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Name of the service provider.
     * Must be alphanumeric with maximum 100 characters.
     */
    @Column(name = "provider_name", nullable = false, length = ValidationConstants.PROVIDER_NAME_MAX_LENGTH)
    private String providerName;

    /**
     * Account number or reference for the service provider.
     * Must be exactly 10 digits.
     */
    @Column(name = "account_reference",
            nullable = false,
            length = ValidationConstants.ACCOUNT_REFERENCE_LENGTH,
            unique = true)
    private String accountReference;

    /**
     * Phone country code (e.g., +52, +1, +591).
     */
    @Column(name = "phone_country_code", nullable = false, length = ValidationConstants.PHONE_COUNTRY_CODE_MAX_LENGTH)
    private String phoneCountryCode;

    /**
     * Phone number of the service provider contact.
     * Must be exactly 10 digits.
     */
    @Column(name = "phone_number", nullable = false, length = ValidationConstants.PHONE_NUMBER_LENGTH)
    private String phoneNumber;

    /**
     * Email address of the service provider contact.
     */
    @Column(name = "email", nullable = false)
    private String email;

    /**
     * Additional contact details or notes.
     */
    @Column(name = "contact_details", columnDefinition = "TEXT")
    private String contactDetails;


    /**
     * Timestamp when the service was created.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the service was last updated.
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Override
    public UUID getId() {
        return id;
    }
}

