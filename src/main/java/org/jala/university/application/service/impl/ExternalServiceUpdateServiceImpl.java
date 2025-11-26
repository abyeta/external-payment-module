package org.jala.university.application.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.jala.university.application.dto.ExternalServiceRegistrationRequestDto;
import org.jala.university.application.dto.ExternalServiceDto;
import org.jala.university.application.dto.ValidationResultDto;
import org.jala.university.application.mapper.ExternalServiceMapper;
import org.jala.university.application.service.ExternalServiceUpdateService;
import org.jala.university.application.validator.ServiceDataValidator;
import org.jala.university.domain.entity.ExternalService;
import org.jala.university.domain.entity.RegistrationDocument;
import org.jala.university.domain.repository.ExternalServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación del servicio para actualizar servicios externos.
 * Esta clase no está diseñada para ser extendida.
 */
@Service
@RequiredArgsConstructor
public class ExternalServiceUpdateServiceImpl implements ExternalServiceUpdateService {

    private final ExternalServiceRepository repository;
    private final ExternalServiceMapper mapper;
    private final ServiceDataValidator validator;
    private final EntityManagerFactory entityManagerFactory;
    /**
     * Actualiza un servicio externo existente.
     * Este método no debe ser sobrescrito.
     *
     * @param id      Id del servicio a actualizar
     * @param request datos de actualización
     * @return DTO del servicio actualizado
     * @throws EntityNotFoundException si el servicio no existe
     * @throws IllegalArgumentException si la validación falla o hay conflicto de unicidad
     */
    @Override
    @Transactional
    public ExternalServiceDto update(UUID id, ExternalServiceRegistrationRequestDto request) {
        ExternalService entity = repository.findById(id);
        if (entity == null) {
            throw new EntityNotFoundException("External service not found with id: " + id);
        }

        ValidationResultDto validation = validator.validateAll(request);
        if (!validation.isValid()) {
            String errors = validation.getErrors().stream()
                    .map(error -> error.getMessage())
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException("Validation failed: " + errors);
        }

        String newRef = request.getAccountReference();
        if (!entity.getAccountReference().equals(newRef)) {
            if (repository.existsByAccountReference(newRef)) {
                throw new IllegalArgumentException("Account reference already in use");
            }
        }

        mapper.updateFromRequest(request, entity);
        ExternalService updated = repository.save(entity);
        return mapper.mapTo(updated);
    }

    /**
     * Busca un servicio por ID y lo convierte a DTO.
     * Este método no debe ser sobrescrito.
     *
     * @param id ID del servicio
     * @return DTO del servicio
     * @throws EntityNotFoundException si no existe
     */
    @Override
    public final ExternalServiceDto findById(UUID id) {
        ExternalService entity = repository.findById(id);
        if (entity == null) {
            throw new EntityNotFoundException("External service not found with id: " + id);
        }
        return mapper.mapTo(entity);
    }

    /**
     * Obtiene todos los servicios como DTOs.
     * Este método no debe ser sobrescrito.
     *
     * @return lista de servicios
     */
    @Override
    public final List<ExternalServiceDto> findAll() {
        return repository.findAll().stream()
                .map(mapper::mapTo)
                .toList();
    }

    @Override
    public final void deleteByIds(List<UUID> docIds) {
        if (docIds.isEmpty()) {
            return;
        }

        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            for (UUID docId : docIds) {
                RegistrationDocument doc = em.find(RegistrationDocument.class, docId);
                if (doc != null) {
                    em.remove(doc);
                }
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
