package org.jala.university.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.jala.university.commons.domain.entity.BaseEntity;
import jakarta.persistence.Lob;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table (name = "payment_invoice")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInvoice implements BaseEntity<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id",  nullable = false, updatable = false)
    private UUID id;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "amount", nullable = false, updatable = false)
    private Double amount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "external_service_id", nullable = false, updatable = false)
    private ExternalService externalService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, updatable = false)
    private Customer customer;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Lob
    @Basic(fetch = FetchType.LAZY)           //evita cargar el PDF en cada consulta
    @Column(name = "pdf_content", columnDefinition = "LONGBLOB")  //  longblob → 4GB
    private byte[] pdfContent;

}
