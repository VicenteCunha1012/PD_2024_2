package pt.isec.pd.Server.Data;

import java.sql.*;

/**
 * Classe que tem um Singleton da Conexão com a Base de dados. Vai ser usada pelo
 * Server e pelo ServerBackup.ServerBackup
 */
public class Database {
    public final static Database database = new Database(); // Singleton

    private String url;
    private boolean isSetup = false;
    private Connection conn;

    /**
     * Constructor vazio da Base de dados porque como é um singleton não lhe podemos passar os argumentos
     * de linha de comando na sua construção
     */
    private Database() {}

    public Connection getConn() { return conn; }

    public String getUrl() { return url; }

    /**
     * Função que vai ser chamada quando o ServerManager for criado para definir o path da db e dar-lhe setup
     * @param url Caminho para o ficheiro (com o user e o jbdc também)
     * @throws SQLException Exceção que a função pode atirar em caso de a conexão falhar ou o driver ser inválido
     */
    public void setupDB(String url, boolean dbFileExists) throws Exception {
        String jdbcUrl = "jdbc:sqlite:" + url;

        this.url = url;

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC driver not found.");
            e.printStackTrace();
            return;
        }

        if (!dbFileExists) {
            System.out.println("Database não existe, a criar...");
            CreateDB.create(jdbcUrl);
        }

        this.conn = DriverManager.getConnection(jdbcUrl);
        this.isSetup = true;
        System.out.println("DB setup Success!!");

    }

    public void disconnect() throws SQLException {
        if (this.conn != null && !this.conn.isClosed()) {
            this.conn.close();
        }
    }

    /**
     * Função usada para interagir com a Base de dados quando usarmos a função PreparedStatement.executeUpdate();
     * @param sql String sql passada para usar
     * @throws SQLException Exceção que a função pode atirar em caso de a conexão falhar ou o driver ser inválido
     */
    public int executeUpdate(String sql) throws SQLException {
        if(!this.isSetup) {return -1;}
        PreparedStatement ps = this.conn.prepareStatement(sql);
        return ps.executeUpdate();
    }

    /**
     * Função usada para interagir com a Base de dados quando usarmos a função PreparedStatement.executeQuery()
     * @param sql String sql passada para usar
     * @return A função retorna um ResultSet que vamos iterar para ver o que a query retornou
     * @throws SQLException Exceção que a função pode atirar em caso de a conexão falhar ou o driver ser inválido
     */
    public ResultSet executeQuery(String sql) throws SQLException {
        if(!this.isSetup) {return null;}
        PreparedStatement ps = this.conn.prepareStatement(sql);
        return ps.executeQuery();
    }


}
