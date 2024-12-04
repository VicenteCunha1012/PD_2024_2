package pt.isec.pd.Server.RMI;

import pt.isec.pd.ObservableClient.RMIHelper.NotificationClient;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class NotificationServerImpl extends UnicastRemoteObject implements NotificationServer {
    public static NotificationServerImpl instance;
    private final ArrayList<NotificationClient> clients = new ArrayList<>();

    public NotificationServerImpl() throws RemoteException {}

    @Override
    public void addObserver(NotificationClient notificationClient) throws RemoteException {
        this.clients.add(notificationClient);
    }

    //later fazer o remove

    @Override
    public void notifyObservers(String message) throws RemoteException {
        for (NotificationClient notificationClient : clients) {
            try {
                notificationClient.update(message);
            } catch (RemoteException e) {}
        }
    }
}
