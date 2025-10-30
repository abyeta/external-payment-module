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

  @Column(name = "provider_name", nullable = false, length = ValidationConstants.PROVIDER_NAME_MAX_LENGTH)
  private String providerName;

  @Column(name = "account_reference",
      nullable = false,
      length = ValidationConstants.ACCOUNT_REFERENCE_LENGTH,
      unique = true)
  private String accountReference;

  @Column(name = "phone_country_code", nullable = false, length = ValidationConstants.PHONE_COUNTRY_CODE_MAX_LENGTH)
  private String phoneCountryCode;

  @Column(name = "phone_number", nullable = false, length = ValidationConstants.PHONE_NUMBER_LENGTH)
  private String phoneNumber;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "contact_details", columnDefinition = "TEXT")
  private String contactDetails;

  @Column(name = "enabled", nullable = false)
  private boolean enabled = true;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Override
  public UUID getId() {
    return id;
  }
}
