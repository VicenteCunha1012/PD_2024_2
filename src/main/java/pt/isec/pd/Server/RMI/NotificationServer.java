package pt.isec.pd.Server.RMI;

import pt.isec.pd.ObservableClient.RMIHelper.NotificationClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NotificationServer extends Remote {
    void addObserver(NotificationClient notificationClient) throws RemoteException;
    void removeObserver(NotificationClient notificationClient) throws RemoteException;
    void notifyObservers(String message) throws RemoteException;
}
