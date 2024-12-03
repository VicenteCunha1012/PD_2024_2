package pt.isec.pd.Server.Springboot.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import pt.isec.pd.Server.Data.Database;
import pt.isec.pd.Server.Data.DatabaseUtils;
import pt.isec.pd.Shared.Entities.Expense;


@RestController
@RequestMapping("api/groups")
public class GroupController {

    @GetMapping("")
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

    //criar despesa no grupo
    @PostMapping("{group}/expenses")
    public ResponseEntity addExpenseToGroup(
            @PathVariable("group") String group,
            @RequestBody Expense expense) {
        if(!DatabaseUtils.GroupExists(group, Database.database.getConn())) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        //se o grupo existir
        String userEmail = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSubject();
        if(!DatabaseUtils.IsUserInGroup(userEmail, group, Database.database.getConn())) {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
        //se o user tiver acesso ao grupo

        boolean result;
        try {
            result = DatabaseUtils.AddExpenseToGroup(userEmail, group, expense, Database.database.getConn());
        } catch (Exception e) {
            result = false;
        }

        if(result) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //receber a lista de despesas de um grupo
    @GetMapping("{group}/expenses")
    public ResponseEntity listGroupExpenses(
            @PathVariable("group") String group
    ) {
        if(!DatabaseUtils.GroupExists(group, Database.database.getConn())) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        //se o grupo existir
        String userEmail = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSubject();
        if(!DatabaseUtils.IsUserInGroup(userEmail, group, Database.database.getConn())) {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
        //se o user tiver acesso ao grupo
        try {
            return new ResponseEntity(DatabaseUtils.GetExpenseListFromGroup(group, Database.database.getConn()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //apagar a despesa de um grupo
    @DeleteMapping("{group}/{expense_id}")
    public ResponseEntity deleteGroupExpenses(
            @PathVariable("group") String group,
            @PathVariable("expense_id") Integer expense_id
    ) {
        if(!DatabaseUtils.GroupExists(group, Database.database.getConn())) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        //se o grupo existir
        if(!DatabaseUtils.ExpenseExists(group, expense_id, Database.database.getConn())) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        //se a despesa existir
        String userEmail = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSubject();
        if(!DatabaseUtils.IsUserInGroup(userEmail, group, Database.database.getConn())) {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
        //se o user tiver acesso ao grupo
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
