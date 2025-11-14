package org.jala.university.application.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.RequiredArgsConstructor;
import org.jala.university.application.dto.ExternalServiceDto;
import org.jala.university.application.dto.ExternalServiceRegistrationRequestDto;
import org.jala.university.application.dto.ValidationResultDto;
import org.jala.university.application.mapper.ExternalServiceMapper;
import org.jala.university.application.validator.ServiceDataValidator;
import org.jala.university.domain.entity.Account;
import org.jala.university.domain.entity.Bank;
import org.jala.university.domain.entity.ExternalService;
import org.jala.university.domain.entity.User;
import org.jala.university.domain.repository.AccountRepository;
import org.jala.university.domain.repository.ExternalServiceRepository;
import org.jala.university.domain.repository.HolderRepository;
import org.jala.university.infrastructure.persistance.AccountRepositoryImpl;


import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of ExternalServiceRegistrationService.
 * Handles business logic for external service registration operations.
 */

@RequiredArgsConstructor
public final class ExternalServiceRegistrationServiceImpl implements ExternalServiceRegistrationService {

    private final ExternalServiceRepository repository;
    private final ExternalServiceMapper mapper;
    private final ServiceDataValidator validator;
    private final HolderRepository holderRepository;

    @Override
    public ValidationResultDto validateServiceData(ExternalServiceRegistrationRequestDto request) {
        return validator.validateAll(request);
    }

    @Override
    public ExternalServiceDto submitRegistration(ExternalServiceRegistrationRequestDto request) {
        final double initialBalance = 0.0;

        validServiceFieldsOrThrow(request);
        validHolderFieldsOrThrow(request);
        ExternalService entity = mapper.mapFromRequest(request);

        Random random = new Random();
        Long number = Math.abs(random.nextLong());

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
        EntityManager em = emf.createEntityManager();
        AccountRepository accountRepository = new AccountRepositoryImpl(em);
        Account account = new Account();
        account.setAccountNumber(number);
        account.setBalance(initialBalance);
        account.setEmail(request.getEmail());
        account.setBank(getBank(em));
        accountRepository.save(account);

        entity.setAccountNumber(number);
        // Use saveAndFlush to ensure the entity ID is generated before mapping to DTO
        ExternalService saved = repository.saveAndFlush(entity);
        return mapper.mapTo(saved);
    }


    /**
     * Get the bank to create an account.
     * @param em EntityManager of transaction module.
     * @return bank to create the account.
     */
    public Bank getBank(EntityManager em) {

        final long id = 1L;
        final long exampleDni = 123456789L;

        Bank bank = em.find(Bank.class, id);

        if (bank == null) {
            em.getTransaction().begin();

            User bankUser =  em.find(User.class, id);
            if (bankUser == null) {
                bankUser = User.builder()
                        .id(1L)
                        .address("Example address")
                        .dniNumber(exampleDni)
                        .firstName("Jala")
                        .lastName("Bank")
                        .build();
                em.persist(em.merge(bankUser));
            }

            bank = Bank.builder()
                    .id(1L)
                    .user(bankUser)
                    .name("JalaBank")
                    .build();
            em.persist(em.merge(bank));
            em.getTransaction().commit();
        }
        return bank;
    }

    @Override
    public ExternalServiceDto findById(UUID id) {
        ExternalService entity = repository.findById(id);
        if (entity == null) {
            throw new IllegalStateException("External service not found with id: " + id);
        }
        return mapper.mapTo(entity);
    }

    @Override
    public List<ExternalServiceDto> findAll() {
        List<ExternalService> entities = repository.findAll();
        return entities.stream()
                .map(mapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
      repository.deleteById(id);
    }

    private void validHolderFieldsOrThrow(ExternalServiceRegistrationRequestDto request) {
          if (holderRepository.existsByEmail(request.getHolder().getEmail())) {
              throw new IllegalArgumentException("holder email already exists");
          }

          if (holderRepository.existsByIdentificationNumber(request.getHolder().getIdentificationNumber())) {
              throw new IllegalArgumentException("holder identification number already exists");
          }

          if (holderRepository.existsByLandlinePhone(request.getHolder().getLandlinePhone())) {
              throw new IllegalArgumentException("holder  landline phone already exists");
          }
      }

      private void validServiceFieldsOrThrow(ExternalServiceRegistrationRequestDto request) {

          ValidationResultDto validationResult = validator.validateAll(request);
          if (!validationResult.isValid()) {
              throw new IllegalArgumentException("Validation failed: " + validationResult.getErrors());
          }

          if (repository.existsByProviderName(request.getProviderName())) {
              throw new IllegalArgumentException("Service name already exists");
          }

          if (repository.existsByAccountReference(request.getAccountReference())) {
              throw new IllegalArgumentException("Service account reference already exists");
          }

          if (repository.existsByEmail(request.getEmail())) {
              throw new IllegalArgumentException("Service email already exists");
          }

          if (repository.existsByPhoneNumber(request.getPhoneNumber())) {
              throw new IllegalArgumentException("Service phone number already exists");
          }
      }

      @Override
      public ExternalServiceDto setEnabled(UUID id, boolean enabled) {
        ExternalService entity = repository.findById(id);
        if (entity == null) {
          throw new IllegalStateException("External service not found with id: " + id);
        }
          entity.setEnabled(enabled);
          entity.setUpdatedAt(java.time.LocalDateTime.now());
          ExternalService saved = repository.saveAndFlush(entity);
          return mapper.mapTo(saved);
      }

      @Override
    public List<ExternalServiceDto> searchServices(String searchTerm) {
        return repository.searchServices(searchTerm).stream()
                .map(mapper::mapTo)
                .collect(Collectors.toList());
    }
}
