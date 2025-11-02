package org.jala.university.infrastructure.persistance;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import org.jala.university.commons.infrastructure.persistance.CrudRepository;
import org.jala.university.domain.entity.Holder;
import org.jala.university.domain.repository.HolderRepository;

import java.util.UUID;

/**
 * JPA implementation of HolderRepository.
 * Extends CrudRepository to inherit basic CRUD operations and implements custom queries.
 */
public class HolderRepositoryImpl extends CrudRepository<Holder, UUID> implements HolderRepository {

    /**
     * Constructor that initializes the repository with an EntityManager.
     *
     * @param entityManager the JPA entity manager
     */
    public HolderRepositoryImpl(EntityManager entityManager) {
        super(Holder.class, entityManager);
    }

    /**
     * Checks if a holder with the specified email already exists in the database.
     * This method performs a case-sensitive search for the exact email address.
     *
     * @param email the email address to check for existence
     * @return {@code true} if a holder with the given email exists, {@code false} otherwise
     * @throws jakarta.persistence.PersistenceException if there is a problem with the database operation
     */
    @Transactional
    @Override
    public boolean existsByEmail(String email) {
        String jpql = "SELECT COUNT(e) FROM Holder e WHERE e.email = :email";

        TypedQuery<Long> query = getEntityManager()
                .createQuery(jpql, Long.class);

        query.setParameter("email", email);

        Long count = query.getSingleResult();
        return count > 0;
    }

    /**
     * Checks if a holder with the specified landline phone number already exists in the database.
     * This method searches for the exact landline phone number match.
     *
     * @param landlinePhone the landline phone number to check for existence
     * @return {@code true} if a holder with the given landline phone number exists, {@code false} otherwise
     * @throws jakarta.persistence.PersistenceException if there is a problem with the database operation
     */
    @Transactional
    @Override
    public boolean existsByLandlinePhone(String landlinePhone) {
        String jpql = "SELECT COUNT(e) FROM Holder e WHERE e.landlinePhone = :landlinePhone";

        TypedQuery<Long> query = getEntityManager()
                .createQuery(jpql, Long.class);

        query.setParameter("landlinePhone", landlinePhone);

        Long count = query.getSingleResult();
        return count > 0;
    }

    /**
     * Checks if a holder with the specified identification number already exists in the database.
     * This method is used to ensure uniqueness of identification numbers among holders.
     *
     * @param identificationNumber the identification number to check for existence
     * @return {@code true} if a holder with the given identification number exists, {@code false} otherwise
     * @throws jakarta.persistence.PersistenceException if there is a problem with the database operation
     */
    @Transactional
    @Override
    public boolean existsByIdentificationNumber(String identificationNumber) {
        String jpql = "SELECT COUNT(e) FROM Holder e WHERE e.identificationNumber = :identificationNumber";

        TypedQuery<Long> query = getEntityManager()
                .createQuery(jpql, Long.class);

        query.setParameter("identificationNumber", identificationNumber);

        Long count = query.getSingleResult();
        return count > 0;
    }

}
