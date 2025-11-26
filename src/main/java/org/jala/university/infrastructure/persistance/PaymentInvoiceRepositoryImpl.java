package org.jala.university.infrastructure.persistance;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.jala.university.commons.infrastructure.persistance.CrudRepository;
import org.jala.university.domain.entity.PaymentInvoice;
import org.jala.university.domain.repository.PaymentInvoiceRepository;

import java.util.List;
import java.util.UUID;

public class PaymentInvoiceRepositoryImpl
    extends CrudRepository<PaymentInvoice, UUID>
    implements PaymentInvoiceRepository {

    public PaymentInvoiceRepositoryImpl(EntityManager entityManager) {
        super(PaymentInvoice.class, entityManager);
    }

    /**
     * Saves a payment invoice entity and flushes to ensure the ID is generated.
     *
     * @param entity the payment invoice entity to save
     * @return the saved entity with the generated ID
     */
    @Override
    public PaymentInvoice saveAndFlush(PaymentInvoice entity) {
        EntityManager em = getEntityManager();
        jakarta.persistence.EntityTransaction tx = em.getTransaction();
        boolean startedHere = false;
        if (!tx.isActive()) {
            tx.begin();
            startedHere = true;
        }

        PaymentInvoice managed;
        if (entity.getId() == null) {
            em.persist(entity);
            managed = entity;
        } else {
            managed = em.merge(entity);
        }

        em.flush();

        if (startedHere) {
            tx.commit();
        }

        return managed;
    }

    /**
     * Get all payment invoices of a specific customer.
     * @param customerId id of the customer.
     * @return A list of invoices.
     */
    @Override
    public List<PaymentInvoice> findByCustomerId(Long customerId) {

        if (customerId == null) {
            return List.of();
        }

        TypedQuery<PaymentInvoice> query = getEntityManager().createQuery(
                "SELECT pi FROM PaymentInvoice pi WHERE pi.customer.id =: id", PaymentInvoice.class);
        query.setParameter("id", customerId);
        return query.getResultList();
    }

    /**
     * Get all payment invoices of a specific external service.
     * @param serviceId id of the external service.
     * @return A list of invoices.
     */
    @Override
    public List<PaymentInvoice> findByServiceId(UUID serviceId) {
        if (serviceId == null) {
            return List.of();
        }

        TypedQuery<PaymentInvoice> query = getEntityManager().createQuery(
                "SELECT pi FROM PaymentInvoice pi WHERE pi.externalService.id =: id", PaymentInvoice.class);
        query.setParameter("id", serviceId);
        return query.getResultList();
    }

    /**
     * Get the payment invoices of the client by service.
     * @param customerId id of the customer.
     * @param serviceId id of the service.
     * @return A list of the invoices
     */
    @Override
    public List<PaymentInvoice> findByCustomerAndService(Long customerId, UUID serviceId) {
        if (serviceId == null || customerId == null) {
            return List.of();
        }

        TypedQuery<PaymentInvoice> query = getEntityManager().createQuery(
                "SELECT pi FROM PaymentInvoice pi WHERE pi.externalService.id =: serviceId "
                       + "AND pi.customer.id =: customerId",
                PaymentInvoice.class);
        query.setParameter("serviceId", serviceId);
        query.setParameter("customerId", customerId);

        return query.getResultList();
    }
}
