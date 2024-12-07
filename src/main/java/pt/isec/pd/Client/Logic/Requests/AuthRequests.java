package pt.isec.pd.Client.Logic.Requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import pt.isec.pd.Client.Logic.Requests.Utils.ConnectionSetup;
import pt.isec.pd.Client.Logic.Requests.Utils.RequestMethod;
import pt.isec.pd.Shared.Entities.User;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class AuthRequests {
    private String url;

    public AuthRequests(String baseUrl) {
        this.url = baseUrl + "/api/auth";
    }

    public boolean register(User user) {
        try {
            HttpURLConnection conn = ConnectionSetup.setup(
                    new URL(this.url + "/register"),
                    RequestMethod.POST,
                    null
            );

            conn.connect();

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonPayload = objectMapper.writeValueAsString(user);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = responseCode = conn.getResponseCode();

            if (conn != null) { conn.disconnect(); }

            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (Exception e) { return false; }
    }

    public String login(String email, String password) {
        try {
            String basicAuthHeader = "Basic " + Base64.getEncoder()
                    .encodeToString((email + ":" + password).getBytes());

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.url + "/login"))
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
