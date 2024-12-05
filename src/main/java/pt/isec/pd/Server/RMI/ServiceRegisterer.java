package pt.isec.pd.Server.RMI;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class ServiceRegisterer {
    public static ArrayList<Registry> openRegistries = new ArrayList<>();
    public static boolean CreateRegistry(int port) {
        try {
            openRegistries.add(LocateRegistry.createRegistry(port));
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }



    public static boolean BindRegistrationToImplementation(String registration, Remote implementation) {
        try {
            Naming.bind(registration, implementation);
            return true;
        } catch (MalformedURLException | AlreadyBoundException | RemoteException e) {
            return false;
        }
    }

    public static boolean RebindRegistrationToImplementation(String registration, Remote implementation) {
        try {
            Naming.rebind(registration, implementation);
            return true;
        } catch (MalformedURLException | RemoteException e) {
            return false;
        }
    }
    //coisas para registar servicos, bind etc
}
