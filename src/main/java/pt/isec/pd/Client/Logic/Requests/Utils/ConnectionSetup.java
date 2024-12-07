package pt.isec.pd.Client.Logic.Requests.Utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectionSetup {
    public static HttpURLConnection setup(URL requestUrl, RequestMethod method, String token) throws Exception {
        HttpURLConnection conn;

        try {
            conn = (HttpURLConnection) requestUrl.openConnection();

            conn.setRequestMethod(method.name());

            switch (method) {
                case DELETE:
                case GET:
                    conn.setRequestProperty("Authorization", "Bearer " + token);
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    break;
                case POST:
                    if (token != null) {
                        conn.setRequestProperty("Authorization", "Bearer " + token);
                    }
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setDoOutput(true);
                    break;
                default:
                    throw new IllegalArgumentException("Método HTTP não suportado: " + method);
            }
            return conn;

        } catch (IOException e) {
            throw new Exception("Impossível conectar ao servidor!");
        }

    }
}
