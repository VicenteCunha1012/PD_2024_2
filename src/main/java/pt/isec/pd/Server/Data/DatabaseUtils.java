package pt.isec.pd.Server.Data;

import org.springframework.security.core.parameters.P;
import pt.isec.pd.Shared.Entities.*;
import pt.isec.pd.Shared.Hasher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public static boolean login(String email, String password, Connection conn) throws Exception {
        resetAttributes();
        ArrayList<Object> args = new ArrayList<>();

        args.add(email);
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



    public static List<ListedGroup> GetUserGroupList(String email, Connection conn) throws Exception {
        resetAttributes();

        psw = new PreparedStatementWrapper(
                """
                        SELECT * FROM groups g 
                        JOIN group_members gm ON g.id = gm.group_id 
                        JOIN users u ON gm.user_id = u.id 
                        WHERE u.email = ?;
                        """,
                email
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

    public static List<ListedUser> GetGroupMembers(String groupName, Connection conn) throws Exception {
        resetAttributes();
        List<ListedUser> users = GetUserList(conn);
        ArrayList<ListedUser> groupMembers = new ArrayList<>();
        for(ListedUser user : users) {
            if(DatabaseUtils.IsUserInGroup(user.getEmail(), groupName, conn)) {
                groupMembers.add(user);
            }
        }
        return groupMembers;

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

    public static boolean GroupExists(String groupName, Connection conn)  {
        resetAttributes();
        boolean result = false;
        try {
            for(ListedGroup listedGroup : DatabaseUtils.GetGroupList(conn)) {
                if(listedGroup.getName().equals(groupName)) {
                    result = true;
                }
            }
        } catch (Exception e) {
            result = false;
        }
        return result;

    }

    public static boolean ExpenseExists(String groupName, Integer expenseId, Connection conn)  {
        resetAttributes();
        ArrayList<Object> args = new ArrayList<>();
        try {
            args.add(DatabaseUtils.GetGroupId(groupName, conn));
        } catch (Exception e) {
            return false;
        }
        args.add(expenseId);

        try {
            psw = new PreparedStatementWrapper(
                    "SELECT * FROM expenses WHERE group_id = ? AND id = ?;",
                    args
            );

            statement = psw.createPreparedStatement(conn);
            resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (Exception e) {
            return false;
        }
    }

    public static Integer GetGroupId(String groupName, Connection conn) throws Exception {
        resetAttributes();

        psw = new PreparedStatementWrapper(
                "SELECT id FROM groups WHERE name = ?;",
                groupName
        );

        statement = psw.createPreparedStatement(conn);
        resultSet = statement.executeQuery();
        resultSet.next();

        return resultSet.getInt("id");
    }

    public static boolean AddExpenseToGroup(String groupName, Expense expense, Connection conn) throws Exception {
        resetAttributes();

        List<Object> args = new ArrayList<>();

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

        if (updateCount == -1) { return false; }

        return divideExpense(
                expense.getPaid_by(),
                expense.getDebtors(),
                DatabaseUtils.getLastRowExpenseId(conn),
                DatabaseUtils.GetGroupId(groupName, conn),
                conn
        );
    }

    public static boolean divideExpense(int my_id, List<Integer> userId_list, int expenseId, int groupId, Connection conn) throws Exception {
        resetAttributes();

        double totalValue = getExpenseValue(expenseId, conn);
        double shareValue = Math.round(totalValue/userId_list.size() * 100.0) / 100.0;

        System.out.println(userId_list);

        List<Object> args = args = new ArrayList<>();

        for (Integer id : userId_list) {

            if (id == my_id) { continue; }

            args.clear();

            args.add(expenseId);
            args.add(id);
            args.add(shareValue);
            args.add(false);
            args.add(groupId);

            psw = new PreparedStatementWrapper(
                    "INSERT INTO expense_shares (expense_id, user_id, share, paid, group_id)" +
                            " VALUES (?, ?, ?, ?, ?)",
                    args
            );

            System.out.println(psw.toString());

            statement = psw.createPreparedStatement(conn);
            statement.execute();

            updateCount = statement.getUpdateCount();

            if (updateCount != 1) { return false; }

        }

        return true;
    }

    public static Integer getLastRowExpenseId(Connection conn) throws Exception {
        resetAttributes();
        List<Object> args = new ArrayList<>();
        Integer id;

        psw = new PreparedStatementWrapper(
                "SELECT id FROM expenses ORDER BY id DESC LIMIT 1"
        );

        System.out.println(psw.toString());

        statement = psw.createPreparedStatement(conn);
        statement.execute();

        resultSet = statement.getResultSet();


        if (resultSet == null) {
            throw new Exception("Esta tabela está vazia!");
        } else {
            return resultSet.getInt("id");
        }

    }

    private static double getExpenseValue(int expenseId, Connection conn) throws Exception {
        resetAttributes();
        psw = new PreparedStatementWrapper(
                "SELECT value FROM expenses WHERE id = ?;",
                (Integer) expenseId
        );

        statement = psw.createPreparedStatement(conn);
        statement.execute();

        resultSet = statement.getResultSet();

        return resultSet.getDouble("value");

    }

    //TODO nas funcoes a usar diretamente em endpoint nao por a dar throw para ficar mais limpinho
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

    public static boolean IsUserInGroup(String email, String groupName, Connection conn) {
        try {
            Integer userId = GetUserId(email, conn);
            Integer groupId = GetGroupId(groupName, conn);
            ArrayList<Object> args = new ArrayList<>();
            args.add(groupId);
            args.add(userId);
            psw = new PreparedStatementWrapper(
                    "SELECT * FROM group_members WHERE group_id = ? AND user_id = ?;",
                    args
            );
            statement = psw.createPreparedStatement(conn);
            resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (Exception e) {
            return false;
        }
    }

    public static Integer GetUserId(String email, Connection conn) throws Exception {
        resetAttributes();
        psw = new PreparedStatementWrapper(
                "SELECT id FROM users WHERE email = ?;",
                List.of(email)
        );

        statement = psw.createPreparedStatement(conn);
        resultSet = statement.executeQuery();
        if(!resultSet.next()) throw new Exception("user email not found");
        return resultSet.getInt("id");
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

            ));
        }
        return expenses;
    }

}