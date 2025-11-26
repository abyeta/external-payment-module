package org.jala.university.infrastructure.persistance;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.jala.university.domain.entity.Holder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Test class for HolderRepositoryImpl.
 * Tests JPA implementation of holder repository operations.
 */
@ExtendWith(MockitoExtension.class)
class HolderRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Long> longQuery;

    private HolderRepositoryImpl holderRepository;

    private Holder holder;

    @BeforeEach
    void setUp() {
        holderRepository = new HolderRepositoryImpl(entityManager);

        holder = new Holder();
        holder.setId(UUID.randomUUID());
        holder.setName("John Doe");
        holder.setEmail("john@example.com");
        holder.setIdentificationNumber("12345678");
        holder.setLandlinePhone("1234567");
    }

    @Test
    void testExistsByEmail_ReturnsTrue() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longQuery);
        when(longQuery.setParameter("email", "john@example.com")).thenReturn(longQuery);
        when(longQuery.getSingleResult()).thenReturn(1L);

        boolean result = holderRepository.existsByEmail("john@example.com");

        assertTrue(result);
        verify(entityManager).createQuery(anyString(), eq(Long.class));
        verify(longQuery).setParameter("email", "john@example.com");
    }

    @Test
    void testExistsByEmail_ReturnsFalse() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longQuery);
        when(longQuery.setParameter("email", "unknown@example.com")).thenReturn(longQuery);
        when(longQuery.getSingleResult()).thenReturn(0L);

        boolean result = holderRepository.existsByEmail("unknown@example.com");

        assertFalse(result);
        verify(entityManager).createQuery(anyString(), eq(Long.class));
        verify(longQuery).setParameter("email", "unknown@example.com");
    }

    @Test
    void testExistsByLandlinePhone_ReturnsTrue() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longQuery);
        when(longQuery.setParameter("landlinePhone", "1234567")).thenReturn(longQuery);
        when(longQuery.getSingleResult()).thenReturn(1L);

        boolean result = holderRepository.existsByLandlinePhone("1234567");

        assertTrue(result);
        verify(entityManager).createQuery(anyString(), eq(Long.class));
        verify(longQuery).setParameter("landlinePhone", "1234567");
    }

    @Test
    void testExistsByLandlinePhone_ReturnsFalse() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longQuery);
        when(longQuery.setParameter("landlinePhone", "9999999")).thenReturn(longQuery);
        when(longQuery.getSingleResult()).thenReturn(0L);

        boolean result = holderRepository.existsByLandlinePhone("9999999");

        assertFalse(result);
        verify(entityManager).createQuery(anyString(), eq(Long.class));
        verify(longQuery).setParameter("landlinePhone", "9999999");
    }

    @Test
    void testExistsByIdentificationNumber_ReturnsTrue() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longQuery);
        when(longQuery.setParameter("identificationNumber", "12345678")).thenReturn(longQuery);
        when(longQuery.getSingleResult()).thenReturn(1L);

        boolean result = holderRepository.existsByIdentificationNumber("12345678");

        assertTrue(result);
        verify(entityManager).createQuery(anyString(), eq(Long.class));
        verify(longQuery).setParameter("identificationNumber", "12345678");
    }

    @Test
    void testExistsByIdentificationNumber_ReturnsFalse() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longQuery);
        when(longQuery.setParameter("identificationNumber", "99999999")).thenReturn(longQuery);
        when(longQuery.getSingleResult()).thenReturn(0L);

        boolean result = holderRepository.existsByIdentificationNumber("99999999");

        assertFalse(result);
        verify(entityManager).createQuery(anyString(), eq(Long.class));
        verify(longQuery).setParameter("identificationNumber", "99999999");
    }

    @Test
    void testExistsByEmail_WithNullEmail() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longQuery);
        when(longQuery.setParameter("email", null)).thenReturn(longQuery);
        when(longQuery.getSingleResult()).thenReturn(0L);

        boolean result = holderRepository.existsByEmail(null);

        assertFalse(result);
        verify(entityManager).createQuery(anyString(), eq(Long.class));
    }

    @Test
    void testExistsByLandlinePhone_WithEmptyString() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longQuery);
        when(longQuery.setParameter("landlinePhone", "")).thenReturn(longQuery);
        when(longQuery.getSingleResult()).thenReturn(0L);

        boolean result = holderRepository.existsByLandlinePhone("");

        assertFalse(result);
        verify(entityManager).createQuery(anyString(), eq(Long.class));
    }

    @Test
    void testExistsByIdentificationNumber_WithEmptyString() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longQuery);
        when(longQuery.setParameter("identificationNumber", "")).thenReturn(longQuery);
        when(longQuery.getSingleResult()).thenReturn(0L);

        boolean result = holderRepository.existsByIdentificationNumber("");

        assertFalse(result);
        verify(entityManager).createQuery(anyString(), eq(Long.class));
    }
}

