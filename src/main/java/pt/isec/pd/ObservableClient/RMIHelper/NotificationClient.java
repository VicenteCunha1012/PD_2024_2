package pt.isec.pd.ObservableClient.RMIHelper;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NotificationClient extends Remote {
    void update(String message) throws RemoteException;

}
