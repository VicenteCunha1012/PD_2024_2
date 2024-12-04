package pt.isec.pd.Client.Logic.Requests;

import pt.isec.pd.Shared.Entities.Expense;
import pt.isec.pd.Shared.Entities.Group;

import java.util.ArrayList;
import java.util.List;

public class GroupRequests {
    private static String URL = "/api/groups";

    public static List<Group> listGroups(String url) {
        String requestUrl = url + URL + '/';
        // TODO
        return new ArrayList<>();
    }

    public static boolean addGroupExpense(String groupName, Expense expense, String url) {
        String requestUrl = url + URL + '/' + groupName + "/expenses";
        // TODO
        return false;
    }

    public static List<Expense> listGroupExpenses(String groupName, String url) {
        String requestUrl = url + URL + '/' + groupName + "/expenses";
        // TODO
        return new ArrayList<>();
    }

    public static boolean deleteGroupExpense(String groupName, int expense_id, String url) {
        String requestUrl = url + URL + '/' + groupName + '/' + expense_id;
        // TODO
        return false;
    }

}
