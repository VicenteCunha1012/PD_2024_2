package pt.isec.pd.Client.Logic.Requests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import pt.isec.pd.Shared.Entities.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class GroupRequests {
    private static String BASE_URL = "/api/groups";

    public static List<ListedGroup> listGroups(String url, String email, String token) {
        URL requestUrl;
        HttpURLConnection conn;
        int responseCode;
        List<ListedGroup> groups = new ArrayList<>();

        try {
            requestUrl = new URL(url + BASE_URL);

            try {
                conn = (HttpURLConnection) requestUrl.openConnection();
            } catch (ConnectException e) {
                throw new Exception("Falha ao conectar ao Servidor!", e);
            }

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            conn.connect();

            responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder responseBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBuilder.append(line);
                    }

                    String response = responseBuilder.toString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    groups = objectMapper.readValue(response, new TypeReference<List<ListedGroup>>() {});
                }
            } else {
                return null;
            }

        } catch (Exception e) {
            throw new RuntimeException("An error occurred while fetching the group list!", e);
        }

        return groups;
    }

    public static List<ListedUser> listGroupMembers(String groupName, String url, String token) {
        URL requestUrl;
        HttpURLConnection conn;
        int responseCode;
        List<ListedUser> users = new ArrayList<>();

        try {
            requestUrl = new URL(url + BASE_URL + '/' + groupName);

            try {
                conn = (HttpURLConnection) requestUrl.openConnection();
            } catch (ConnectException e) {
                throw new Exception("Falha ao conectar ao Servidor!", e);
            }

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            conn.connect();

            responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder responseBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBuilder.append(line);
                    }

                    String response = responseBuilder.toString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    users = objectMapper.readValue(response, new TypeReference<List<ListedUser>>() {});
                }
            } else {
                return null;
            }

        } catch (Exception e) {
            throw new RuntimeException("Exceção ao listar Users!", e);
        }

        return users;
    }

    public static boolean addGroupExpense(String groupName, Expense expense, String url, String token) throws MalformedURLException {
        URL requestUrl;
        HttpURLConnection conn;
        int responseCode;

        try {
            requestUrl = new URL(url + BASE_URL + '/' + groupName + "/expenses");

            try {
                conn = (HttpURLConnection) requestUrl.openConnection();
            } catch (Exception e) {
                throw new Exception("Falha ao conectar ao Servidor!", e);
            }

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            conn.connect();

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonPayload = objectMapper.writeValueAsString(expense);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            responseCode = conn.getResponseCode();

            System.out.println("ReponseCode: " + responseCode);

            if (conn != null) { conn.disconnect(); }

            return responseCode == HttpURLConnection.HTTP_OK;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<ListedExpense> listGroupExpenses(String url, String groupName, String token) {
        URL requestUrl;
        HttpURLConnection conn;
        int responseCode;
        List<ListedExpense> expenses = new ArrayList<>();

        try {
            requestUrl = new URL(url + BASE_URL + '/' + groupName + "/expenses");


            try {
                conn = (HttpURLConnection) requestUrl.openConnection();
            } catch (ConnectException e) {
                throw new Exception("Falha ao conectar ao Servidor!", e);
            }

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            conn.connect();

            responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder responseBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBuilder.append(line);
                    }

                    String response = responseBuilder.toString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    expenses = objectMapper.readValue(response, new TypeReference<List<ListedExpense>>() {});
                }
            } else {
                return null;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return expenses;
    }

    public static boolean deleteGroupExpense(String groupName, int expense_id, String url, String token) {
        URL requestUrl;
        HttpURLConnection conn;
        int responseCode;

        try {
            requestUrl = new URL(url + BASE_URL + '/' + groupName + '/' + expense_id);

            try {
                conn = (HttpURLConnection) requestUrl.openConnection();
            } catch (ConnectException e) {
                throw new Exception("Falha ao conectar ao Servidor!", e);
            }

            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", "Bearer " + token);

            conn.connect();

            responseCode = conn.getResponseCode();

            if (conn != null) { conn.disconnect(); }

            return responseCode == HttpURLConnection.HTTP_OK;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
