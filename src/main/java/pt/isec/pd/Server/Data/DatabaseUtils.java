package pt.isec.pd.Server.Data;

import org.springframework.security.core.parameters.P;
import pt.isec.pd.Shared.Entities.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de funções auxiliares para a base de dados
 */
public class DatabaseUtils {
    static PreparedStatementWrapper psw;
    static ResultSet resultSet;
    static int updateCount;
    static PreparedStatement statement;

    private static void resetAttributes() {
        psw = null;
        resultSet = null;
        updateCount = 0;
        statement = null;
    }

    public static boolean registar(User user, Connection conn) throws Exception {
        resetAttributes();
        ArrayList<Object> args = new ArrayList<>();

        args.add(user.getName());
        args.add(user.getContact());
        args.add(user.getEmail());
        args.add(user.getPassword());

        psw = new PreparedStatementWrapper(
                "INSERT INTO users (name, contact, email, password)" +
                        "VALUES (?, ?, ?, ?)",
                args);

        statement = psw.createPreparedStatement(conn);
        statement.execute();

        updateCount = statement.getUpdateCount();

        return updateCount != -1;
    }

    public static boolean login(String username, String password, Connection conn) throws Exception {
        resetAttributes();
        ArrayList<Object> args = new ArrayList<>();

        args.add(username);
        args.add(password);

        psw = new PreparedStatementWrapper(
                "SELECT * FROM users WHERE email = ? AND password = ?;",
                args
        );

        statement = psw.createPreparedStatement(conn);
        resultSet = statement.executeQuery();

        return resultSet.next();
    }

    public static List<ListedUser> GetUserList(Connection conn) throws Exception {
        resetAttributes();
        psw = new PreparedStatementWrapper(
                "SELECT * FROM users"
        );

        ArrayList<ListedUser> users = new ArrayList<>();

        statement = psw.createPreparedStatement(conn);
        resultSet = statement.executeQuery();

        while (resultSet.next()) {
            users.add(new ListedUser(
                    resultSet.getInt("id"),
                    resultSet.getDate("creation_date"),
                    resultSet.getString("name"),
                    resultSet.getString("contact"),
                    resultSet.getString("email"),
                    ""
            ));
        }

        return users;
    }

    public static List<ListedGroup> GetGroupList(Connection conn) throws Exception {
        resetAttributes();
        psw = new PreparedStatementWrapper(
                "SELECT * FROM groups"
        );

        ArrayList<ListedGroup> groups = new ArrayList<>();

        statement = psw.createPreparedStatement(conn);
        resultSet = statement.executeQuery();

        while (resultSet.next()) {
            groups.add(new ListedGroup(
                    resultSet.getInt("id"),
                    resultSet.getDate("creation_date"),
                    resultSet.getString("name"),
                    resultSet.getInt("owner_id")
            ));
        }

        return groups;

    }

    public static Integer GetGroupId(String groupName, Connection conn) throws Exception {
        resetAttributes();
        ArrayList<Object> args = new ArrayList<>();

        args.add(groupName);

        psw = new PreparedStatementWrapper(
                "SELECT id FROM groups WHERE name = ?;",
                args
        );

        statement = psw.createPreparedStatement(conn);
        resultSet = statement.executeQuery();
        resultSet.next();

        return resultSet.getInt("id");
    }

    public static boolean AddExpenseToGroup(String groupName, Expense expense, Connection conn) throws Exception {
        resetAttributes();
        ArrayList<Object> args = new ArrayList<>();

        args.add(expense.getDescription());
        args.add(expense.getValue());
        args.add(expense.getPaid_by());
        args.add(DatabaseUtils.GetGroupId(groupName, conn));

        psw = new PreparedStatementWrapper(
                "INSERT INTO expenses (description, value, paid_by, group_id) " +
                        "VALUES (?, ?, ?, ?);",
                args
        );

        statement = psw.createPreparedStatement(conn);
        statement.execute();

        updateCount = statement.getUpdateCount();

        return updateCount != 1;
    }

    public static boolean DeleteExpenseFromGroup(Integer expenseId, Connection conn) throws Exception {
        resetAttributes();

        psw = new PreparedStatementWrapper(
                "DELETE FROM expenses " +
                        "WHERE id = ?;",
                expenseId
        );

        statement = psw.createPreparedStatement(conn);
        statement.execute();

        updateCount = statement.getUpdateCount();

        return updateCount != -1;
    }


    public static List<ListedExpense> GetExpenseListFromGroup(String groupName, Connection conn) throws Exception {
        resetAttributes();
        psw = new PreparedStatementWrapper(
                "SELECT * FROM expenses WHERE group_id = ?;",
                DatabaseUtils.GetGroupId(groupName, conn)
        );

        ArrayList<ListedExpense> expenses = new ArrayList<>();
        statement = psw.createPreparedStatement(conn);
        resultSet = statement.executeQuery();

        while (resultSet.next()) {
            expenses.add(new ListedExpense(
                    resultSet.getInt("id"),
                    resultSet.getDate("creation_date"),
                    resultSet.getString("description"),
                    resultSet.getDouble("value"),
                    resultSet.getBoolean("paid"),
                    resultSet.getInt("paid_by"),
                    resultSet.getInt("group_id")
            ));;
        }
        return expenses;
    }

}