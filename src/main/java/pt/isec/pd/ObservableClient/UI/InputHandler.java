package pt.isec.pd.ObservableClient.UI;

import pt.isec.pd.Server.RMI.GetAppInfo;
import pt.isec.pd.Shared.Entities.ListedGroup;
import pt.isec.pd.Shared.Entities.ListedUser;

import java.rmi.RemoteException;

public class InputHandler {
    public static boolean HandleInput(String input, GetAppInfo getAppInfo) throws RemoteException {
        switch(input) {
            case "help":
                System.out.println("""
                        help: ja ca chegaste
                        listar utilizadores: executar função remota para listar utilizadores
                        listar grupos: executar função remota para listar grupos
                        sair: sair
                        """);
                return true;
            case "listar utilizadores":
                for(ListedUser user : getAppInfo.ListUsers()) {
                    System.out.println(user);
                }
                return true;
            case "listar grupos":
                for(ListedGroup group : getAppInfo.ListGroups()) {
                    System.out.println(group);
                }
                return true;
            case "sair":
                return false;
            default:
                System.out.println("Comando inválido");
                return true;
        }
    }
}
