package pt.isec.pd.Client.Logic;

import pt.isec.pd.Shared.AccessLevel;

public class ClientManager {
    private String url;
    private String token;
    private int targetGroup_id;
    private AccessLevel accessLevel;

    public ClientManager(String ip, int port) {
        this.url = "http://" + ip + ':' + port;
        this.accessLevel = AccessLevel.BEFORE_LOGIN;
    }

    public String getUrl() {
        return url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getTargetGroup_id() {
        return targetGroup_id;
    }

    public void setTargetGroup_id(int targetGroup_id) {
        this.targetGroup_id = targetGroup_id;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }
}
