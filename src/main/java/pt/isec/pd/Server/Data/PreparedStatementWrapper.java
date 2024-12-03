package pt.isec.pd.Server.Data;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper de PreparedStatement usado para transmitir informação nos QueryHeartBeats
 */
public class PreparedStatementWrapper implements Serializable {
    private String query;
    private List<Object> arguments;
    private static ArrayList<PreparedStatement> statementsToFreeLater = new ArrayList<>();

    public static void CloseAllPendingStatements() {
        for(PreparedStatement ps : statementsToFreeLater) {
            try {
                ps.close();
            } catch (SQLException e) {}
        }
        statementsToFreeLater.clear();
    }

    public PreparedStatementWrapper(String query, List<Object> arguments) throws SQLException {
        this.query = query;
        this.arguments = arguments;
    }

    public PreparedStatementWrapper(String query, Object argument) throws SQLException {
        this.query = query;
        this.arguments = new ArrayList<>();
        this.arguments.add(argument);
    }

    public PreparedStatementWrapper(String query) throws SQLException {
        this.query = query;
        this.arguments = new ArrayList<>();
    }

    public String getQuery() { return query; }

    public List<Object> getArguments() {
        return new ArrayList<>(arguments);
    }

    public PreparedStatement createPreparedStatement(Connection conn) throws Exception {

        if (conn == null) {
            throw new Exception("Conexão é nula!");
        } else if (conn.isClosed()) {
            throw  new Exception("Conexão encontra-se fechada!");
        }

        PreparedStatement preparedStatement = conn.prepareStatement(this.query);

        for (int i = 0; i < this.arguments.size(); i++) {
            Object parameter = this.arguments.get(i);

            if (parameter == null) {
                preparedStatement.setNull(i + 1, java.sql.Types.NULL);
            } else {
                preparedStatement.setObject(i + 1, parameter);
            }
        }

        statementsToFreeLater.add(preparedStatement);
        return preparedStatement;
    }

    public String toString() {
        String str = "Query: " + this.query + "\nArguments:\n";

        for (int i = 0; i < this.arguments.size(); i++) {
            str += (i + 1) + ". " + this.arguments.get(i).toString() + '\n';
        }

        return str;
    }

}