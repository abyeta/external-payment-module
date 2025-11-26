package org.jala.university.infrastructure.external.dto.invoice;

import lombok.Builder;
import lombok.Value;
import org.jala.university.infrastructure.external.dto.subscription.SubscriptionResponse;

import java.math.BigDecimal;
import java.time.LocalDate;

@Value
@Builder
public class InvoiceResponse {
    Long invoiceNumber;
    SubscriptionResponse subscription;
    LocalDate issueDate;
    LocalDate dueDate;
    BigDecimal amount;
    String status;
}
