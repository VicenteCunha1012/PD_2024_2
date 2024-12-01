package pt.isec.pd.Server.RMI;

import pt.isec.pd.Shared.Entities.ListedGroup;
import pt.isec.pd.Shared.Entities.ListedUser;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GetAppInfo extends Remote {
    List<ListedUser> ListUsers() throws RemoteException;
    List<ListedGroup> ListGroups() throws RemoteException;

}
