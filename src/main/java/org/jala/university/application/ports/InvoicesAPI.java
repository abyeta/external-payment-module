package org.jala.university.application.ports;

import org.json.simple.JSONArray;

public interface InvoicesAPI {

    JSONArray getInvoices(String clientCode, String serviceCode);
    boolean updateStatus(String invoiceCode, String serviceCode) throws IllegalAccessException;

}
