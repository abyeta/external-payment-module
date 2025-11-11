package org.jala.university.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.jala.university.commons.domain.entity.BaseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a bank customer who can link external services for payments.
 * This is a simplified customer model for MVP purposes.
 * This class is final and not designed for extension.
 */
@Entity
@Table(name = "customers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class Customer implements BaseEntity<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "customer_services",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    @Builder.Default
    private List<ExternalService> linkedServices = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Override
    public UUID getId() {
        return id;
    }

    /**
     * Links an external service to this customer.
     * @param service the service to link
     */
    public void linkService(ExternalService service) {
        if (!linkedServices.contains(service)) {
            linkedServices.add(service);
        }
    }

    /**
     * Unlinks an external service from this customer.
     * @param service the service to unlink
     */
    public void unlinkService(ExternalService service) {
        linkedServices.remove(service);
    }

    /**
     * Checks if a service is already linked to this customer.
     * @param service the service to check
     * @return true if linked, false otherwise
     */
    public boolean hasLinkedService(ExternalService service) {
        return linkedServices.contains(service);
    }

    /**
     * Checks if a service (by ID) is already linked to this customer.
     * @param serviceId the service ID to check
     * @return true if linked, false otherwise
     */
    public boolean hasLinkedServiceById(UUID serviceId) {
        return linkedServices.stream()
                .anyMatch(s -> s.getId().equals(serviceId));
    }
}

