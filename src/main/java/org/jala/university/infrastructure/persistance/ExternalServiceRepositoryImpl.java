package org.jala.university.infrastructure.persistance;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.jala.university.commons.infrastructure.persistance.CrudRepository;
import org.jala.university.domain.entity.ExternalService;
import org.jala.university.domain.repository.ExternalServiceRepository;

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
}

