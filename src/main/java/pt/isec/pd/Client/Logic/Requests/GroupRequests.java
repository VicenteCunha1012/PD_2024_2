package pt.isec.pd.Client.Logic.Requests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import pt.isec.pd.Client.Logic.Requests.Utils.ConnectionSetup;
import pt.isec.pd.Client.Logic.Requests.Utils.RequestMethod;
import pt.isec.pd.Shared.Entities.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GroupRequests {
    private String url;

    public GroupRequests(String baseUrl) {
        this.url = baseUrl + "/api/groups";
    }

    public List<ListedGroup> listGroups(String token) {
        try {
            HttpURLConnection conn = ConnectionSetup.setup(
                    new URL(this.url),
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
                    List<ListedGroup> groups = objectMapper.readValue(response, new TypeReference<List<ListedGroup>>() {});

                    return groups;
                }
            }
            return null;
        } catch (Exception e) { return null; }
    }

    public List<ListedUser> listGroupMembers(String groupName, String token) {
        try {
            HttpURLConnection conn = ConnectionSetup.setup(
                    new URL(this.url + '/' + URLEncoder.encode(groupName, StandardCharsets.UTF_8).replace("+", "%20")),
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
                    List<ListedUser> users = objectMapper.readValue(response, new TypeReference<List<ListedUser>>() {});

                    return users;
                }
            }
            return null;
        } catch (Exception e) { return null; }
    }
}
