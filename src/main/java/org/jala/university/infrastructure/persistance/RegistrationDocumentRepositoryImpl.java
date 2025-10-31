package org.jala.university.infrastructure.persistance;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.jala.university.commons.infrastructure.persistance.CrudRepository;
import org.jala.university.domain.entity.ExternalService;
import org.jala.university.domain.entity.RegistrationDocument;
import org.jala.university.domain.repository.ExternalServiceRepository;
import org.jala.university.domain.repository.RegistrationDocumentRepository;

import java.util.List;
import java.util.UUID;

public class RegistrationDocumentRepositoryImpl extends CrudRepository<RegistrationDocument, UUID> implements RegistrationDocumentRepository {

    public RegistrationDocumentRepositoryImpl(EntityManager entityManager) {
        super(RegistrationDocument.class, entityManager);
    }


    @Override
    public List<RegistrationDocument> findAllById(UUID externalServiceId) {
        TypedQuery<RegistrationDocument> query = getEntityManager().createQuery(
                "FROM RegistrationDocument rd WHERE rd.externalService.id =: id", RegistrationDocument.class
        );
        query.setParameter("id", externalServiceId);
        return query.getResultList();
    }
}
