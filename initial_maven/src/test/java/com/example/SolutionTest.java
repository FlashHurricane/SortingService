package com.example;


import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class SolutionTest {

    @Test
    public void testApp() throws IOException {
        // Start the App in a separate thread
        Thread thread = new Thread(() -> {
            try {
                Solution.main(new String[]{});
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();

        // Wait for the App to start
        try {
            Thread.sleep(1000); // Adjust as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Make an HTTP POST request to test the App
        String requestBody = "{\"sortKeys\":[\"fruits\",\"numbers\"],\"payload\":{\"fruits\":[\"watermelon\",\"apple\",\"pineapple\"],\"numbers\":[1333,4,2431,7],\"colors\":[\"green\",\"blue\",\"yellow\"]}}";
        String response = sendPostRequest("http://localhost:8000/sort", requestBody);

        // Stop the App
        thread.interrupt();

        // Verify the response
        String expectedResponse = "{\"fruits\":[\"apple\",\"pineapple\",\"watermelon\"],\"numbers\":[4,7,1333,2431],\"colors\":[\"green\",\"blue\",\"yellow\"]}";
        Assert.assertEquals(expectedResponse, response);
    }

    private String sendPostRequest(String url, String requestBody) throws IOException {
        URL postUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (Scanner scanner = new Scanner(connection.getInputStream())) {
                scanner.useDelimiter("\\A");
                return scanner.hasNext() ? scanner.next() : "";
            }
        } else {
            throw new IOException("HTTP POST request failed with response code: " + responseCode);
        }
    }
}
