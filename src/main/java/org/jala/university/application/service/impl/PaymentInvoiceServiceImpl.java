package org.jala.university.application.service.impl;

import org.jala.university.application.service.PaymentInvoiceService;
import org.jala.university.domain.entity.Customer;
import org.jala.university.domain.entity.ExternalService;
import org.jala.university.domain.entity.PaymentInvoice;
import org.jala.university.domain.repository.CustomerRepository;
import org.jala.university.domain.repository.ExternalServiceRepository;
import org.jala.university.domain.repository.PaymentInvoiceRepository;
import org.jala.university.application.dto.PaymentInvoiceDto;
import org.jala.university.application.mapper.PaymentInvoiceMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public final class PaymentInvoiceServiceImpl implements PaymentInvoiceService {

  private final PaymentInvoiceRepository paymentInvoiceRepository;
  private final CustomerRepository customerRepository;
  private final ExternalServiceRepository externalServiceRepository;
  private final PaymentInvoiceMapper paymentInvoiceMapper;

  public PaymentInvoiceServiceImpl(PaymentInvoiceRepository paymentInvoiceRepository,
                                   CustomerRepository customerRepository,
                                   ExternalServiceRepository externalServiceRepository,
                                   PaymentInvoiceMapper paymentInvoiceMapper) {
    this.paymentInvoiceRepository = paymentInvoiceRepository;
      this.customerRepository = customerRepository;
      this.externalServiceRepository = externalServiceRepository;
      this.paymentInvoiceMapper = paymentInvoiceMapper;
  }

  @Override
  public List<PaymentInvoiceDto> findByCustomerId(Long customerId) {
    if (customerId == null) {
      return List.of();
    }

    List<PaymentInvoice> invoices = paymentInvoiceRepository.findByCustomerId(customerId);
    return paymentInvoiceMapper.toDtoList(invoices);
  }

    @Override
    public List<PaymentInvoiceDto> findByCustomerAndService(Long customerId, UUID serviceId) {
      if (customerId == null || serviceId == null) {
          return List.of();
      }

      List<PaymentInvoice> invoices = paymentInvoiceRepository.findByCustomerAndService(customerId, serviceId);
        return paymentInvoiceMapper.toDtoList(invoices);
    }

    @Override
  public List<PaymentInvoiceDto> findByServiceId(UUID serviceId) {
    if (serviceId == null) {
      return List.of();
    }

    List<PaymentInvoice> invoices = paymentInvoiceRepository.findByServiceId(serviceId);
    return paymentInvoiceMapper.toDtoList(invoices);
  }

    @Override
    public PaymentInvoiceDto savePaymentInvoice(PaymentInvoiceDto paymentInvoiceDto) {


      if (paymentInvoiceDto == null) {
          return null;
      }

      ExternalService externalService =
              externalServiceRepository.findById(paymentInvoiceDto.getExternalServiceId());
      Customer customer = customerRepository.findById(paymentInvoiceDto.getCustomerId());

      PaymentInvoice paymentInvoice = paymentInvoiceMapper.toEntity(paymentInvoiceDto);
      paymentInvoice.setExternalService(externalService);
      paymentInvoice.setCustomer(customer);
      paymentInvoice.setPaymentDate(LocalDateTime.now());
        PaymentInvoice saved = paymentInvoiceRepository.saveAndFlush(paymentInvoice);
        return paymentInvoiceMapper.toDto(saved);
    }

  @Override
  public PaymentInvoiceDto findById(UUID id) {
    if (id == null) {
      return null;
    }

    PaymentInvoice invoice = paymentInvoiceRepository.findById(id);
    if (invoice == null) {
      return null;
    }

    return paymentInvoiceMapper.toDto(invoice);
  }
}

