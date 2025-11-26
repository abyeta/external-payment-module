package org.jala.university.application.service.impl;

import jakarta.persistence.EntityManagerFactory;
import org.jala.university.application.dto.RegistrationDocumentDto;
import org.jala.university.application.mapper.RegistrationDocumentMapper;
import org.jala.university.domain.entity.ExternalService;
import org.jala.university.domain.entity.RegistrationDocument;
import org.jala.university.domain.repository.ExternalServiceRepository;
import org.jala.university.domain.repository.RegistrationDocumentRepository;
import org.jala.university.infrastructure.persistance.RegistrationDocumentRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
 * Test class for RegistrationDocumentServiceImpl.
 * Tests the business logic for managing registration documents.
 */
@ExtendWith(MockitoExtension.class)
class RegistrationDocumentServiceImplTest {

    @Mock
    private RegistrationDocumentRepository documentRepository;

    @Mock
    private ExternalServiceRepository serviceRepository;

    @Mock
    private RegistrationDocumentMapper mapper;

    @Mock
    private EntityManagerFactory entityManagerFactory;

    private RegistrationDocumentServiceImpl documentService;

    private UUID externalServiceId;
    private ExternalService externalService;
    private RegistrationDocument document;
    private RegistrationDocumentDto documentDto;

    @BeforeEach
    void setUp() {
        documentService = new RegistrationDocumentServiceImpl(
                documentRepository,
                serviceRepository,
                mapper,
                entityManagerFactory
        );

        externalServiceId = UUID.randomUUID();
        
        externalService = new ExternalService();
        externalService.setId(externalServiceId);
        externalService.setProviderName("Test Service");

        document = new RegistrationDocument();
        document.setId(UUID.randomUUID());
        document.setFileName("Test Document");
        document.setExternalService(externalService);

        documentDto = RegistrationDocumentDto.builder()
                .fileName("Test Document")
                .build();
    }

    @Test
    void testSaveRegistrationDocuments_Success() {
        List<RegistrationDocumentDto> dtoList = Arrays.asList(documentDto);
        
        when(serviceRepository.findById(externalServiceId)).thenReturn(externalService);
        when(mapper.mapFrom(documentDto)).thenReturn(document);
        when(documentRepository.save(document)).thenReturn(document);

        assertDoesNotThrow(() -> 
            documentService.saveRegistrationDocuments(externalServiceId, dtoList)
        );

        verify(serviceRepository).findById(externalServiceId);
        verify(mapper).mapFrom(documentDto);
        verify(documentRepository).save(document);
    }

    @Test
    void testSaveRegistrationDocuments_WithRepositoryImpl() {
        RegistrationDocumentRepositoryImpl repoImpl = mock(RegistrationDocumentRepositoryImpl.class);
        documentService = new RegistrationDocumentServiceImpl(
                repoImpl,
                serviceRepository,
                mapper,
                entityManagerFactory
        );

        List<RegistrationDocumentDto> dtoList = Arrays.asList(documentDto);
        
        when(serviceRepository.findById(externalServiceId)).thenReturn(externalService);
        when(mapper.mapFrom(documentDto)).thenReturn(document);
        when(repoImpl.saveWithoutTransaction(document)).thenReturn(document);

        assertDoesNotThrow(() -> 
            documentService.saveRegistrationDocuments(externalServiceId, dtoList)
        );

        verify(serviceRepository).findById(externalServiceId);
        verify(mapper).mapFrom(documentDto);
        verify(repoImpl).saveWithoutTransaction(document);
    }

    @Test
    void testSaveRegistrationDocuments_ServiceNotFound() {
        List<RegistrationDocumentDto> dtoList = Arrays.asList(documentDto);
        
        when(serviceRepository.findById(externalServiceId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> 
            documentService.saveRegistrationDocuments(externalServiceId, dtoList)
        );

        verify(serviceRepository).findById(externalServiceId);
        verify(mapper, never()).mapFrom(any());
        verify(documentRepository, never()).save(any());
    }

    @Test
    void testSaveRegistrationDocuments_MultipleDocuments() {
        RegistrationDocumentDto dto2 = RegistrationDocumentDto.builder()
                .fileName("Document 2")
                .build();

        RegistrationDocument doc2 = new RegistrationDocument();
        doc2.setId(UUID.randomUUID());
        doc2.setFileName("Document 2");

        List<RegistrationDocumentDto> dtoList = Arrays.asList(documentDto, dto2);
        
        when(serviceRepository.findById(externalServiceId)).thenReturn(externalService);
        when(mapper.mapFrom(documentDto)).thenReturn(document);
        when(mapper.mapFrom(dto2)).thenReturn(doc2);
        when(documentRepository.save(any(RegistrationDocument.class))).thenReturn(document);

        assertDoesNotThrow(() -> 
            documentService.saveRegistrationDocuments(externalServiceId, dtoList)
        );

        verify(serviceRepository).findById(externalServiceId);
        verify(mapper, times(2)).mapFrom(any(RegistrationDocumentDto.class));
        verify(documentRepository, times(2)).save(any(RegistrationDocument.class));
    }

    @Test
    void testSaveRegistrationDocuments_EmptyList() {
        List<RegistrationDocumentDto> emptyList = Collections.emptyList();
        
        when(serviceRepository.findById(externalServiceId)).thenReturn(externalService);

        assertDoesNotThrow(() -> 
            documentService.saveRegistrationDocuments(externalServiceId, emptyList)
        );

        verify(serviceRepository).findById(externalServiceId);
        verify(mapper, never()).mapFrom(any());
        verify(documentRepository, never()).save(any());
    }

    @Test
    void testFindAllRegistrationDocuments_ReturnsDocuments() {
        RegistrationDocument doc2 = new RegistrationDocument();
        doc2.setId(UUID.randomUUID());
        doc2.setFileName("Document 2");

        RegistrationDocumentDto dto2 = RegistrationDocumentDto.builder()
                .fileName("Document 2")
                .build();

        List<RegistrationDocument> documents = Arrays.asList(document, doc2);
        
        when(documentRepository.findAllById(externalServiceId)).thenReturn(documents);
        when(mapper.mapTo(document)).thenReturn(documentDto);
        when(mapper.mapTo(doc2)).thenReturn(dto2);

        List<RegistrationDocumentDto> result = 
            documentService.findAllRegistrationDocuments(externalServiceId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test Document", result.get(0).getFileName());
        assertEquals("Document 2", result.get(1).getFileName());
        
        verify(documentRepository).findAllById(externalServiceId);
        verify(mapper, times(2)).mapTo(any(RegistrationDocument.class));
    }

    @Test
    void testFindAllRegistrationDocuments_EmptyList() {
        when(documentRepository.findAllById(externalServiceId)).thenReturn(Collections.emptyList());

        List<RegistrationDocumentDto> result = 
            documentService.findAllRegistrationDocuments(externalServiceId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(documentRepository).findAllById(externalServiceId);
        verify(mapper, never()).mapTo(any());
    }
}

