package org.jala.university.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jala.university.commons.domain.entity.BaseEntity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table (name = "RegistrationDocument")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDocument implements BaseEntity<UUID> {

    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    @Column (name = "id", nullable = false, updatable = false)
    private UUID id;

    @Lob
    @Column (name = "file", nullable = false, columnDefinition = "MEDIUMBLOB")
    private byte[] file;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn (name = "service_id", nullable = false, updatable = false)
    private ExternalService externalService;


    @Override
    public UUID getId() {
        return null;
    }
}
