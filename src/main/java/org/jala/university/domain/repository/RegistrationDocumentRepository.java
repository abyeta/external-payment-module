package org.jala.university.domain.repository;

import org.jala.university.commons.domain.repository.Repository;
import org.jala.university.domain.entity.RegistrationDocument;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for RegistrationDocument entities.
 */
public interface RegistrationDocumentRepository extends Repository<RegistrationDocument, UUID> {

    List<RegistrationDocument> findAllById(UUID externalServiceId);

}
