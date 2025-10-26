package org.jala.university.infrastructure.persistance;

import jakarta.persistence.EntityManager;
import org.jala.university.commons.infrastructure.persistance.CrudRepository;
import org.jala.university.domain.entity.ExternalService;
import org.jala.university.domain.repository.ExternalServiceListRepository;
import java.util.UUID;

public class ExternalServiceListRepositoryImpl
    extends CrudRepository<ExternalService, UUID>
    implements ExternalServiceListRepository {

  public ExternalServiceListRepositoryImpl(EntityManager entityManager) {
    super(ExternalService.class, entityManager);
  }
}
