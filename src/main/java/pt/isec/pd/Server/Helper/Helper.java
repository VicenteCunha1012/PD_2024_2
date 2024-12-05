package pt.isec.pd.Server.Helper;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import pt.isec.pd.Server.Data.PreparedStatementWrapper;
import pt.isec.pd.Server.RMI.UpdatableType;

public class Helper {
    public static ApplicationContext sbAppContext;
    public static void shutDown() {
        PreparedStatementWrapper.CloseAllPendingStatements();
        System.out.println("done");
        if(sbAppContext != null) {
            SpringApplication.exit(sbAppContext);
        }
        System.out.println("done");
        System.exit(0);
    }

    public static String BuildNotificationMessage(UpdatableType type, String username) {
        return switch(type) {
            case REGISTER -> String.format("O utilizador %s acabou de se registar", username);
            case LOGIN -> String.format("O utilizador %s acabou de entrar" , username);
            case EXPENSE_ADDED -> String.format("O utilizador %s acabou de introduzir uma despesa nova", username);
            case EXPENSE_DELETED -> String.format("O utilizador %s acabou de remover uma despesa", username);
        };
    }
}
