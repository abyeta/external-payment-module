package org.jala.university.infrastructure.persistance;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import org.jala.university.commons.infrastructure.persistance.CrudRepository;
import org.jala.university.domain.entity.ExternalService;
import org.jala.university.domain.repository.ExternalServiceRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA implementation of ExternalServiceRepository.
 * Extends CrudRepository to inherit basic CRUD operations and implements custom queries.
 */
public class ExternalServiceRepositoryImpl
        extends CrudRepository<ExternalService, UUID>
        implements ExternalServiceRepository {

    /**
     * Constructor that initializes the repository with an EntityManager.
     *
     * @param entityManager the JPA entity manager
     */
    public ExternalServiceRepositoryImpl(EntityManager entityManager) {
        super(ExternalService.class, entityManager);
    }

    /**
     * Saves an external service entity and flushes to ensure the ID is generated.
     *
     * @param entity the external service entity to save
     * @return the saved entity with the generated ID
     */
    public ExternalService saveAndFlush(ExternalService entity) {
        EntityManager em = getEntityManager();
        jakarta.persistence.EntityTransaction tx = em.getTransaction();
        boolean startedHere = false;
        if (!tx.isActive()) {
            tx.begin();
            startedHere = true;
        }

        ExternalService managed;
        if (entity.getId() == null) {
            em.persist(entity);
            managed = entity;
        } else {
            managed = em.merge(entity);
        }

        em.flush();

        if (startedHere) {
            tx.commit();
        }

        return managed;
    }

    /**
     * Finds an external service by its account reference number.
     * Returns Optional to handle cases where the service is not found.
     *
     * @param accountReference the account reference to search for
     * @return Optional containing the external service if found, empty otherwise
     */
    @Override
    @Transactional
    public Optional<ExternalService> findByAccountReference(String accountReference) {
        String jpql = "SELECT e FROM ExternalService e WHERE e.accountReference = :accountReference";
        TypedQuery<ExternalService> query = getEntityManager()
                .createQuery(jpql, ExternalService.class);
        query.setParameter("accountReference", accountReference);

        try {
            ExternalService result = query.getSingleResult();
            return Optional.of(result);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Checks if an external service with the given account reference exists.
     * Uses COUNT query for efficient existence check.
     *
     * @param accountReference the account reference to check
     * @return true if exists, false otherwise
     */
    @Override
    @Transactional
    public boolean existsByAccountReference(String accountReference) {
        String jpql = "SELECT COUNT(e) FROM ExternalService e WHERE e.accountReference = :accountReference";
        TypedQuery<Long> query = getEntityManager()
                .createQuery(jpql, Long.class);
        query.setParameter("accountReference", accountReference);
        Long count = query.getSingleResult();
        return count > 0;
    }

    /**
     * Checks if an external service with the specified provider name already exists in the database.
     * This method is used to ensure uniqueness of provider names among external services.
     *
     * @param providerName the provider name to check for existence
     * @return {@code true} if an external service with the given provider name exists, {@code false} otherwise
     * @throws jakarta.persistence.PersistenceException if there is a problem with the database operation
     */
    @Transactional
    @Override
    public boolean existsByProviderName(String providerName) {
        String jpql = "SELECT COUNT(e) FROM ExternalService e WHERE e.providerName = :providerName";

        TypedQuery<Long> query = getEntityManager()
                .createQuery(jpql, Long.class);

        query.setParameter("providerName", providerName);

        Long count = query.getSingleResult();
        return count > 0;
    }

    /**
     * Determines if an external service with the specified email address is already registered.
     * This method provides efficient existence check to prevent duplicate email assignments
     * across different external service providers in the system.
     *
     * @param email the email address to check, must not be {@code null} or empty
     * @return {@code true} if the email is already associated with an external service,
     * {@code false} if the email is available
     * @throws jakarta.persistence.PersistenceException if there are issues with the underlying persistence operations
     * @throws IllegalArgumentException                 if the email parameter is {@code null} or empty
     */
    @Transactional
    @Override
    public boolean existsByEmail(String email) {
        String jpql = "SELECT COUNT(e) FROM ExternalService e WHERE e.email = :email";

        TypedQuery<Long> query = getEntityManager()
                .createQuery(jpql, Long.class);

        query.setParameter("email", email);

        Long count = query.getSingleResult();
        return count > 0;
    }

    /**
     * Checks for the existence of an external service with the given phone number.
     * This validation ensures phone number uniqueness among external services and is important
     * for maintaining accurate contact information and preventing duplicates.
     *
     * @param phoneNumber the phone number to verify (including country code), must not be {@code null} or empty
     * @return {@code true} if the phone number is already in use by another external service,
     * {@code false} if the phone number is available
     * @throws jakarta.persistence.PersistenceException if database access operations fail
     * @throws IllegalArgumentException                 if the phoneNumber parameter is {@code null} or empty
     */
    @Transactional
    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        String jpql = "SELECT COUNT(e) FROM ExternalService e WHERE e.phoneNumber = :phoneNumber";

        TypedQuery<Long> query = getEntityManager()
                .createQuery(jpql, Long.class);

        query.setParameter("phoneNumber", phoneNumber);

        Long count = query.getSingleResult();
        return count > 0;
    }

    /**
     * Searches for enabled external services matching the search term.
     * Performs a case-insensitive search across provider name, email, and phone number.
     *
     * @param searchTerm the term to search for
     * @return a list of external services matching the search criteria, ordered by provider name
     */
    @Override
    @Transactional
    public List<ExternalService> searchServices(String searchTerm) {
        String jpql = "SELECT e FROM ExternalService e WHERE "
                + "(LOWER(e.providerName) LIKE LOWER(:term) "
                + "OR LOWER(e.email) LIKE LOWER(:term) "
                + "OR e.phoneNumber LIKE :term) "
                + "AND e.enabled = true "
                + "ORDER BY e.providerName";

        TypedQuery<ExternalService> query = getEntityManager()
                .createQuery(jpql, ExternalService.class);
        query.setParameter("term", "%" + searchTerm + "%");

        return query.getResultList();
    }

}
