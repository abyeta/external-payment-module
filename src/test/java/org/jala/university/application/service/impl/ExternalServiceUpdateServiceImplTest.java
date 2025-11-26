package org.jala.university.application.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityTransaction;
import org.jala.university.application.dto.*;
import org.jala.university.application.mapper.ExternalServiceMapper;
import org.jala.university.application.validator.ServiceDataValidator;
import org.jala.university.domain.entity.ExternalService;
import org.jala.university.domain.entity.RegistrationDocument;
import org.jala.university.domain.repository.ExternalServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for ExternalServiceUpdateServiceImpl.
 * Tests the update service logic for external services.
 */
@ExtendWith(MockitoExtension.class)
class ExternalServiceUpdateServiceImplTest {

    @Mock
    private ExternalServiceRepository repository;

    @Mock
    private ExternalServiceMapper mapper;

    @Mock
    private ServiceDataValidator validator;

    @Mock
    private EntityManagerFactory entityManagerFactory;

    @Mock
    private EntityManager entityManager;

    @Mock
    private EntityTransaction transaction;

    @InjectMocks
    private ExternalServiceUpdateServiceImpl updateService;

    private ExternalServiceRegistrationRequestDto requestDto;
    private ExternalService service;
    private ExternalServiceDto serviceDto;
    private ValidationResultDto validValidation;
    private ValidationResultDto invalidValidation;
    private UUID serviceId;

    @BeforeEach
    void setUp() {
        serviceId = UUID.randomUUID();

        requestDto = ExternalServiceRegistrationRequestDto.builder()
                .providerName("Updated Service")
                .accountReference("ACC-456")
                .email("updated@example.com")
                .phoneNumber("1234567890")
                .build();

        service = new ExternalService();
        service.setId(serviceId);
        service.setProviderName("Original Service");
        service.setAccountReference("ACC-123");

        serviceDto = ExternalServiceDto.builder()
                .id(serviceId)
                .providerName("Updated Service")
                .build();

        validValidation = ValidationResultDto.builder()
                .valid(true)
                .errors(Collections.emptyList())
                .build();

        ValidationErrorDto error = ValidationErrorDto.builder()
                .field("providerName")
                .message("Invalid name")
                .build();
        invalidValidation = ValidationResultDto.builder()
                .valid(false)
                .errors(Arrays.asList(error))
                .build();
    }

    @Test
    void testUpdate_Success() {
        when(repository.findById(serviceId)).thenReturn(service);
        when(validator.validateAll(requestDto)).thenReturn(validValidation);
        when(repository.existsByAccountReference("ACC-456")).thenReturn(false);
        doNothing().when(mapper).updateFromRequest(requestDto, service);
        when(repository.save(service)).thenReturn(service);
        when(mapper.mapTo(service)).thenReturn(serviceDto);

        ExternalServiceDto result = updateService.update(serviceId, requestDto);

        assertNotNull(result);
        assertEquals("Updated Service", result.getProviderName());
        verify(repository).findById(serviceId);
        verify(validator).validateAll(requestDto);
        verify(repository).save(service);
    }

    @Test
    void testUpdate_ServiceNotFound() {
        when(repository.findById(serviceId)).thenReturn(null);

        assertThrows(EntityNotFoundException.class,
                () -> updateService.update(serviceId, requestDto));

        verify(repository).findById(serviceId);
        verify(validator, never()).validateAll(any());
        verify(repository, never()).save(any());
    }

    @Test
    void testUpdate_ValidationFails() {
        when(repository.findById(serviceId)).thenReturn(service);
        when(validator.validateAll(requestDto)).thenReturn(invalidValidation);

        assertThrows(IllegalArgumentException.class,
                () -> updateService.update(serviceId, requestDto));

        verify(repository).findById(serviceId);
        verify(validator).validateAll(requestDto);
        verify(repository, never()).save(any());
    }

