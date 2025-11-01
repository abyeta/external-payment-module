package org.jala.university.infrastructure.persistance;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.jala.university.commons.infrastructure.persistance.CrudRepository;
import org.jala.university.domain.entity.RegistrationDocument;
import org.jala.university.domain.repository.RegistrationDocumentRepository;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of RegistrationDocumentRepository using JPA.
 */
public final class RegistrationDocumentRepositoryImpl
        extends CrudRepository<RegistrationDocument, UUID>
        implements RegistrationDocumentRepository {

    /**
     * Constructor for RegistrationDocumentRepositoryImpl.
     *
     * @param entityManager the entity manager
     */
    public RegistrationDocumentRepositoryImpl(EntityManager entityManager) {
        super(RegistrationDocument.class, entityManager);
    }
    /**
     * Save a registration document without forcibly starting a new transaction
     * if one is already active. Uses EntityManager persist/merge and flush.
     * This is an implementation-specific helper; it does NOT override the
     * potentially final save() from the parent CrudRepository.
     *
     * @param entity the registration document to save
     * @return the managed registration document
     */
    public RegistrationDocument saveWithoutTransaction(RegistrationDocument entity) {
        EntityManager em = getEntityManager();
        jakarta.persistence.EntityTransaction tx = em.getTransaction();
        boolean startedHere = false;
        if (!tx.isActive()) {
            tx.begin();
            startedHere = true;
        }

        RegistrationDocument managed;
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

    @Override
    public List<RegistrationDocument> findAllById(UUID externalServiceId) {
        TypedQuery<RegistrationDocument> query = getEntityManager().createQuery(
                "FROM RegistrationDocument rd WHERE rd.externalService.id =: id",
                RegistrationDocument.class
        );
        query.setParameter("id", externalServiceId);
        return query.getResultList();
    }
}
