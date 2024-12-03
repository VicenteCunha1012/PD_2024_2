package pt.isec.pd.Server.Helper;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import pt.isec.pd.MainServer;
import pt.isec.pd.Server.Data.PreparedStatementWrapper;

public class ShutDown {
    public static ApplicationContext sbAppContext;
    public static void shutDown() {
        PreparedStatementWrapper.CloseAllPendingStatements();
        if(sbAppContext != null) {
            SpringApplication.exit(sbAppContext);
        }
        System.exit(1);
    }
}
