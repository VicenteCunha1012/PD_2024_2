package pt.isec.pd.Client.Logic.Requests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import pt.isec.pd.Client.Logic.Requests.Utils.ConnectionSetup;
import pt.isec.pd.Client.Logic.Requests.Utils.RequestMethod;
import pt.isec.pd.Shared.Entities.Expense;
import pt.isec.pd.Shared.Entities.ListedExpense;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ExpenseRequests {
    private String url;

    public ExpenseRequests(String baseUrl, String groupName) {
        this.url = baseUrl
                + "/api/groups/"
                + URLEncoder.encode(groupName, StandardCharsets.UTF_8).replace("+", "%20");
    }

    public List<ListedExpense> listGroupExpenses(String token) {
        try {
            HttpURLConnection conn = ConnectionSetup.setup(
                    new URL(this.url + "/expenses"),
                    RequestMethod.GET,
                    token
            );

            conn.connect();

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder responseBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBuilder.append(line);
                    }

                    String response = responseBuilder.toString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<ListedExpense> expenses = objectMapper.readValue(response, new TypeReference<List<ListedExpense>>() {});

                    return expenses;
                }
            }
            return null;
        } catch (Exception e) { return null; }
    }

    public boolean addGroupExpense(Expense expense, String token) throws MalformedURLException {
        try {
            HttpURLConnection conn = ConnectionSetup.setup(
                    new URL(this.url + "/expenses"),
                    RequestMethod.POST,
                    token
            );

            conn.connect();

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonPayload = objectMapper.writeValueAsString(expense);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();

            if (conn != null) { conn.disconnect(); }

            return responseCode == HttpURLConnection.HTTP_OK;

        } catch (Exception e) { return false; }
    }

    public boolean deleteGroupExpense(int expense_id, String token) {
        try {

            HttpURLConnection conn = ConnectionSetup.setup(
                    new URL(this.url + '/' + Integer.toString(expense_id)),
                    RequestMethod.DELETE,
                    token
            );

            conn.connect();

            int responseCode = conn.getResponseCode();

            if (conn != null) { conn.disconnect(); }

            return responseCode == HttpURLConnection.HTTP_OK;

        } catch (Exception e) { return false; }
    }

}
