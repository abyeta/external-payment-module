package org.jala.university.infrastructure.persistance;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.jala.university.domain.entity.ExternalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class for ExternalServiceRepositoryImpl.
 * Tests JPA implementation of external service repository operations.
 */
@ExtendWith(MockitoExtension.class)
class ExternalServiceRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private EntityTransaction transaction;

    @Mock
    private TypedQuery<ExternalService> serviceQuery;

    @Mock
    private TypedQuery<Long> longQuery;

    private ExternalServiceRepositoryImpl serviceRepository;

    private ExternalService service;
    private UUID serviceId;

    @BeforeEach
    void setUp() {
        serviceRepository = new ExternalServiceRepositoryImpl(entityManager);

        serviceId = UUID.randomUUID();
        service = new ExternalService();
        service.setId(serviceId);
        service.setProviderName("Test Service");
        service.setAccountReference("ACC-123");
        service.setEmail("test@example.com");
        service.setPhoneNumber("1234567890");
        service.setEnabled(true);
    }

    @Test
    void testSaveAndFlush_NewEntity() {
        when(entityManager.getTransaction()).thenReturn(transaction);
        when(transaction.isActive()).thenReturn(false);

        ExternalService newService = new ExternalService();
        newService.setProviderName("New Service");

        ExternalService result = serviceRepository.saveAndFlush(newService);

        assertNotNull(result);
        verify(transaction).begin();
        verify(entityManager).persist(newService);
        verify(entityManager).flush();
        verify(transaction).commit();
    }

    @Test
    void testSaveAndFlush_ExistingEntity() {
        when(entityManager.getTransaction()).thenReturn(transaction);
        when(transaction.isActive()).thenReturn(false);
        when(entityManager.merge(service)).thenReturn(service);

        ExternalService result = serviceRepository.saveAndFlush(service);

        assertNotNull(result);
        verify(transaction).begin();
        verify(entityManager).merge(service);
        verify(entityManager).flush();
        verify(transaction).commit();
    }

    @Test
    void testSaveAndFlush_WithActiveTransaction() {
        when(entityManager.getTransaction()).thenReturn(transaction);
        when(transaction.isActive()).thenReturn(true);
        when(entityManager.merge(service)).thenReturn(service);

        ExternalService result = serviceRepository.saveAndFlush(service);

        assertNotNull(result);
        verify(transaction, never()).begin();
        verify(entityManager).merge(service);
        verify(entityManager).flush();
        verify(transaction, never()).commit();
    }

    @Test
    void testFindByAccountReference_Found() {
        when(entityManager.createQuery(anyString(), eq(ExternalService.class))).thenReturn(serviceQuery);
        when(serviceQuery.setParameter("accountReference", "ACC-123")).thenReturn(serviceQuery);
        when(serviceQuery.getSingleResult()).thenReturn(service);

        Optional<ExternalService> result = serviceRepository.findByAccountReference("ACC-123");

        assertTrue(result.isPresent());
        assertEquals("ACC-123", result.get().getAccountReference());
        verify(entityManager).createQuery(anyString(), eq(ExternalService.class));
    }

    @Test
    void testFindByAccountReference_NotFound() {
        when(entityManager.createQuery(anyString(), eq(ExternalService.class))).thenReturn(serviceQuery);
        when(serviceQuery.setParameter("accountReference", "ACC-999")).thenReturn(serviceQuery);
        when(serviceQuery.getSingleResult()).thenThrow(new NoResultException());

        Optional<ExternalService> result = serviceRepository.findByAccountReference("ACC-999");

        assertFalse(result.isPresent());
        verify(entityManager).createQuery(anyString(), eq(ExternalService.class));
    }

    @Test
    void testExistsByAccountReference_ReturnsTrue() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longQuery);
        when(longQuery.setParameter("accountReference", "ACC-123")).thenReturn(longQuery);
        when(longQuery.getSingleResult()).thenReturn(1L);

        boolean result = serviceRepository.existsByAccountReference("ACC-123");

        assertTrue(result);
        verify(entityManager).createQuery(anyString(), eq(Long.class));
    }

    @Test
    void testExistsByAccountReference_ReturnsFalse() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longQuery);
        when(longQuery.setParameter("accountReference", "ACC-999")).thenReturn(longQuery);
        when(longQuery.getSingleResult()).thenReturn(0L);

        boolean result = serviceRepository.existsByAccountReference("ACC-999");

        assertFalse(result);
        verify(entityManager).createQuery(anyString(), eq(Long.class));
    }

    @Test
    void testExistsByProviderName_ReturnsTrue() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longQuery);
        when(longQuery.setParameter("providerName", "Test Service")).thenReturn(longQuery);
        when(longQuery.getSingleResult()).thenReturn(1L);

        boolean result = serviceRepository.existsByProviderName("Test Service");

        assertTrue(result);
        verify(entityManager).createQuery(anyString(), eq(Long.class));
    }

    @Test
    void testExistsByProviderName_ReturnsFalse() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longQuery);
        when(longQuery.setParameter("providerName", "Unknown")).thenReturn(longQuery);
        when(longQuery.getSingleResult()).thenReturn(0L);

        boolean result = serviceRepository.existsByProviderName("Unknown");

        assertFalse(result);
        verify(entityManager).createQuery(anyString(), eq(Long.class));
    }

    @Test
    void testExistsByEmail_ReturnsTrue() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longQuery);
        when(longQuery.setParameter("email", "test@example.com")).thenReturn(longQuery);
        when(longQuery.getSingleResult()).thenReturn(1L);

        boolean result = serviceRepository.existsByEmail("test@example.com");

        assertTrue(result);
        verify(entityManager).createQuery(anyString(), eq(Long.class));
    }

    @Test
    void testExistsByEmail_ReturnsFalse() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longQuery);
        when(longQuery.setParameter("email", "unknown@example.com")).thenReturn(longQuery);
        when(longQuery.getSingleResult()).thenReturn(0L);

        boolean result = serviceRepository.existsByEmail("unknown@example.com");

        assertFalse(result);
        verify(entityManager).createQuery(anyString(), eq(Long.class));
    }

    @Test
    void testExistsByPhoneNumber_ReturnsTrue() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longQuery);
        when(longQuery.setParameter("phoneNumber", "1234567890")).thenReturn(longQuery);
        when(longQuery.getSingleResult()).thenReturn(1L);

        boolean result = serviceRepository.existsByPhoneNumber("1234567890");

        assertTrue(result);
        verify(entityManager).createQuery(anyString(), eq(Long.class));
    }

    @Test
    void testExistsByPhoneNumber_ReturnsFalse() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longQuery);
        when(longQuery.setParameter("phoneNumber", "9999999999")).thenReturn(longQuery);
        when(longQuery.getSingleResult()).thenReturn(0L);

        boolean result = serviceRepository.existsByPhoneNumber("9999999999");

        assertFalse(result);
        verify(entityManager).createQuery(anyString(), eq(Long.class));
    }

    @Test
    void testSearchServices_ReturnsMatchingServices() {
        ExternalService service2 = new ExternalService();
        service2.setId(UUID.randomUUID());
        service2.setProviderName("Another Service");
        service2.setEnabled(true);

        List<ExternalService> services = Arrays.asList(service, service2);

        when(entityManager.createQuery(anyString(), eq(ExternalService.class))).thenReturn(serviceQuery);
        when(serviceQuery.setParameter(eq("term"), anyString())).thenReturn(serviceQuery);
        when(serviceQuery.getResultList()).thenReturn(services);

        List<ExternalService> result = serviceRepository.searchServices("Service");

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(entityManager).createQuery(anyString(), eq(ExternalService.class));
        verify(serviceQuery).setParameter(eq("term"), anyString());
    }

    @Test
    void testSearchServices_NoResults() {
        when(entityManager.createQuery(anyString(), eq(ExternalService.class))).thenReturn(serviceQuery);
        when(serviceQuery.setParameter(eq("term"), anyString())).thenReturn(serviceQuery);
        when(serviceQuery.getResultList()).thenReturn(Arrays.asList());

        List<ExternalService> result = serviceRepository.searchServices("NonExistent");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(entityManager).createQuery(anyString(), eq(ExternalService.class));
    }
}

