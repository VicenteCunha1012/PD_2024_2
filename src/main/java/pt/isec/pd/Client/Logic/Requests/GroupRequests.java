package pt.isec.pd.Client.Logic.Requests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import pt.isec.pd.Shared.Entities.Expense;
import pt.isec.pd.Shared.Entities.Group;
import pt.isec.pd.Shared.Entities.ListedExpense;
import pt.isec.pd.Shared.Entities.ListedGroup;

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

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while fetching the group list!", e);
        }

        return groups;
    }


    public static boolean addGroupExpense(String groupName, Expense expense, String url) throws MalformedURLException {
        URL requestUrl;
        HttpURLConnection conn;
        int responseCode;;

        try {
            requestUrl = new URL(url + BASE_URL + '/' + groupName + "/expenses");

            try {
                conn = (HttpURLConnection) requestUrl.openConnection();
            } catch (Exception e) {
                throw new Exception("Falha ao conectar ao Servidor!", e);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    public static List<ListedExpense> listGroupExpenses(String groupName, String url) {
        URL requestUrl;
        HttpURLConnection conn;
        int responseCode;

        try {
            requestUrl = new URL(url + BASE_URL + '/' + groupName + "/expenses");

            try {
                conn = (HttpURLConnection) requestUrl.openConnection();
            } catch (ConnectException e) {
                throw new Exception("Falha ao conectar ao Servidor!", e);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new ArrayList<>();
    }

    public static boolean deleteGroupExpense(String groupName, int expense_id, String url) {
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

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return false;
    }

}
