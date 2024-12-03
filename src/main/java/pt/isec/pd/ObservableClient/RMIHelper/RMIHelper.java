package pt.isec.pd.ObservableClient.RMIHelper;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class  RMIHelper {
     public static <T> T GetRemoteReference(String uri) {
        try {
            return (T) Naming.lookup(uri);
        } catch (MalformedURLException | NotBoundException | RemoteException e) {
            return null;
        }
    }
}
