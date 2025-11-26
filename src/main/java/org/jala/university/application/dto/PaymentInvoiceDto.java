package org.jala.university.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInvoiceDto {
  private UUID id;
  private String serviceName;
  private String serviceEmail;
  private String serviceNumberReference;
  private String paymentDate;
  private Double amount;
  private Long customerId;
  private UUID externalServiceId;
  private byte[] pdfContent;
}

