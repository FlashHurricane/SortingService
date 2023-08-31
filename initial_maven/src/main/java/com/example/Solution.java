package com.example;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Scanner;
public class Solution {

  public static void main (String args[]){
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
            server.createContext("/sort", new SortHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("Server started on port 8000");
        } catch (IOException e){
            System.err.println("Error starting the server: " + e.getMessage());
        }
    }

    static class SortHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if ("POST".equals(exchange.getRequestMethod())) {
                    InputStream requestBody = exchange.getRequestBody();
                    Scanner scanner = new Scanner(requestBody).useDelimiter("\\A");
                    String requestBodyString = scanner.hasNext() ? scanner.next() : "";
                    scanner.close();

                    JSONObject requestJson = new JSONObject(requestBodyString);
                    JSONArray sortKeys = requestJson.getJSONArray("sortKeys");
                    JSONObject payload = requestJson.getJSONObject("payload");

                    sortKeys.forEach(key -> {
                        if (payload.has(key.toString())) {
                            JSONArray array = payload.getJSONArray(key.toString());
                            array = sortArray(array);
                            payload.put(key.toString(), array);
                        }
                    });

                    exchange.sendResponseHeaders(200, 0);
                    OutputStream responseBody = exchange.getResponseBody();
                    responseBody.write(payload.toString().getBytes());
                    responseBody.close();
                } else {
                    exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                }
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(400, -1); // Bad Request
            } finally {
                exchange.close();
            }
        }

        private JSONArray sortArray(JSONArray array) {
            int length = array.length();
            int[] sortedArray = new int[length];
            for (int i = 0; i < length; i++) {
                sortedArray[i] = array.getInt(i);
            }
            Arrays.sort(sortedArray);
            JSONArray resultArray = new JSONArray();
            for (int num : sortedArray) {
                resultArray.put(num);
            }
            return resultArray;
        }
    }
}
