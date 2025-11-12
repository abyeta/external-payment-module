package org.jala.university.infrastructure.api;

import org.jala.university.application.ports.InvoicesAPI;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;

public final class ServicesAPI implements InvoicesAPI {

    private final String jsonPath = "invoices.json";
    private final JSONParser parser = new JSONParser();

    /**
     * Get the invoices from an external service by its client code  and service code.
     * @param clientCode The client user to search for.
     * @param serviceCode The service code to search for.
     * @return A json with the invoices.
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
     * Update the state of an invoice to "paid".
     * @param invoiceCode code of invoice to search for.
     * @param serviceCode service to which the invoice belongs
     * @return true if the invoice was found. false if the invoice was not found.
     * @throws IllegalAccessException if the json was not found (only for the simulation).
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

                                try (FileWriter fw = new FileWriter("invoices.json")) {
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


}
