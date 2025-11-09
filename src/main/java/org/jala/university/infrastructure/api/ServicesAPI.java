package org.jala.university.infrastructure.api;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class ServicesAPI {

    public static JSONObject getClientInvoices (String userCode, String serviceCode) {

        //TODO this is a simulation. Need to be changed then.

        JSONParser parser = new JSONParser();
        InputStream inputStream = ServicesAPI.class.getClassLoader().getResourceAsStream("json/invoices.json");
        if (inputStream == null) {
            return null;
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            JSONArray ja = (JSONArray) parser.parse(br);

            for (Object service : ja) {
                JSONObject obj = (JSONObject) service;
                if (obj.get("service_code").equals(serviceCode)) {
                    JSONArray clients = (JSONArray) obj.get("clients");
                    for (Object o : clients) {
                        JSONObject client = (JSONObject) o;
                        if(client.get("client_code").equals(userCode)) {
                            return client;
                        }
                    }
                }
            }

        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public static void main(String[] args) {
        getClientInvoices(null, null);
    }


}
