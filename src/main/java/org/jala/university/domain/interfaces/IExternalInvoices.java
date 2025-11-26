package org.jala.university.domain.interfaces;

import org.json.simple.JSONArray;

/**
 * Repository interface for external invoices operations.
 * Following Onion Architecture, this interface is defined in the domain layer
 * and implemented by the infrastructure layer to access external invoice services.
 */
public interface IExternalInvoices {

    /**
     * Retrieves invoices for a specific client and service.
     *
     * @param clientCode the client code to search for
     * @param serviceCode the service code to search for
     * @return a JSONArray containing the invoices
     * @throws IllegalArgumentException if client or service is not found
     */
    JSONArray getInvoices(String clientCode, String serviceCode) throws IllegalArgumentException;

    /**
     * Updates the status of an invoice to "paid".
     *
     * @param invoiceCode the invoice code to update
     * @param serviceCode the service code to which the invoice belongs
     * @return true if the invoice was successfully updated, false otherwise
     * @throws IllegalAccessException if the operation cannot be performed
     */
    boolean updateStatus(String invoiceCode, String serviceCode) throws IllegalAccessException;

    /**
     * Retrieves all paid invoices for a specific client across all services.
     *
     * @param clientCode the client code to search for
     * @return a JSONArray containing all paid invoices for the client
     * @throws IllegalArgumentException if the client is not found
     */
    JSONArray getPaidInvoices(String clientCode) throws IllegalArgumentException;

}