    @Test
    void testUpdate_AccountReferenceAlreadyExists() {
        when(repository.findById(serviceId)).thenReturn(service);
        when(validator.validateAll(requestDto)).thenReturn(validValidation);
        when(repository.existsByAccountReference("ACC-456")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> updateService.update(serviceId, requestDto));

        verify(repository).findById(serviceId);
        verify(repository).existsByAccountReference("ACC-456");
        verify(repository, never()).save(any());
    }

    @Test
    void testUpdate_SameAccountReference() {
        requestDto = ExternalServiceRegistrationRequestDto.builder()
                .providerName("Updated Service")
                .accountReference("ACC-123") // Same as original
                .email("updated@example.com")
                .phoneNumber("1234567890")
                .build();
        when(repository.findById(serviceId)).thenReturn(service);
        when(validator.validateAll(requestDto)).thenReturn(validValidation);
        doNothing().when(mapper).updateFromRequest(requestDto, service);
        when(repository.save(service)).thenReturn(service);
        when(mapper.mapTo(service)).thenReturn(serviceDto);

        ExternalServiceDto result = updateService.update(serviceId, requestDto);

        assertNotNull(result);
        verify(repository).findById(serviceId);
        verify(repository, never()).existsByAccountReference(any());
        verify(repository).save(service);
    }

    @Test
    void testFindById_Success() {
        when(repository.findById(serviceId)).thenReturn(service);
        when(mapper.mapTo(service)).thenReturn(serviceDto);

        ExternalServiceDto result = updateService.findById(serviceId);

        assertNotNull(result);
        assertEquals(serviceId, result.getId());
        verify(repository).findById(serviceId);
        verify(mapper).mapTo(service);
    }

    @Test
    void testFindById_NotFound() {
        when(repository.findById(serviceId)).thenReturn(null);

        assertThrows(EntityNotFoundException.class,
                () -> updateService.findById(serviceId));

        verify(repository).findById(serviceId);
    }

    @Test
    void testFindAll_ReturnsServices() {
        ExternalService service2 = new ExternalService();
        service2.setId(UUID.randomUUID());
        
        List<ExternalService> services = Arrays.asList(service, service2);
        when(repository.findAll()).thenReturn(services);
        when(mapper.mapTo(any(ExternalService.class))).thenReturn(serviceDto);

        List<ExternalServiceDto> result = updateService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository).findAll();
    }

    @Test
    void testDeleteByIds_Success() {
        UUID docId1 = UUID.randomUUID();
        UUID docId2 = UUID.randomUUID();
        List<UUID> docIds = Arrays.asList(docId1, docId2);

        RegistrationDocument doc1 = new RegistrationDocument();
        RegistrationDocument doc2 = new RegistrationDocument();

        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(transaction);
        when(entityManager.find(RegistrationDocument.class, docId1)).thenReturn(doc1);
        when(entityManager.find(RegistrationDocument.class, docId2)).thenReturn(doc2);

        assertDoesNotThrow(() -> updateService.deleteByIds(docIds));

        verify(transaction).begin();
        verify(entityManager, times(2)).find(eq(RegistrationDocument.class), any(UUID.class));
        verify(entityManager, times(2)).remove(any(RegistrationDocument.class));
        verify(transaction).commit();
        verify(entityManager).close();
    }

    @Test
    void testDeleteByIds_EmptyList() {
        List<UUID> emptyList = Collections.emptyList();

        assertDoesNotThrow(() -> updateService.deleteByIds(emptyList));

        verify(entityManagerFactory, never()).createEntityManager();
    }

    @Test
    void testDeleteByIds_RollbackOnError() {
        UUID docId = UUID.randomUUID();
        List<UUID> docIds = Arrays.asList(docId);

        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(transaction);
        when(entityManager.find(RegistrationDocument.class, docId)).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> updateService.deleteByIds(docIds));

        verify(transaction).begin();
        verify(transaction).rollback();
        verify(entityManager).close();
    }
}

