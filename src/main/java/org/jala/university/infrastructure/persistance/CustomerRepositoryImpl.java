package org.jala.university.infrastructure.persistance;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.jala.university.commons.infrastructure.persistance.CrudRepository;
import org.jala.university.domain.entity.Customer;
import org.jala.university.domain.entity.ExternalService;
import org.jala.university.domain.repository.CustomerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA implementation of CustomerRepository.
 * Extends CrudRepository to inherit basic CRUD operations.
 */
public class CustomerRepositoryImpl
        extends CrudRepository<Customer, UUID>
        implements CustomerRepository {

    /**
     * Constructor that initializes the repository with an EntityManager.
     *
     * @param entityManager the JPA entity manager
     */
    public CustomerRepositoryImpl(EntityManager entityManager) {
        super(Customer.class, entityManager);
    }

    /**
     * Retrieves all external services linked to a specific customer.
     *
     * @param customerId the unique identifier of the customer
     * @return a list of external services linked to the customer
     * @throws IllegalArgumentException if the customer is not found
     */
    @Override
    public List<ExternalService> getLinkedServices(UUID customerId) {
        try {
            // Fetch customer with linked services
            String jpql = "SELECT c FROM Customer c "
                    + "LEFT JOIN FETCH c.linkedServices "
                    + "WHERE c.id = :customerId";

            TypedQuery<Customer> query = getEntityManager()
                    .createQuery(jpql, Customer.class);
            query.setParameter("customerId", customerId);

            Customer customer = query.getSingleResult();

            return new ArrayList<>(customer.getLinkedServices());
        } catch (Exception e) {
            throw new IllegalArgumentException("Customer not found: " + customerId, e);
        }
    }

    /**
     * Links an external service to a customer.
     *
     * @param customerId the unique identifier of the customer
     * @param serviceId the unique identifier of the service to link
     * @throws IllegalArgumentException if customer or service is not found
     * @throws IllegalStateException if the service is not enabled
     */
    @Override
    public void linkService(UUID customerId, UUID serviceId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            Customer customer = em.find(Customer.class, customerId);
            ExternalService service = em.find(ExternalService.class, serviceId);

            if (customer == null) {
                throw new IllegalArgumentException("Customer not found: " + customerId);
            }
            if (service == null) {
                throw new IllegalArgumentException("Service not found: " + serviceId);
            }
            if (!service.isEnabled()) {
                throw new IllegalStateException("Service is not enabled: " + service.getProviderName());
            }

            // Check if already linked
            if (!customer.hasLinkedServiceById(serviceId)) {
                customer.linkService(service);
                em.merge(customer);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

    /**
     * Removes the link between a customer and an external service.
     *
     * @param customerId the unique identifier of the customer
     * @param serviceId the unique identifier of the service to unlink
     */
    @Override
    public void unlinkService(UUID customerId, UUID serviceId) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            // Fetch customer with linked services
            String jpql = "SELECT c FROM Customer c "
                    + "LEFT JOIN FETCH c.linkedServices "
                    + "WHERE c.id = :customerId";

            TypedQuery<Customer> query = em.createQuery(jpql, Customer.class);
            query.setParameter("customerId", customerId);
            Customer customer = query.getSingleResult();

            ExternalService service = em.find(ExternalService.class, serviceId);

            if (customer != null && service != null) {
                customer.unlinkService(service);
                em.merge(customer);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

    /**
     * Checks if a specific service is linked to a customer.
     *
     * @param customerId the unique identifier of the customer
     * @param serviceId the unique identifier of the service
     * @return true if the service is linked to the customer, false otherwise
     */
    @Override
    public boolean isServiceLinked(UUID customerId, UUID serviceId) {
        String jpql = "SELECT COUNT(s) FROM Customer c "
                + "JOIN c.linkedServices s "
                + "WHERE c.id = :customerId AND s.id = :serviceId";

        TypedQuery<Long> query = getEntityManager()
                .createQuery(jpql, Long.class);
        query.setParameter("customerId", customerId);
        query.setParameter("serviceId", serviceId);

        Long count = query.getSingleResult();
        return count > 0;
    }
}


