package org.jala.university.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jala.university.commons.domain.entity.BaseEntity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a registration document for an external service.
 */
@Entity
@Table(name = "registration_document")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDocument implements BaseEntity<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String fileName;

    @Lob
    @Column(name = "file", nullable = false, columnDefinition = "MEDIUMBLOB")
    private byte[] file;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false, updatable = false)
    private ExternalService externalService;

    /**
     * Gets the ID of the registration document.
     *
     * @return the document ID
     */
    @Override
    public UUID getId() {
        return this.id;
    }
}
