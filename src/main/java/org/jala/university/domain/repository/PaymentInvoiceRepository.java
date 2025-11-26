package org.jala.university.domain.repository;

import org.jala.university.commons.domain.repository.Repository;
import org.jala.university.domain.entity.PaymentInvoice;

import java.util.List;
import java.util.UUID;

public interface PaymentInvoiceRepository extends Repository<PaymentInvoice, UUID> {

    List<PaymentInvoice> findByCustomerId(Long customerId);
    List<PaymentInvoice> findByServiceId(UUID serviceId);
    List<PaymentInvoice> findByCustomerAndService(Long customerId, UUID serviceId);
    PaymentInvoice saveAndFlush(PaymentInvoice paymentInvoice);

}
