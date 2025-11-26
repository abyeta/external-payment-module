package org.jala.university.application.mapper;

import org.jala.university.domain.entity.ExternalService;
import org.jala.university.domain.entity.PaymentInvoice;
import org.jala.university.application.dto.PaymentInvoiceDto;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class PaymentInvoiceMapper {

  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

  public PaymentInvoiceDto toDto(PaymentInvoice entity) {
    if (entity == null) {
      return null;
    }

    ExternalService service = entity.getExternalService();

    String formattedDate = entity.getPaymentDate() != null
        ? entity.getPaymentDate().format(FORMATTER)
        : null;

    return PaymentInvoiceDto.builder()
        .id(entity.getId())
        .customerId(entity.getCustomer() !=  null ? entity.getCustomer().getId() : null)
        .externalServiceId(service != null ? service.getId() : null)
        .serviceName(service != null ? service.getProviderName() : null)
        .serviceEmail(service != null ? service.getEmail() : null)
        .serviceNumberReference(service != null ? service.getAccountReference() : null)
        .paymentDate(formattedDate)
        .amount(entity.getAmount())
        .pdfContent(entity.getPdfContent())
        .build();
  }

  public PaymentInvoice toEntity(PaymentInvoiceDto dto) {
      if (dto == null) {
          return null;
      }
      return PaymentInvoice.builder()
              .id(dto.getId())
              .amount(dto.getAmount())
              .pdfContent(dto.getPdfContent())
              .build();
  }

  public List<PaymentInvoiceDto> toDtoList(List<PaymentInvoice> entities) {
    if (entities == null || entities.isEmpty()) {
      return Collections.emptyList();
    }

    return entities.stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }
}
