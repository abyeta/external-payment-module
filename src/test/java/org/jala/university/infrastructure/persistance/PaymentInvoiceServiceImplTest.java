package org.jala.university.infrastructure.persistance;

import org.jala.university.application.dto.PaymentInvoiceDto;
import org.jala.university.application.mapper.PaymentInvoiceMapper;
import org.jala.university.application.service.PaymentInvoiceService;
import org.jala.university.application.service.impl.PaymentInvoiceServiceImpl;
import org.jala.university.domain.entity.PaymentInvoice;
import org.jala.university.domain.repository.CustomerRepository;
import org.jala.university.domain.repository.ExternalServiceRepository;
import org.jala.university.domain.repository.PaymentInvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentInvoiceServiceImplTest {

  @Mock private PaymentInvoiceRepository rep;
  @Mock private PaymentInvoiceMapper mapper;
  @Mock private CustomerRepository customerRepository;
  @Mock private ExternalServiceRepository externalServiceRepository;
  private PaymentInvoiceService service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new PaymentInvoiceServiceImpl(rep, customerRepository, externalServiceRepository, mapper);
  }

  @Test
  void returnsDtosWhenCustomerIdIsValid() {
    Long id = 1L;

    List<PaymentInvoice> entities = List.of(new PaymentInvoice());
    List<PaymentInvoiceDto> dtos = List.of(new PaymentInvoiceDto());

    when(rep.findByCustomerId(id)).thenReturn(entities);
    when(mapper.toDtoList(entities)).thenReturn(dtos);

    List<PaymentInvoiceDto> result = service.findByCustomerId(id);

    assertEquals(dtos, result);
    verify(rep).findByCustomerId(id);
    verify(mapper).toDtoList(entities);
  }

  @Test
  void returnsDtosWhenServiceIdIsValid() {
    UUID id = UUID.randomUUID();

    List<PaymentInvoice> entities = List.of(new PaymentInvoice());
    List<PaymentInvoiceDto> dtos = List.of(new PaymentInvoiceDto());

    when(rep.findByServiceId(id)).thenReturn(entities);
    when(mapper.toDtoList(entities)).thenReturn(dtos);

    List<PaymentInvoiceDto> result = service.findByServiceId(id);

    assertEquals(dtos, result);
    verify(rep).findByServiceId(id);
    verify(mapper).toDtoList(entities);
  }

    @Test
    void savePaymentInvoiceDtoValid() {
      // Arrange
      Long customerId = 1L;
      UUID externalServiceId = UUID.randomUUID();
      
      PaymentInvoiceDto dto = PaymentInvoiceDto.builder()
              .customerId(customerId)
              .externalServiceId(externalServiceId)
              .build();
      
      PaymentInvoice entity = PaymentInvoice.builder().build();
      PaymentInvoice savedEntity = PaymentInvoice.builder().build();
      PaymentInvoiceDto expectedDto = new PaymentInvoiceDto();
      
      // Mock dependencies
      when(mapper.toEntity(dto)).thenReturn(entity);
      when(customerRepository.findById(customerId)).thenReturn(mock(org.jala.university.domain.entity.Customer.class));
      when(externalServiceRepository.findById(externalServiceId)).thenReturn(mock(org.jala.university.domain.entity.ExternalService.class));
      when(rep.saveAndFlush(any(PaymentInvoice.class))).thenReturn(savedEntity);
      when(mapper.toDto(savedEntity)).thenReturn(expectedDto);

      // Act
      PaymentInvoiceDto result = service.savePaymentInvoice(dto);
      
      // Assert
      assertEquals(expectedDto, result);
      verify(mapper).toEntity(dto);
      verify(customerRepository).findById(customerId);
      verify(externalServiceRepository).findById(externalServiceId);
      verify(rep).saveAndFlush(any(PaymentInvoice.class));
      verify(mapper).toDto(savedEntity);
    }

  @Test
  void returnsEmptyListWhenCustomerIdIsNull() {
    assertTrue(service.findByCustomerId(null).isEmpty());
    verifyNoInteractions(rep, mapper);
  }

  @Test
  void returnsEmptyListWhenServiceIdIsNull() {
    assertTrue(service.findByServiceId(null).isEmpty());
    verifyNoInteractions(rep, mapper);
  }

  @Test
  void savePaymentInvoiceDtoNull() {
    PaymentInvoiceDto paymentInvoiceDto = service.savePaymentInvoice(null);
    assertNull(paymentInvoiceDto);
  }

}

