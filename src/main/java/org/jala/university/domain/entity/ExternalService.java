package org.jala.university.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jala.university.application.validator.ValidationConstants;
import org.jala.university.commons.domain.entity.BaseEntity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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

    @Column(name = "provider_name", unique = true, nullable = false)
    private String providerName;

    @Column(name = "account_reference",
            nullable = false,
            length = ValidationConstants.ACCOUNT_REFERENCE_LENGTH,
            unique = true)
    private String accountReference;

    @Column(name = "phone_country_code", nullable = false, length = ValidationConstants.PHONE_COUNTRY_CODE_MAX_LENGTH)
    private String phoneCountryCode;

    @Column(name = "phone_number", unique = true, nullable = false, length = ValidationConstants.PHONE_NUMBER_LENGTH)
    private String phoneNumber;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "contact_details", columnDefinition = "TEXT")
    private String contactDetails;

    @Column(name="account_number")
    private Long accountNumber;

    @Builder.Default
    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @Builder.Default
    @Column(name = "contract_expiration")
    private LocalDate contractExpiration = null;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
    private Holder holder;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "externalService", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistrationDocument> documents;

    @Override
    public UUID getId() {
        return id;
    }
}

