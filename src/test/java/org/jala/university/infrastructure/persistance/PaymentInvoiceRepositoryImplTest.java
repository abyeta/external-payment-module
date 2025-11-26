package org.jala.university.infrastructure.persistance;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.jala.university.domain.entity.PaymentInvoice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PaymentInvoiceRepositoryImplTest {

    private PaymentInvoiceRepositoryImpl repository;
    @Mock private EntityManager em;
    @Mock private TypedQuery<PaymentInvoice> query;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository =  new PaymentInvoiceRepositoryImpl(em);
    }

    @Test
    public void getInvoicesByCustomerTest() {
        Long customerId = 1L;

        List<PaymentInvoice> invoices = List.of(new PaymentInvoice());
        when(em.createQuery("SELECT pi FROM PaymentInvoice pi WHERE pi.customer.id =: id",  PaymentInvoice.class)).thenReturn(query);
        when(query.setParameter("id", customerId)).thenReturn(query);
        when(query.getResultList()).thenReturn(invoices);

        List<PaymentInvoice> result = repository.findByCustomerId(customerId);

        assertEquals(invoices, result);

    }


    @Test
    public void getInvoicesByServiceTest() {
        UUID serviceId = UUID.randomUUID();

        List<PaymentInvoice> invoices = List.of(new PaymentInvoice());
        when(em.createQuery("SELECT pi FROM PaymentInvoice pi WHERE pi.externalService.id =: id",  PaymentInvoice.class)).thenReturn(query);
        when(query.setParameter("id", serviceId)).thenReturn(query);
        when(query.getResultList()).thenReturn(invoices);

        List<PaymentInvoice> result = repository.findByServiceId(serviceId);

        assertEquals(invoices, result);
    }


    @Test
    public void nullCustomerIdTest() {
        List<PaymentInvoice> paymentInvoices =  repository.findByCustomerId(null);
        assertTrue(paymentInvoices.isEmpty());
    }

    @Test
    public void nullServiceIdTest() {
        List<PaymentInvoice> paymentInvoices =  repository.findByServiceId(null);
        assertTrue(paymentInvoices.isEmpty());
    }

}