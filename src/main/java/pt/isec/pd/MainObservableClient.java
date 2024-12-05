package pt.isec.pd;

import pt.isec.pd.ObservableClient.RMIHelper.NotificationClient;
import pt.isec.pd.ObservableClient.RMIHelper.NotificationClientImpl;
import pt.isec.pd.ObservableClient.RMIHelper.RMIHelper;
import pt.isec.pd.ObservableClient.UI.InputHandler;
import pt.isec.pd.Server.RMI.GetAppInfo;
import pt.isec.pd.Server.RMI.GetAppInfoImpl;
import pt.isec.pd.Server.RMI.NotificationServer;
import pt.isec.pd.Server.RMI.NotificationServerImpl;
import pt.isec.pd.Shared.IO;

import java.rmi.Remote;
import java.rmi.RemoteException;

import static pt.isec.pd.ObservableClient.RMIHelper.RMIHelper.GetRemoteReference;

public class MainObservableClient {
    //ligar ao servico
    //ver se da pra fazer so com um thread
    //se nao é a vida
    public static boolean keepRunning = true;
    public static void main(String[] args) {
        if(args.length != 2) {
            System.out.println("Sintaxe java MainObservableClient get_app_info_rmi_uri update_server_rmi");
        }
        GetAppInfo remoteAppInfoImpl = RMIHelper.GetRemoteReference(args[0]);
        NotificationServer remoteNotiServerImpl = RMIHelper.GetRemoteReference(args[1]);
        NotificationClientImpl notiClient = null;
        try {
            notiClient = new NotificationClientImpl();
        } catch (RemoteException e) {
            System.exit(2);
        }

        if(remoteAppInfoImpl == null || remoteNotiServerImpl == null) {
            System.out.println("Ocorreu um erro a obter a referência remota.");
            System.exit(1);
        } else {
            System.out.println("Conexão bem sucedida");
        }


        try {
            remoteNotiServerImpl.addObserver(notiClient);
        } catch (RemoteException e) {
            System.out.println("Não foi possível adicionar-me como observer ao servidor");
            System.exit(3);
        }

        String input;

        boolean keepRunningInput = true;
        while (keepRunning && keepRunningInput) {
            input = IO.readString("Comando > ", false);
            try {
                keepRunningInput = InputHandler.HandleInput(input, remoteAppInfoImpl);
            } catch (Exception e) {
                System.out.println("Ocorreu um erro a tentar correr uma função remota, verifique se o servidor está online ou tente reiniciar a aplicação.");
            }
        }
        try {
            remoteNotiServerImpl.removeObserver(notiClient);
        } catch (RemoteException e) {}

    }
}
