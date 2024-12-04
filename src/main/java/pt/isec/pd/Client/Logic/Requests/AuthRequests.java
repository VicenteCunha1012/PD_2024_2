package pt.isec.pd.Client.Logic.Requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import pt.isec.pd.Shared.Entities.User;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class AuthRequests {
    private static String URL = "/api/auth";

    public static boolean register(User user, String url) {
        URL requestUrl = null;
        HttpURLConnection conn;
        int responseCode;

        try {
            requestUrl = new URL(url + URL + "/register");

            try {
                conn = (HttpURLConnection) requestUrl.openConnection();
            } catch (ConnectException e) {
                throw new Exception("Impossível conectar ao servidor!");
            }

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            conn.connect();

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonPayload = objectMapper.writeValueAsString(user);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            responseCode = conn.getResponseCode();

            if (conn != null) { conn.disconnect(); }

            return responseCode == HttpURLConnection.HTTP_OK;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String login(String email, String password, String url) {
        String requestUrl = url + URL + "/login";

        try {
            String basicAuthHeader = "Basic " + Base64.getEncoder()
                    .encodeToString((email + ":" + password).getBytes());

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header("Authorization", basicAuthHeader)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


}
