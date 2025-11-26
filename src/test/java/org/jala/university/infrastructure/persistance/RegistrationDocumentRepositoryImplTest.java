package org.jala.university.infrastructure.persistance;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.jala.university.domain.entity.ExternalService;
import org.jala.university.domain.entity.RegistrationDocument;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class for RegistrationDocumentRepositoryImpl.
 * Tests JPA implementation of registration document repository operations.
 */
@ExtendWith(MockitoExtension.class)
class RegistrationDocumentRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private EntityTransaction transaction;

    @Mock
    private TypedQuery<RegistrationDocument> documentQuery;

    private RegistrationDocumentRepositoryImpl documentRepository;

    private RegistrationDocument document;
    private ExternalService externalService;
    private UUID externalServiceId;
    private UUID documentId;

    @BeforeEach
    void setUp() {
        documentRepository = new RegistrationDocumentRepositoryImpl(entityManager);

        externalServiceId = UUID.randomUUID();
        documentId = UUID.randomUUID();

        externalService = new ExternalService();
        externalService.setId(externalServiceId);
        externalService.setProviderName("Test Service");

        document = new RegistrationDocument();
        document.setId(documentId);
        document.setFileName("Test Document");
        document.setExternalService(externalService);
    }

    @Test
    void testSaveWithoutTransaction_NewEntity() {
        RegistrationDocument newDoc = new RegistrationDocument();
        newDoc.setFileName("New Document");

        when(entityManager.getTransaction()).thenReturn(transaction);
        when(transaction.isActive()).thenReturn(false);

        RegistrationDocument result = documentRepository.saveWithoutTransaction(newDoc);

        assertNotNull(result);
        verify(transaction).begin();
        verify(entityManager).persist(newDoc);
        verify(entityManager).flush();
        verify(transaction).commit();
    }

    @Test
    void testSaveWithoutTransaction_ExistingEntity() {
        when(entityManager.getTransaction()).thenReturn(transaction);
        when(transaction.isActive()).thenReturn(false);
        when(entityManager.merge(document)).thenReturn(document);

        RegistrationDocument result = documentRepository.saveWithoutTransaction(document);

        assertNotNull(result);
        assertEquals(documentId, result.getId());
        verify(transaction).begin();
        verify(entityManager).merge(document);
        verify(entityManager).flush();
        verify(transaction).commit();
    }

    @Test
    void testSaveWithoutTransaction_WithActiveTransaction() {
        when(entityManager.getTransaction()).thenReturn(transaction);
        when(transaction.isActive()).thenReturn(true);
        when(entityManager.merge(document)).thenReturn(document);

        RegistrationDocument result = documentRepository.saveWithoutTransaction(document);

        assertNotNull(result);
        verify(transaction, never()).begin();
        verify(entityManager).merge(document);
        verify(entityManager).flush();
        verify(transaction, never()).commit();
    }

    @Test
    void testFindAllById_ReturnsDocuments() {
        RegistrationDocument doc2 = new RegistrationDocument();
        doc2.setId(UUID.randomUUID());
        doc2.setFileName("Document 2");
        doc2.setExternalService(externalService);

        List<RegistrationDocument> documents = Arrays.asList(document, doc2);

        when(entityManager.createQuery(anyString(), eq(RegistrationDocument.class))).thenReturn(documentQuery);
        when(documentQuery.setParameter("id", externalServiceId)).thenReturn(documentQuery);
        when(documentQuery.getResultList()).thenReturn(documents);

        List<RegistrationDocument> result = documentRepository.findAllById(externalServiceId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test Document", result.get(0).getFileName());
        assertEquals("Document 2", result.get(1).getFileName());
        
        verify(entityManager).createQuery(anyString(), eq(RegistrationDocument.class));
        verify(documentQuery).setParameter("id", externalServiceId);
    }

    @Test
    void testFindAllById_EmptyList() {
        when(entityManager.createQuery(anyString(), eq(RegistrationDocument.class))).thenReturn(documentQuery);
        when(documentQuery.setParameter("id", externalServiceId)).thenReturn(documentQuery);
        when(documentQuery.getResultList()).thenReturn(Collections.emptyList());

        List<RegistrationDocument> result = documentRepository.findAllById(externalServiceId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(entityManager).createQuery(anyString(), eq(RegistrationDocument.class));
        verify(documentQuery).setParameter("id", externalServiceId);
    }

    @Test
    void testFindAllById_WithNonExistentServiceId() {
        UUID nonExistentId = UUID.randomUUID();
        
        when(entityManager.createQuery(anyString(), eq(RegistrationDocument.class))).thenReturn(documentQuery);
        when(documentQuery.setParameter("id", nonExistentId)).thenReturn(documentQuery);
        when(documentQuery.getResultList()).thenReturn(Collections.emptyList());

        List<RegistrationDocument> result = documentRepository.findAllById(nonExistentId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(entityManager).createQuery(anyString(), eq(RegistrationDocument.class));
        verify(documentQuery).setParameter("id", nonExistentId);
    }

    @Test
    void testSaveWithoutTransaction_PersistNewEntity() {
        RegistrationDocument newDoc = new RegistrationDocument();
        newDoc.setFileName("Brand New");
        // ID is null, so it should persist

        when(entityManager.getTransaction()).thenReturn(transaction);
        when(transaction.isActive()).thenReturn(false);

        RegistrationDocument result = documentRepository.saveWithoutTransaction(newDoc);

        assertSame(newDoc, result);
        verify(entityManager).persist(newDoc);
        verify(entityManager, never()).merge(any());
        verify(entityManager).flush();
    }

    @Test
    void testSaveWithoutTransaction_MergeExistingEntity() {
        when(entityManager.getTransaction()).thenReturn(transaction);
        when(transaction.isActive()).thenReturn(false);
        when(entityManager.merge(document)).thenReturn(document);

        RegistrationDocument result = documentRepository.saveWithoutTransaction(document);

        assertEquals(document, result);
        verify(entityManager, never()).persist(any());
        verify(entityManager).merge(document);
        verify(entityManager).flush();
    }
}

