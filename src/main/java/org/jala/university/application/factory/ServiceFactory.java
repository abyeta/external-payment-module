package org.jala.university.application.factory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.jala.university.application.mapper.ExternalServiceMapper;
import org.jala.university.application.mapper.RegistrationDocumentMapper;
import org.jala.university.application.service.*;
import org.jala.university.application.validator.ServiceDataValidator;
import org.jala.university.domain.repository.*;
import org.jala.university.infrastructure.persistance.*;

public final class ServiceFactory {
    private static final String PERSISTENCE_UNIT_NAME = "external-payment-pu";

    private static ServiceDataValidator validator;
    private static ExternalServiceRegistrationService registrationService;
    private static ExternalServiceUpdateService updateService;
    private static RegistrationDocumentService documentService;
    private static CustomerService customerServiceLinkService;

    private ServiceFactory() { }

    public static synchronized ServiceDataValidator getValidator() {
        if (validator == null) {
            validator = new ServiceDataValidator();
        }
        return validator;
    }

    public static synchronized ExternalServiceRegistrationService getRegistrationService() {
        if (registrationService != null) {
            return registrationService;
        }

        EntityManager em = getEntityManager();
        ExternalServiceRepository repo = new ExternalServiceRepositoryImpl(em);
        HolderRepository holderRepo = new HolderRepositoryImpl(em);
        ExternalServiceMapper mapper = new ExternalServiceMapper();

        registrationService = new ExternalServiceRegistrationServiceImpl(
                repo,
                mapper,
                getValidator(),
                holderRepo
        );
        return registrationService;
    }

    public static synchronized ExternalServiceUpdateService getUpdateService() {
        if (updateService != null) {
            return updateService;
        }

        EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        ExternalServiceRepository repo = new ExternalServiceRepositoryImpl(em);
        ExternalServiceMapper mapper = new ExternalServiceMapper();

        updateService = new ExternalServiceUpdateServiceImpl(
                repo,
                mapper,
                getValidator(),
                emf
        );
        return updateService;
    }

    public static synchronized RegistrationDocumentService getDocumentService() {
        if (documentService != null) {
            return documentService;
        }

        EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        RegistrationDocumentRepository docRepo = new RegistrationDocumentRepositoryImpl(em);
        ExternalServiceRepository repo = new ExternalServiceRepositoryImpl(em);
        RegistrationDocumentMapper docMapper = new RegistrationDocumentMapper();

        documentService = new RegistrationDocumentServiceImpl(docRepo, repo, docMapper, emf); // ← emf
        return documentService;
    }

    public static synchronized CustomerService getCustomerServiceLinkService() {
        if (customerServiceLinkService != null) {
            return customerServiceLinkService;
        }

        EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        CustomerRepository customerRepo = new CustomerRepositoryImpl(em);
        ExternalServiceMapper mapper = new ExternalServiceMapper();

        customerServiceLinkService = new CustomerServiceImpl(
                customerRepo,
                mapper
        );
        return customerServiceLinkService;
    }

    private static EntityManager getEntityManager() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        return emf.createEntityManager();
    }
}
