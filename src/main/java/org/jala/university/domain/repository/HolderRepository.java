package org.jala.university.domain.repository;

import org.jala.university.commons.domain.repository.Repository;
import org.jala.university.domain.entity.Holder;

import java.util.UUID;

public interface HolderRepository extends Repository<Holder, UUID> {

    boolean existsByEmail(String email);

    boolean existsByLandlinePhone(String landlinePhone);

    boolean existsByIdentificationNumber(String identificationNumber);
}
