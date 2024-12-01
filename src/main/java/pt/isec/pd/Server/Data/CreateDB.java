package pt.isec.pd.Server.Data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe auxiliar para criar uma base de dados nova
 */
public class CreateDB {
    private static final String DB_SCRIPT = Paths.get("src", "main", "resources", "database", "scripts", "CreateScript.sql").toString();
    private static final String DATA_SCRIPT = Paths.get("src", "main", "resources", "database", "scripts", "DataScript.sql").toString();

    /**
     * Função para efetivamente criar a bd
     * @param dbPath
     * @return
     * @throws Exception
     */
    public static boolean create(String dbPath) throws Exception {

        try (Connection conn = DriverManager.getConnection(dbPath)) {
            List<String> sqlStatements = getSQLStatements(DB_SCRIPT);
            executeSQLStatements(conn, sqlStatements);
            System.out.println("Database Criada!");

            System.out.println("A encher a database com fake data...");
            List<String> insertDataSQLStatements = getSQLStatements(DATA_SCRIPT);
            executeSQLStatements(conn, insertDataSQLStatements);

            conn.prepareStatement("INSERT INTO db_versions (id) VALUES (0)").execute();

            conn.close();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;

    }


    private static List<String> getSQLStatements(String filePath) {
        List<String> sqlStatements = new ArrayList<>();
        boolean isTrigger = false;

        try (BufferedReader reader  = new BufferedReader(new FileReader(filePath))) {
            StringBuilder sqlStatement = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.contains("--") || line.contains("//") || line.contains("/*")) {
                    continue;
                }

                if (line.toUpperCase().contains("CREATE TRIGGER")) {
                    isTrigger = true;
                }

                sqlStatement.append(line.trim()).append(" ");

                if (isTrigger && line.toUpperCase().contains("END;")) {
                    sqlStatements.add(sqlStatement.toString().trim());
                    sqlStatement.setLength(0);
                    isTrigger = false;
                }

                else if (!isTrigger && line.trim().endsWith(";")) {
                    sqlStatements.add(sqlStatement.toString().trim());
                    sqlStatement.setLength(0); // Reset for next statement
                }
            }

            if (sqlStatement.length() > 0) {
                sqlStatements.add(sqlStatement.toString().trim());
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return sqlStatements;
    }


    private static void executeSQLStatements(Connection connection, List<String> sqlStatements) throws SQLException, SQLException {
        try (Statement statement = connection.createStatement()) {
            for (String sql : sqlStatements) {
                //System.out.println("+--------------------------------------------------------------------------------------------+");
                //System.out.println(sql);
                statement.execute(sql);
            }
        }
    }

}
