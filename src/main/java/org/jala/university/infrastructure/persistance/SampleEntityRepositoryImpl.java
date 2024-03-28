package org.jala.university.infrastructure.persistance;

import jakarta.persistence.EntityManager;
import org.jala.university.commons.infrastructure.persistance.CrudRepository;
import org.jala.university.domain.entity.SampleEntity;
import org.jala.university.domain.repository.SampleEntityRepository;

import java.util.UUID;

public class SampleEntityRepositoryImpl extends CrudRepository<SampleEntity, UUID> implements SampleEntityRepository {
    protected SampleEntityRepositoryImpl(EntityManager entityManager) {
        super(SampleEntity.class, entityManager);
    }
}
