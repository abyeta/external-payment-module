package org.jala.university.infrastructure.persistance;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.jala.university.domain.entity.Customer;
import org.jala.university.domain.entity.ExternalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class for CustomerRepositoryImpl.
 * Tests JPA implementation of customer repository operations.
 */
@ExtendWith(MockitoExtension.class)
class CustomerRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private EntityTransaction transaction;

    @Mock
    private TypedQuery<Customer> customerQuery;

    @Mock
    private TypedQuery<Long> longQuery;

    private CustomerRepositoryImpl customerRepository;

    private Customer customer;
    private ExternalService service;
    private Long customerId;
    private UUID serviceId;

    @BeforeEach
    void setUp() {
        customerRepository = new CustomerRepositoryImpl(entityManager);

        customerId = 1L;
        serviceId = UUID.randomUUID();

        service = new ExternalService();
        service.setId(serviceId);
        service.setProviderName("Test Service");
        service.setEnabled(true);

        customer = new Customer();
        customer.setId(customerId);
    }

    @Test
    void testGetLinkedServices_ReturnsServices() {
        ExternalService service2 = new ExternalService();
        service2.setId(UUID.randomUUID());
        service2.setProviderName("Service 2");

        customer.linkService(service);
        customer.linkService(service2);

        when(entityManager.createQuery(anyString(), eq(Customer.class))).thenReturn(customerQuery);
        when(customerQuery.setParameter("customerId", customerId)).thenReturn(customerQuery);
        when(customerQuery.getSingleResult()).thenReturn(customer);

        List<ExternalService> result = customerRepository.getLinkedServices(customerId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(entityManager).createQuery(anyString(), eq(Customer.class));
    }

    @Test
    void testGetLinkedServices_EmptyList() {
        when(entityManager.createQuery(anyString(), eq(Customer.class))).thenReturn(customerQuery);
        when(customerQuery.setParameter("customerId", customerId)).thenReturn(customerQuery);
        when(customerQuery.getSingleResult()).thenReturn(customer);

        List<ExternalService> result = customerRepository.getLinkedServices(customerId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(entityManager).createQuery(anyString(), eq(Customer.class));
    }

    @Test
    void testGetLinkedServices_CustomerNotFound() {
        when(entityManager.createQuery(anyString(), eq(Customer.class))).thenReturn(customerQuery);
        when(customerQuery.setParameter("customerId", customerId)).thenReturn(customerQuery);
        when(customerQuery.getSingleResult()).thenThrow(new RuntimeException("Customer not found"));

        assertThrows(IllegalArgumentException.class,
                () -> customerRepository.getLinkedServices(customerId));

        verify(entityManager).createQuery(anyString(), eq(Customer.class));
    }

    @Test
    void testLinkService_Success() {
        when(entityManager.getTransaction()).thenReturn(transaction);
        when(entityManager.find(Customer.class, customerId)).thenReturn(customer);
        when(entityManager.find(ExternalService.class, serviceId)).thenReturn(service);
        when(entityManager.merge(customer)).thenReturn(customer);

        assertDoesNotThrow(() -> customerRepository.linkService(customerId, serviceId));

        verify(transaction).begin();
        verify(transaction).commit();
        verify(entityManager).find(Customer.class, customerId);
        verify(entityManager).find(ExternalService.class, serviceId);
        verify(entityManager).merge(customer);
    }

    @Test
    void testLinkService_CustomerNotFound() {
        when(entityManager.getTransaction()).thenReturn(transaction);
        when(entityManager.find(Customer.class, customerId)).thenReturn(null);
        when(transaction.isActive()).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> customerRepository.linkService(customerId, serviceId));

        verify(transaction).begin();
        verify(transaction).rollback();
        verify(entityManager, never()).merge(any());
    }

    @Test
    void testLinkService_ServiceNotFound() {
        when(entityManager.getTransaction()).thenReturn(transaction);
        when(entityManager.find(Customer.class, customerId)).thenReturn(customer);
        when(entityManager.find(ExternalService.class, serviceId)).thenReturn(null);
        when(transaction.isActive()).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> customerRepository.linkService(customerId, serviceId));

        verify(transaction).begin();
        verify(transaction).rollback();
        verify(entityManager, never()).merge(any());
    }

    @Test
    void testLinkService_ServiceNotEnabled() {
        service.setEnabled(false);
        
        when(entityManager.getTransaction()).thenReturn(transaction);
        when(entityManager.find(Customer.class, customerId)).thenReturn(customer);
        when(entityManager.find(ExternalService.class, serviceId)).thenReturn(service);
        when(transaction.isActive()).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> customerRepository.linkService(customerId, serviceId));

        verify(transaction).begin();
        verify(transaction).rollback();
        verify(entityManager, never()).merge(any());
    }

    @Test
    void testLinkService_AlreadyLinked() {
        customer.linkService(service);

        when(entityManager.getTransaction()).thenReturn(transaction);
        when(entityManager.find(Customer.class, customerId)).thenReturn(customer);
        when(entityManager.find(ExternalService.class, serviceId)).thenReturn(service);

        assertDoesNotThrow(() -> customerRepository.linkService(customerId, serviceId));

        verify(transaction).begin();
        verify(transaction).commit();
        verify(entityManager, never()).merge(any());
    }

    @Test
    void testUnlinkService_Success() {
        customer.linkService(service);

        when(entityManager.getTransaction()).thenReturn(transaction);
        when(entityManager.createQuery(anyString(), eq(Customer.class))).thenReturn(customerQuery);
        when(customerQuery.setParameter("customerId", customerId)).thenReturn(customerQuery);
        when(customerQuery.getSingleResult()).thenReturn(customer);
        when(entityManager.find(ExternalService.class, serviceId)).thenReturn(service);
        when(entityManager.merge(customer)).thenReturn(customer);

        assertDoesNotThrow(() -> customerRepository.unlinkService(customerId, serviceId));

        verify(transaction).begin();
        verify(transaction).commit();
        verify(entityManager).merge(customer);
    }

    @Test
    void testUnlinkService_RollbackOnError() {
        when(entityManager.getTransaction()).thenReturn(transaction);
        when(entityManager.createQuery(anyString(), eq(Customer.class))).thenReturn(customerQuery);
        when(customerQuery.setParameter("customerId", customerId)).thenReturn(customerQuery);
        when(customerQuery.getSingleResult()).thenThrow(new RuntimeException("Error"));
        when(transaction.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class,
                () -> customerRepository.unlinkService(customerId, serviceId));

        verify(transaction).begin();
        verify(transaction).rollback();
    }

    @Test
    void testIsServiceLinked_ReturnsTrue() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longQuery);
        when(longQuery.setParameter("customerId", customerId)).thenReturn(longQuery);
        when(longQuery.setParameter("serviceId", serviceId)).thenReturn(longQuery);
        when(longQuery.getSingleResult()).thenReturn(1L);

        boolean result = customerRepository.isServiceLinked(customerId, serviceId);

        assertTrue(result);
        verify(entityManager).createQuery(anyString(), eq(Long.class));
    }

    @Test
    void testIsServiceLinked_ReturnsFalse() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longQuery);
        when(longQuery.setParameter("customerId", customerId)).thenReturn(longQuery);
        when(longQuery.setParameter("serviceId", serviceId)).thenReturn(longQuery);
        when(longQuery.getSingleResult()).thenReturn(0L);

        boolean result = customerRepository.isServiceLinked(customerId, serviceId);

        assertFalse(result);
        verify(entityManager).createQuery(anyString(), eq(Long.class));
    }
}

