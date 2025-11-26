package org.jala.university.infrastructure.external;

import org.jala.university.domain.interfaces.IExternalInvoices;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

/**
 * Implementation of ExternalInvoicesRepository for accessing external invoice services.
 * This class belongs to the infrastructure/external layer in Onion Architecture,
 * providing concrete implementation for the interface defined in the domain layer.
 * Currently simulates an external API by reading from a JSON file.
 * In production, this would make actual HTTP calls to an external service.
 */
public final class ExternalInvoicesImpl implements IExternalInvoices {

    private final String jsonPath = "invoices.json";
    private final JSONParser parser = new JSONParser();

    /**
     * Retrieves invoices for a specific client and service from the external system.
     *
     * @param clientCode the client code to search for
     * @param serviceCode the service code to search for
     * @return a JSONArray containing the invoices
     * @throws IllegalArgumentException if client or service is not found
     */
    @Override
    public JSONArray getInvoices(String clientCode, String serviceCode) throws IllegalArgumentException {
        //TODO this is a simulation. Need to be changed then.
        try (FileReader reader = new FileReader(jsonPath)) {
            JSONArray ja = (JSONArray) parser.parse(reader);

            for (Object service : ja) {
                JSONObject obj = (JSONObject) service;
                if (obj.get("service_code").equals(serviceCode)) {
                    JSONArray clients = (JSONArray) obj.get("clients");
                    for (Object o : clients) {
                        JSONObject client = (JSONObject) o;
                        if (client.get("client_code").equals(clientCode)) {
                            return (JSONArray) client.get("invoices");
                        }
                    }
                    throw new IllegalArgumentException("Client not found");
                }
            }
            throw new IllegalArgumentException("Service not found");

        } catch (IOException | ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates the status of an invoice to "paid" in the external system.
     *
     * @param invoiceCode the invoice code to update
     * @param serviceCode the service code to which the invoice belongs
     * @return true if the invoice was successfully updated, false otherwise
     * @throws IllegalAccessException if the operation cannot be performed
     */
    @Override
    public boolean updateStatus(String invoiceCode, String serviceCode) throws IllegalAccessException {

        try (FileReader reader = new FileReader(jsonPath)) {
            JSONArray ja = (JSONArray) parser.parse(reader);

            for (Object service : ja) {
                JSONObject obj = (JSONObject) service;
                if (obj.get("service_code").equals(serviceCode)) {
                    JSONArray clients = (JSONArray) obj.get("clients");
                    for (Object o : clients) {
                        JSONObject client = (JSONObject) o;
                        JSONArray invoices = (JSONArray) client.get("invoices");
                        for (Object invoice : invoices) {
                            JSONObject invoiceObj = (JSONObject) invoice;
                            if (invoiceObj.get("code").equals(invoiceCode)) {
                                invoiceObj.put("status", "paid");

                                try (FileWriter fw = new FileWriter(jsonPath)) {
                                    fw.write(ja.toJSONString());
                                    System.out.println(ja);
                                    fw.flush();
                                } catch (IOException e) {
                                    throw new IllegalAccessException("json no encontrado");
                                }

                                return true;
                            }
                        }
                    }
                }
            }

        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    /**
     * Retrieves all paid invoices for a specific client across all services.
     *
     * @param clientCode the client code to search for
     * @return a JSONArray containing all paid invoices for the client
     * @throws IllegalArgumentException if the client is not found
     */
    @Override
    @SuppressWarnings("unchecked")
    public JSONArray getPaidInvoices(String clientCode) throws IllegalArgumentException {
        try (FileReader reader = new FileReader(jsonPath)) {
            JSONArray ja = (JSONArray) parser.parse(reader);
            JSONArray paidInvoices = new JSONArray();

            for (Object service : ja) {
                JSONObject serviceObj = (JSONObject) service;
                String serviceCode = (String) serviceObj.get("service_code");
                JSONArray clients = (JSONArray) serviceObj.get("clients");

                for (Object o : clients) {
                    JSONObject client = (JSONObject) o;
                    if (client.get("client_code").equals(clientCode)) {
                        JSONArray invoices = (JSONArray) client.get("invoices");
                        for (Object invoice : invoices) {
                            JSONObject invoiceObj = (JSONObject) invoice;
                            String status = invoiceObj.get("status").toString().toLowerCase();
                            if (status.equals("paid")) {
                                // Create a copy with service information
                                JSONObject paidInvoice = new JSONObject();
                                paidInvoice.put("code", invoiceObj.get("code"));
                                paidInvoice.put("expedition_date", invoiceObj.get("expedition_date"));
                                paidInvoice.put("expiration_date", invoiceObj.get("expiration_date"));
                                paidInvoice.put("client_name", invoiceObj.get("client_name"));
                                paidInvoice.put("status", invoiceObj.get("status"));
                                paidInvoice.put("description", invoiceObj.get("description"));
                                paidInvoice.put("amount", invoiceObj.get("amount"));
                                paidInvoice.put("service_code", serviceCode);
                                paidInvoices.add(paidInvoice);
                            }
                        }
                    }
                }
            }

            return paidInvoices;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}

