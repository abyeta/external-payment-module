package org.jala.university.application.service.impl;

import org.jala.university.application.dto.*;
import org.jala.university.application.mapper.ExternalServiceMapper;
import org.jala.university.application.validator.ServiceDataValidator;
import org.jala.university.domain.entity.ExternalService;
import org.jala.university.domain.repository.ExternalServiceRepository;
import org.jala.university.domain.repository.HolderRepository;
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
 * Test class for ExternalServiceRegistrationServiceImpl.
 * Tests the business logic for external service registration.
 */
@ExtendWith(MockitoExtension.class)
class ExternalServiceRegistrationServiceImplTest {

    @Mock
    private ExternalServiceRepository repository;

    @Mock
    private ExternalServiceMapper mapper;

    @Mock
    private ServiceDataValidator validator;

    @Mock
    private HolderRepository holderRepository;

    @InjectMocks
    private ExternalServiceRegistrationServiceImpl registrationService;

    private ExternalServiceRegistrationRequestDto requestDto;
    private HolderDto holderDto;
    private ValidationResultDto validValidation;
    private ValidationResultDto invalidValidation;
    private ExternalService service;
    private ExternalServiceDto serviceDto;

    @BeforeEach
    void setUp() {
        holderDto = HolderDto.builder()
                .email("holder@example.com")
                .identificationNumber("123456789")
                .landlinePhone("1234567")
                .build();

        requestDto = ExternalServiceRegistrationRequestDto.builder()
                .providerName("Test Service")
                .accountReference("ACC-123")
                .email("service@example.com")
                .phoneNumber("9876543210")
                .holder(holderDto)
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

        service = new ExternalService();
        service.setId(UUID.randomUUID());
        service.setProviderName("Test Service");

        serviceDto = ExternalServiceDto.builder()
                .id(service.getId())
                .providerName("Test Service")
                .build();
    }

    @Test
    void testValidateServiceData_ReturnsValidResult() {
        when(validator.validateAll(requestDto)).thenReturn(validValidation);

        ValidationResultDto result = registrationService.validateServiceData(requestDto);

        assertNotNull(result);
        assertTrue(result.isValid());
        verify(validator).validateAll(requestDto);
    }

    @Test
    void testValidateServiceData_ReturnsInvalidResult() {
        when(validator.validateAll(requestDto)).thenReturn(invalidValidation);

        ValidationResultDto result = registrationService.validateServiceData(requestDto);

        assertNotNull(result);
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        verify(validator).validateAll(requestDto);
    }

    @Test
    void testFindById_Success() {
        UUID serviceId = UUID.randomUUID();
        when(repository.findById(serviceId)).thenReturn(service);
        when(mapper.mapTo(service)).thenReturn(serviceDto);

        ExternalServiceDto result = registrationService.findById(serviceId);

        assertNotNull(result);
        assertEquals(serviceDto.getProviderName(), result.getProviderName());
        verify(repository).findById(serviceId);
        verify(mapper).mapTo(service);
    }

    @Test
    void testFindById_NotFound() {
        UUID serviceId = UUID.randomUUID();
        when(repository.findById(serviceId)).thenReturn(null);

        assertThrows(IllegalStateException.class,
                () -> registrationService.findById(serviceId));

        verify(repository).findById(serviceId);
        verify(mapper, never()).mapTo(any());
    }

    @Test
    void testFindAll_ReturnsListOfServices() {
        ExternalService service2 = new ExternalService();
        service2.setId(UUID.randomUUID());
        service2.setProviderName("Service 2");

        ExternalServiceDto serviceDto2 = ExternalServiceDto.builder()
                .id(service2.getId())
                .providerName("Service 2")
                .build();

        List<ExternalService> services = Arrays.asList(service, service2);
        when(repository.findAll()).thenReturn(services);
        when(mapper.mapTo(service)).thenReturn(serviceDto);
        when(mapper.mapTo(service2)).thenReturn(serviceDto2);

        List<ExternalServiceDto> result = registrationService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository).findAll();
        verify(mapper, times(2)).mapTo(any(ExternalService.class));
    }

    @Test
    void testFindAll_EmptyList() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<ExternalServiceDto> result = registrationService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findAll();
    }

    @Test
    void testDelete_Success() {
        UUID serviceId = UUID.randomUUID();
        doNothing().when(repository).deleteById(serviceId);

        assertDoesNotThrow(() -> registrationService.delete(serviceId));

        verify(repository).deleteById(serviceId);
    }

    @Test
    void testSetEnabled_Success() {
        UUID serviceId = UUID.randomUUID();
        service.setEnabled(false);
        
        when(repository.findById(serviceId)).thenReturn(service);
        when(repository.saveAndFlush(service)).thenReturn(service);
        when(mapper.mapTo(service)).thenReturn(serviceDto);

        ExternalServiceDto result = registrationService.setEnabled(serviceId, true);

        assertNotNull(result);
        assertTrue(service.isEnabled());
        verify(repository).findById(serviceId);
        verify(repository).saveAndFlush(service);
    }

    @Test
    void testSetEnabled_ServiceNotFound() {
        UUID serviceId = UUID.randomUUID();
        when(repository.findById(serviceId)).thenReturn(null);

        assertThrows(IllegalStateException.class,
                () -> registrationService.setEnabled(serviceId, true));

        verify(repository).findById(serviceId);
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void testSearchServices_ReturnsMatchingServices() {
        String searchTerm = "test";
        List<ExternalService> services = Arrays.asList(service);
        when(repository.searchServices(searchTerm)).thenReturn(services);
        when(mapper.mapTo(service)).thenReturn(serviceDto);

        List<ExternalServiceDto> result = registrationService.searchServices(searchTerm);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository).searchServices(searchTerm);
    }

    @Test
    void testSearchServices_NoResults() {
        String searchTerm = "nonexistent";
        when(repository.searchServices(searchTerm)).thenReturn(Collections.emptyList());

        List<ExternalServiceDto> result = registrationService.searchServices(searchTerm);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).searchServices(searchTerm);
    }
}

