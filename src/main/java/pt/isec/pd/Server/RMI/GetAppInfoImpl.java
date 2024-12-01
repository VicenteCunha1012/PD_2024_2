package pt.isec.pd.Server.RMI;

import pt.isec.pd.Server.Data.Database;
import pt.isec.pd.Server.Data.DatabaseUtils;
import pt.isec.pd.Shared.Entities.ListedGroup;
import pt.isec.pd.Shared.Entities.ListedUser;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class GetAppInfoImpl extends UnicastRemoteObject implements GetAppInfo {

    public GetAppInfoImpl() throws RemoteException {
        super();
    }

    @Override
    public List<ListedUser> ListUsers() throws RemoteException {
        try {
            return DatabaseUtils.GetUserList(Database.database.getConn());
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<ListedGroup> ListGroups() throws RemoteException {
        try {
            return DatabaseUtils.GetGroupList(Database.database.getConn());
        } catch (Exception e) {
            return List.of();
        }
    }
}
