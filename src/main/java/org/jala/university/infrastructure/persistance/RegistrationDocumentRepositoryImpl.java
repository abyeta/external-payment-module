package org.jala.university.infrastructure.persistance;

import jakarta.persistence.EntityManager;
import org.jala.university.commons.infrastructure.persistance.CrudRepository;
import org.jala.university.domain.entity.ExternalService;
import org.jala.university.domain.entity.RegistrationDocument;
import org.jala.university.domain.repository.ExternalServiceRepository;
import org.jala.university.domain.repository.RegistrationDocumentRepository;

import java.util.UUID;

public class RegistrationDocumentRepositoryImpl extends CrudRepository<RegistrationDocument, UUID> implements RegistrationDocumentRepository {

    protected RegistrationDocumentRepositoryImpl(Class<RegistrationDocument> clazzToSet, EntityManager entityManager) {
        super(clazzToSet, entityManager);
    }

}
