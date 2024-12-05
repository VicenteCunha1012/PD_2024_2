package pt.isec.pd.Client.Logic;

import pt.isec.pd.Shared.AccessLevel;

public class ClientManager {
    private String url;
    private String token;
    private String email;
    private String targetGroupName;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTargetGroupName() {
        return targetGroupName;
    }

    public void setTargetGroupName(String targetGroupName) {
        this.targetGroupName = targetGroupName;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }
}
