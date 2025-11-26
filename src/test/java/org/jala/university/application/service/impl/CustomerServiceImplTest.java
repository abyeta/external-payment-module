package org.jala.university.application.service.impl;

import org.jala.university.application.dto.ExternalServiceDto;
import org.jala.university.application.mapper.ExternalServiceMapper;
import org.jala.university.domain.entity.ExternalService;
import org.jala.university.domain.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for CustomerServiceImpl.
 * Tests the business logic for customer-service link operations.
 */
@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ExternalServiceMapper mapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private ExternalService service1;
    private ExternalService service2;
    private ExternalServiceDto serviceDto1;
    private ExternalServiceDto serviceDto2;
    private Long customerId;
    private UUID serviceId;

    @BeforeEach
    void setUp() {
        customerId = 1L;
        serviceId = UUID.randomUUID();

        service1 = new ExternalService();
        service1.setId(UUID.randomUUID());
        service1.setProviderName("Service 1");

        service2 = new ExternalService();
        service2.setId(UUID.randomUUID());
        service2.setProviderName("Service 2");

        serviceDto1 = ExternalServiceDto.builder()
                .id(service1.getId())
                .providerName("Service 1")
                .build();

        serviceDto2 = ExternalServiceDto.builder()
                .id(service2.getId())
                .providerName("Service 2")
                .build();
    }

    @Test
    void testGetLinkedServices_ReturnsListOfDtos() {
        List<ExternalService> services = Arrays.asList(service1, service2);
        when(customerRepository.getLinkedServices(customerId)).thenReturn(services);
        when(mapper.mapTo(service1)).thenReturn(serviceDto1);
        when(mapper.mapTo(service2)).thenReturn(serviceDto2);

        List<ExternalServiceDto> result = customerService.getLinkedServices(customerId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Service 1", result.get(0).getProviderName());
        assertEquals("Service 2", result.get(1).getProviderName());
        verify(customerRepository).getLinkedServices(customerId);
        verify(mapper, times(2)).mapTo(any(ExternalService.class));
    }

    @Test
    void testGetLinkedServices_EmptyList() {
        when(customerRepository.getLinkedServices(customerId)).thenReturn(Arrays.asList());

        List<ExternalServiceDto> result = customerService.getLinkedServices(customerId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(customerRepository).getLinkedServices(customerId);
    }

    @Test
    void testLinkService_Success() {
        doNothing().when(customerRepository).linkService(customerId, serviceId);

        assertDoesNotThrow(() -> customerService.linkService(customerId, serviceId));

        verify(customerRepository).linkService(customerId, serviceId);
    }

    @Test
    void testLinkService_ThrowsException() {
        doThrow(new IllegalArgumentException("Service not found"))
                .when(customerRepository).linkService(customerId, serviceId);

        assertThrows(IllegalArgumentException.class,
                () -> customerService.linkService(customerId, serviceId));

        verify(customerRepository).linkService(customerId, serviceId);
    }

    @Test
    void testUnlinkService_Success() {
        doNothing().when(customerRepository).unlinkService(customerId, serviceId);

        assertDoesNotThrow(() -> customerService.unlinkService(customerId, serviceId));

        verify(customerRepository).unlinkService(customerId, serviceId);
    }

    @Test
    void testIsServiceLinked_ReturnsTrue() {
        when(customerRepository.isServiceLinked(customerId, serviceId)).thenReturn(true);

        boolean result = customerService.isServiceLinked(customerId, serviceId);

        assertTrue(result);
        verify(customerRepository).isServiceLinked(customerId, serviceId);
    }

    @Test
    void testIsServiceLinked_ReturnsFalse() {
        when(customerRepository.isServiceLinked(customerId, serviceId)).thenReturn(false);

        boolean result = customerService.isServiceLinked(customerId, serviceId);

        assertFalse(result);
        verify(customerRepository).isServiceLinked(customerId, serviceId);
    }
}

