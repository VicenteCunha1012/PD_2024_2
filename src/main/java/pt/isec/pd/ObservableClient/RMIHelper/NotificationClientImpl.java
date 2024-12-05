package pt.isec.pd.ObservableClient.RMIHelper;

import pt.isec.pd.MainObservableClient;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class NotificationClientImpl extends UnicastRemoteObject implements NotificationClient {
    public NotificationClientImpl() throws RemoteException {}

    @Override
    public void update(String message) throws RemoteException {
        if(message.equals("EXIT")) {
            System.out.println("A fechar o cliente.");
            MainObservableClient.keepRunning = false;
            return;
        }
        System.out.println("\n" + message);
        System.out.print("Comando > ");
    }
}
