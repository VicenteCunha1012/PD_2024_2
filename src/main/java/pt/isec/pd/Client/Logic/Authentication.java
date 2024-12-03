package pt.isec.pd.Client.Logic;

import com.google.gson.Gson;

import pt.isec.pd.Shared.Entities.User;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class Authentication {
    private  String ip = new String("localhost");
    private  int port = 8080;

    public Authentication(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String registar(String nome, String contacto, String email, String password) {
        URL url;
        HttpURLConnection conn;
        String boundary = UUID.randomUUID().toString(); // Generate a unique boundary
        String lineEnd = "\r\n";
        String twoHyphens = "--";

        int responseCode;
        StringBuilder response = new StringBuilder();
        String responseLine;

        User user = new User(nome, contacto, email, password);

        try {
            url = new URL("http://" + ip + ':' + port + "/api/auth/register");

            try {
                conn = (HttpURLConnection) url.openConnection();
            } catch (ConnectException e) {
                System.out.println("Impossível conectar ao servidor!");
                return "Impossível conectar ao servidor!";
            }

            conn.setRequestMethod("POST");

            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            conn.setDoOutput(true);

            conn.connect();

            Gson gson = new Gson();
            String jsonPayload = gson.toJson(user);

            try (OutputStream outputStream = conn.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true)) {

                addFormField(writer, "nome", nome, boundary);
                addFormField(writer, "contact", contacto, boundary);
                addFormField(writer, "email", email, boundary);
                addFormField(writer, "password", password, boundary);

                writer.append(twoHyphens).append(boundary).append(twoHyphens).append(lineEnd);
                writer.flush();
            }

            responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader buff = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                )) {
                    response = new StringBuilder();
                    while ((responseLine = buff.readLine()) != null) {
                        response.append(responseLine);
                    }

                    if (conn != null) { conn.disconnect(); }

                    return responseCode + "";
                }
            } else {

                System.out.println("ResponseCode: " + responseCode);

                if (conn != null) { conn.disconnect(); }
                return responseCode + "";

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Helper method to add a form field
    private void addFormField(PrintWriter writer, String name, String value, String boundary) {
        String lineEnd = "\r\n";
        String twoHyphens = "--";

        writer.append(twoHyphens).append(boundary).append(lineEnd);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"" + lineEnd);
        writer.append("Content-Type: text/plain; charset=UTF-8" + lineEnd); // Specify content type
        writer.append(lineEnd);
        writer.append(value).append(lineEnd);
        writer.flush();
    }

}
