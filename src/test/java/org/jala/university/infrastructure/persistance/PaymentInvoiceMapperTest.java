package org.jala.university.infrastructure.persistance;

import org.jala.university.application.dto.PaymentInvoiceDto;
import org.jala.university.application.mapper.PaymentInvoiceMapper;
import org.jala.university.domain.entity.ExternalService;
import org.jala.university.domain.entity.PaymentInvoice;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaymentInvoiceMapperTest {

  private final PaymentInvoiceMapper mapper = new PaymentInvoiceMapper();

  @Test
  void mapsFieldsWell() {
    ExternalService service = new ExternalService();
    service.setProviderName("Telefono");
    service.setEmail("telefono@gmail.com");
    service.setAccountReference("acc123");

    PaymentInvoice invoice = new PaymentInvoice();
    invoice.setExternalService(service);
    invoice.setPaymentDate(LocalDateTime.of(2025, 10, 5, 14, 30));
    invoice.setAmount(65.99);

    PaymentInvoiceDto dto = mapper.toDto(invoice);

    assertEquals("Telefono", dto.getServiceName());
    assertEquals("telefono@gmail.com", dto.getServiceEmail());
    assertEquals("acc123", dto.getServiceNumberReference());
    assertEquals("05/10/2025 14:30", dto.getPaymentDate());
    assertEquals(65.99, dto.getAmount());
  }

  @Test
  void returnsNullWhenEntityIsNull() {
    assertNull(mapper.toDto(null));
  }

  @Test
  void ListHandlesNullOrEmptyInput() {
    assertTrue(mapper.toDtoList(null).isEmpty());
    assertTrue(mapper.toDtoList(List.of()).isEmpty());
  }

  @Test
  void mapsListCorrectly() {
    PaymentInvoice invoice = new PaymentInvoice();
    invoice.setAmount(50.0);

    List<PaymentInvoiceDto> result = mapper.toDtoList(List.of(invoice));

    assertEquals(1, result.size());
    assertEquals(50.0, result.get(0).getAmount());
  }

}

