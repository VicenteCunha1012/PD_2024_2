package pt.isec.pd.ObservableClient.RMIHelper;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class NotificationClientImpl extends UnicastRemoteObject implements NotificationClient {
    public NotificationClientImpl() throws RemoteException {}

    @Override
    public void update(String message) throws RemoteException {
        System.out.println("\n" + message);
        System.out.print("Comando > ");
    }
}
