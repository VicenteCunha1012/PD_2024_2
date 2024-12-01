package pt.isec.pd.Server.Springboot.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.isec.pd.Server.Data.Database;
import pt.isec.pd.Server.Data.DatabaseUtils;
import pt.isec.pd.Shared.Entities.Expense;


@RestController
@RequestMapping("api/groups")
public class GroupController {

    @GetMapping("/")
    public ResponseEntity listGroups() {
        try {
            return new ResponseEntity(
                    DatabaseUtils.GetGroupList(Database.database.getConn()),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("{group}/expenses")
    public ResponseEntity addGroupExpenses(
            @PathVariable("group") String group,
            @RequestBody Expense expense) {
        boolean result;
        try {
            result = DatabaseUtils.AddExpenseToGroup(group, expense, Database.database.getConn());
        } catch (Exception e) {
            result = false;
        }

        if(result) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("{group}/expenses")
    public ResponseEntity listGroupExpenses(
            @PathVariable("group") String group
    ) {
        try {
            return new ResponseEntity(DatabaseUtils.GetExpenseListFromGroup(group, Database.database.getConn()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("{group}/{expense_id}")
    public ResponseEntity deleteGroupExpenses(
            @PathVariable("group") String group,
            @PathVariable("expense_id") Integer expense_id
    ) {
        boolean result;
        try {
            result = DatabaseUtils.DeleteExpenseFromGroup(group, expense_id, Database.database.getConn());
        } catch (Exception e) {
            result = false;
        }

        if(result) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
