package org.jala.university.application.service;

import org.jala.university.application.dto.PaymentInvoiceDto;

import java.util.List;
import java.util.UUID;

public interface PaymentInvoiceService {

  List<PaymentInvoiceDto> findByCustomerId(Long customerId);
  List<PaymentInvoiceDto> findByCustomerAndService(Long customerId, UUID serviceId);
  List<PaymentInvoiceDto> findByServiceId(UUID serviceId);
  PaymentInvoiceDto savePaymentInvoice(PaymentInvoiceDto paymentInvoiceDto);
  PaymentInvoiceDto findById(UUID id);
}
